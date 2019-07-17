package manage.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import m.common.model.util.ModelQueryList;
import m.common.model.util.ModelQueryUtil;
import m.common.model.util.ModelUpdateUtil;
import m.common.model.util.QueryCondition;
import m.common.model.util.QueryOrder;
import m.common.service.Service;
import m.system.RuntimeData;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.exception.MException;
import m.system.util.GenerateID;
import m.system.util.StringUtil;
import manage.model.ImageInfo;
import manage.model.ImageLink;

public class ImageLinkService extends Service {
	public static boolean hasImageLink(String imageOid) throws SQLException{
		DataRow dr=DBManager.queryFirstRow("SELECT oid FROM os_image_link where image_info_oid=?", new String[]{imageOid});
		if(null==dr){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 添加业务对应图片关联
	 */
	public static void addImageLink(String businessOid,String business,String imageOid) throws Exception{
		DataRow dr=DBManager.queryFirstRow("SELECT oid FROM os_image_link where business_oid=? and business=? and image_info_oid=?", new String[]{businessOid,business,imageOid});
		if(null==dr){
			ImageInfo ii=new ImageInfo();
			ii.setOid(imageOid);
			ii=ModelQueryUtil.getModel(ii);
			ImageLink il=new ImageLink();
			il.setOid(GenerateID.generatePrimaryKey());
			il.setBusinessOid(businessOid);
			il.setBusiness(business);
			il.setImageInfo(ii);
			il.setImageAdmin(ii.getImageAdmin());
			ModelUpdateUtil.insertModel(il);
		}
	}
	/**
	 * 添加业务对应图片唯一关联
	 */
	public static void addOnlyImageLink(String businessOid,String business,String imageOid) throws Exception{
		removeAllImageLink(businessOid, business);
		addImageLink(businessOid,business,imageOid);
	}
	/**
	 * 移除业务对应图片关联
	 */
	public static void removeImageLink(String businessOid,String business,String imageOid) throws Exception{
		DBManager.executeUpdate("delete FROM os_image_link where business_oid=? and business=? and image_info_oid=?", new String[]{businessOid,business,imageOid});
	}
	protected static void removeImageLink(String businessOid,String imageOid) throws Exception{
		DBManager.executeUpdate("delete FROM os_image_link where business_oid=? and image_info_oid=?", new String[]{businessOid,imageOid});
	}
	/**
	 * 移除业务对应的主键关联
	 */
	public static void removeAllImageLink(String businessOid,String business) throws Exception{
		DBManager.executeUpdate("delete FROM os_image_link where business_oid=? and business=?", new String[]{businessOid,business});
	}
	/**
	 * 获取业务对于的所有图片
	 */
	public static List<ImageInfo> getImageLinkList(String businessOid,String business) throws SQLException, MException{
		List<ImageLink> list=ModelQueryList.getModelList(ImageLink.class, 
			new String[]{"imageInfo.oid","imageInfo.imageType","imageInfo.path","imageInfo.imgPath","imageInfo.thumPath","imageInfo.createDate",
			"imageInfo.imageAdmin.oid"}, null, 
			QueryCondition.and(new QueryCondition[]{QueryCondition.eq("businessOid", businessOid),QueryCondition.eq("business", business)}), QueryOrder.desc("imageInfo.createDate")
		);
		List<ImageInfo> newList=new ArrayList<ImageInfo>();
		for(ImageLink link : list){
			newList.add(link.getImageInfo());
		}
		return newList;
	}
	/**
	 * 拷贝图片关联
	 * @param oldBusinessOid
	 * @param newBusinessOid
	 * @param business
	 * @throws Exception
	 */
	public static void copyImageLinkList(String oldBusinessOid,String newBusinessOid,String imageType) throws Exception{
		List<ImageInfo> list=getImageLinkList(oldBusinessOid, "图片列表");
		for(ImageInfo img : list){
			if(imageType.equals(img.getImageType()))
				addImageLink(newBusinessOid,"图片列表",img.getOid());
		}
	}

	/**
	 * 添加业务对应图片唯一关联 
	 * @param businessOid
	 * @param business
	 * @param content 内容中包含的图片
	 * @throws Exception
	 */
	public static void addAllOnlyImageLink(String businessOid,String business,String content) throws Exception {
		removeAllImageLink(businessOid, business);
		Pattern p = Pattern.compile(new StringBuffer("src=\"").append(RuntimeData.getFilePath()).append(".*?\"").toString());
		Matcher m = p.matcher(content);
		Set<String> strs = new HashSet<String>();
		String line;
		while (m.find()) {
			line=m.group();
			strs.add(line.substring(5,line.length()-1));
		}
		if(strs.size()>0) {
			List<ImageInfo> list=ModelQueryList.getModelList(ImageInfo.class, new String[] {"oid"}, null, QueryCondition.in("imgPath", strs.toArray(new String[] {})));
			for(ImageInfo image : list) {
				addImageLink(businessOid,business,image.getOid());
			}
		}
	}
	public static String convertStaticPath(String content) {
		String sd=RuntimeData.getStaticDomain();
		if(!StringUtil.isSpace(sd)) {
			Pattern p = Pattern.compile(new StringBuffer("src=\"").append(RuntimeData.getFilePath()).append(".*?\"").toString());
			Matcher m = p.matcher(content);
			Set<String> strs = new HashSet<String>();
			String line;
			while (m.find()) {
				line=m.group();
				strs.add(line.substring(5,line.length()-1));
			}
			if(strs.size()>0) {
				for(String s : strs) {
					content=content.replaceAll(s, new StringBuffer(sd).append(s).toString());
				}
			}
		}
		return content;
	}
}
