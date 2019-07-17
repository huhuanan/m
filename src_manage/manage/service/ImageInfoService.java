package manage.service;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import m.common.model.util.ModelQueryList;
import m.common.model.util.ModelQueryUtil;
import m.common.model.util.ModelUpdateUtil;
import m.common.model.util.QueryCondition;
import m.common.model.util.QueryOrder;
import m.common.model.util.QueryPage;
import m.common.service.Service;
import m.system.RuntimeData;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.exception.MException;
import m.system.util.DateUtil;
import m.system.util.FileUtil;
import m.system.util.GenerateID;
import m.system.util.StringUtil;
import manage.model.ImageAdmin;
import manage.model.ImageInfo;
import manage.model.ImageLink;

public class ImageInfoService extends Service {
	/**
	 * 获取图片缩略图路径
	 * @param oid
	 * @return
	 */
	public static String getThumPath(String oid){
		try {
			if(StringUtil.isSpace(oid))return "";
			if(oid.indexOf("\"")==0)oid=oid.substring(1);
			if(oid.lastIndexOf("\"")==oid.length()-1)oid=oid.substring(0,oid.length()-1);
			DataRow dr = DBManager.queryFirstRow("select thum_path from os_image_info where oid=?",new String[]{oid});
			if(null!=dr){
				return RuntimeData.getStaticDomain()+dr.get(String.class, "thum_path");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 保存图片
	 * @param model
	 * @param file
	 * @throws Exception
	 */
	public void saveImage(ImageInfo model, File file) throws Exception {
		String key=GenerateID.generatePrimaryKey();
		StringBuffer fPath=new StringBuffer(RuntimeData.getFilePath()).append("image/")
			.append(DateUtil.format(new Date(),"yyyyMM")).append("/").append(DateUtil.format(new Date(),"ddHH")).append("/");
		String fName=new StringBuffer(key).append(".jpg").toString();
		//
		model.setImgPath(new StringBuffer(fPath).append("image").append(fName).toString());
		model.setThumPath(new StringBuffer(fPath).append("thum").append(fName).toString());
		FileUtil.saveImgFile(file, model.getImgPath());
		FileUtil.saveThumFile(file, model.getThumPath(), model.getThumWidth().intValue(),new Double(model.getThumWidth()/model.getThumRatio()).intValue());
		model.setOid(key);
		model.setCreateDate(new Date());
		ModelUpdateUtil.insertModel(model);
		//将上传的文件同步到主控服务器上
//		sendFile2Server("", new String[]{model.getImgPath(),model.getThumPath()});
	}
//	/**
//	 * 将上传的文件同步到主控服务器上
//	 * @param adminToken
//	 * @param paths
//	 * @throws Exception
//	 */
//	private void sendFile2Server(String adminToken,String[] paths) throws Exception{
//		try{//将上传的文件同步到主控服务器上
//			HostInfo host=RuntimeData.getHostInfo();
//			if(!(null==host||host.getMain()==1)){//不是主控服务器
//				StringBuffer url=new StringBuffer("http://").append(RuntimeData.getServerIp()).append(":80/action/manageImageInfo/synchFile?adminToken=").append(adminToken);
//				List<String> ps=new ArrayList<String>();
//				List<String> ns=new ArrayList<String>();
//				List<File> files=new ArrayList<File>();
//				for(String path : paths){
//					File f=FileUtil.getWebFile(path);
//					files.add(f);
//					ps.add(URLEncoder.encode(path,"UTF-8"));
//					ns.add(URLEncoder.encode(f.getName(),"UTF-8"));
//				}
//				url.append("&synchPath[]=").append(ArrayUtil.connection(ps.toArray(new String[]{}), ","));
//				url.append("&synchName[]=").append(ArrayUtil.connection(ns.toArray(new String[]{}), ","));
//				new HttpRequestUtil().doUpload(url.toString(), files.toArray(new File[]{}));
//				//System.out.println("同步完成!");
//			}else{
//				//System.out.println("主控服务器,不用同步!");
//			}
//		}catch(Exception e){
//			//System.out.println("文件同步失败:"+e.getMessage());
//		}
//	}
//	/**
//	 * 保存图片
//	 * @param path
//	 * @param file
//	 */
//	public void saveSynchFile(String path,File file){
//		try {
//			if(!FileUtil.existsWebFile(path)){
//				FileUtil.writeWebFile(path,new FileInputStream(file));
//			}
//		} catch (FileNotFoundException e) {
//			//System.out.println("文件同步失败:"+e.getMessage());
//		}
//	}

	/**
	 * 保存并关联图片
	 * @param model
	 * @param file
	 * @param businessOid
	 * @throws Exception
	 */
	public void saveImageAndSelect(ImageInfo model, File file, String businessOid) throws Exception {
		saveImage(model,file);
		ImageLinkService.addImageLink(businessOid, "图片列表", model.getOid());
	}
	/**
	 * 返回未使用或者已使用的图片列表 
	 * @param admin
	 * @param page
	 * @param imageType
	 * @param isUsed
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public List<ImageInfo> getImageList(ImageAdmin admin,QueryPage page,String imageType,boolean isUsed) throws Exception{
		QueryCondition condition=QueryCondition.in("oid", ModelQueryList.instance(ImageLink.class, new String[]{"imageInfo.oid"}, null,
			QueryCondition.or(new QueryCondition[]{
				QueryCondition.eq("imageAdmin.oid", admin.getOid()),
				QueryCondition.isEmpty("imageAdmin.oid")
			})
		));
		if(!isUsed){
			condition=QueryCondition.not(condition);
		}
		ModelQueryList util = ModelQueryList.instance(ImageInfo.class, new String[]{"oid","thumPath","imgPath"}, page, 
			QueryCondition.and(new QueryCondition[]{
				QueryCondition.or(new QueryCondition[]{
					QueryCondition.eq("imageAdmin.oid", admin.getOid()),
					QueryCondition.isEmpty("imageAdmin.oid")
				}),
				QueryCondition.and(new QueryCondition[]{condition,QueryCondition.eq("imageType", imageType)})
			}), 
			QueryOrder.desc("createDate"));
		return ModelQueryList.getModelList(util.setUseStaticField(false));
	}
	/**
	 * 返回业务对应的图片列表
	 * @param businessOid
	 * @param page
	 * @param imageType
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public List<ImageInfo> getImageList(String businessOid,QueryPage page,String imageType) throws Exception{
		QueryCondition condition=QueryCondition.in("oid", ModelQueryList.instance(ImageLink.class, new String[]{"imageInfo.oid"}, null,QueryCondition.eq("businessOid", businessOid)));
		return ModelQueryList.getModelList(ImageInfo.class, new String[]{"oid","thumPath","imgPath"}, page, 
			QueryCondition.and(new QueryCondition[]{
				QueryCondition.and(new QueryCondition[]{condition,QueryCondition.eq("imageType", imageType)})
			}), 
			QueryOrder.desc("createDate"));
	}
	/**
	 * 根据oid数组获取图片
	 * @param oids
	 * @param page
	 * @param order
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public List<ImageInfo> getImageList(String[] oids,QueryPage page,QueryOrder order) throws Exception{
		return getImageList(oids, page, order, true);
	}
	/**
	 * 根据oid数组获取图片
	 * @param oids
	 * @param page
	 * @param order
	 * @param isStatic 是否使用静态
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public List<ImageInfo> getImageList(String[] oids,QueryPage page,QueryOrder order,boolean isStatic) throws Exception{
		List<QueryCondition> condList=new ArrayList<QueryCondition>();
		for(String oid : oids){
			condList.add(QueryCondition.eq("oid", oid));
		}
		ModelQueryList util=ModelQueryList.instance(ImageInfo.class, new String[]{"oid","thumPath","imgPath"}, page, 
			QueryCondition.or(condList.toArray(new QueryCondition[]{})), 
			order);
		util.setUseStaticField(isStatic);
		return ModelQueryList.getModelList(util);
	}
	/**
	 * 根据图片oid, 获取图片html标签
	 * @param oids
	 * @return
	 * @throws Exception
	 */
	public static String getImageDetails(String[] oids) throws Exception {
		List<ImageInfo> list=getService(ImageInfoService.class).getImageList(oids, new QueryPage(0,oids.length), QueryOrder.asc("createDate"),false);
		StringBuffer sb=new StringBuffer();
		for(ImageInfo image : list) {
			sb.append("<img src=\"").append(image.getImgPath()).append("\">");
		}
		return sb.toString();
	}
	/**
	 * 拷贝图片到另一个用户下
	 * @param oid
	 * @param adminOid
	 * @param imageType
	 * @return
	 * @throws Exception
	 */
	public static String copyImage(String oid,String adminOid,String imageType) throws Exception {
		ImageInfo image=new ImageInfo();
		image.setOid(oid);
		image=ModelQueryList.getModel(image, new String[] {"*"},false);
		image.setOid("");
		ImageAdmin admin=new ImageAdmin();
		admin.setOid(adminOid);
		image.setImageAdmin(admin);
		image.setImageType(imageType);
		getService(ImageInfoService.class).saveImage(image, FileUtil.getWebFile(image.getImgPath()));
		return image.getOid();
	}
	/**
	 * 拷贝图片到另一个用户下
	 * @param oids
	 * @param admin
	 * @param imageType
	 * @return
	 * @throws Exception
	 */
	public static String[] copyImageList(String[] oids,String adminOid,String imageType) throws Exception {
		List<String> newList=new ArrayList<String>();
		for(String oid : oids) {
			newList.add(copyImage(oid,adminOid,imageType));
		}
		return newList.toArray(new String[] {});
	}
	/**
	 * 删除未使用的图片
	 * @param admin
	 * @param imageOid
	 * @return
	 * @throws Exception
	 */
	public String delete(ImageAdmin admin, String imageOid, String businessOid) throws Exception {
		ImageInfo ii=new ImageInfo();
		ii.setOid(imageOid);
		ii=ModelQueryUtil.getModel(ii);
		if(ii.getImageAdmin().getOid().equals(admin.getOid())){
			if(!StringUtil.isSpace(businessOid)) ImageLinkService.removeImageLink(businessOid, imageOid);
			if(ImageLinkService.hasImageLink(imageOid)){
				throw new MException(this.getClass(),"已被使用的图片不能删除");
			}
			DataRow dr=DBManager.queryFirstRow("select img_path,thum_path,path from os_image_info where oid=?",new String[]{imageOid});
			if(!StringUtil.isSpace(dr.get(String.class, "img_path"))) FileUtil.deleteWebFile(dr.get(String.class, "img_path"));
			if(!StringUtil.isSpace(dr.get(String.class, "thum_path"))) FileUtil.deleteWebFile(dr.get(String.class, "thum_path"));
			if(!StringUtil.isSpace(dr.get(String.class, "path"))) FileUtil.deleteWebFile(dr.get(String.class, "path"));
			ModelUpdateUtil.deleteModel(ii);
			return imageOid;
		}else{
			throw new MException(this.getClass(),"不能删除不属于自己的图片");
		}
	}
	/**
	 * 取消业务关联并删除图片
	 * @param admin
	 * @param imageOid
	 * @param businessOid
	 * @return
	 * @throws Exception
	 */
	public String deleteBusiness(ImageAdmin admin, String imageOid,String businessOid) throws Exception{
		return delete(admin,imageOid,businessOid);
	}
}
