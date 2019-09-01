package manage.action;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.common.model.util.ModelQueryUtil;
import m.common.model.util.QueryOrder;
import m.common.model.util.QueryPage;
import m.system.RuntimeData;
import m.system.exception.MException;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;
import manage.model.AdminLogin;
import manage.model.ImageAdmin;
import manage.model.ImageInfo;
import manage.service.ImageAdminService;
import manage.service.ImageInfoService;

@ActionMeta(name="manageImageInfo")
public class ImageInfoAction extends ManageAction {
	private String adminToken;
	private Boolean isUsed;
	private String selected;
	private String field;
	private String imageType;
	private String businessOid;
	private String imageOid;
	private Double thumRatio=1.0;
	private Double thumWidth=500.0;
	private String[] synchPath;
	private String[] synchName;
	/**
	 * 接收非主控服务器同步过来的文件
	 * @return
	 * @throws MException 
	 */
//	public HtmlBodyContent synchFile() throws MException{
//		for(int i=0;i<synchName.length;i++){
//			getService(ImageInfoService.class).saveSynchFile(synchPath[i], super.getFileMap().get(synchName[i]));
//		}
//		return new HtmlBodyContent("success");
//	}
	
	/**
	 * 图片列表
	 * @return
	 */
	public ActionResult toImageList(){
		ActionResult result=new ActionResult("manage/image/imageList");
		return result;
	}
	/**
	 * 上传图片
	 * @return
	 */
	public JSONMessage uploadImage(){
		JSONMessage message=new JSONMessage();
		try {
			ImageInfo model=new ImageInfo();
			ImageAdmin ia=new ImageAdmin();
			if(StringUtil.isSpace(adminToken)||StringUtil.noSpace(adminToken).length()<3){
				AdminLogin admin=getSessionAdmin();
				if(null==admin) throw noLoginException;
				ia.setOid(admin.getOid());
			}else{
				ia.setOid(getService(ImageAdminService.class).getOid(adminToken));
			}
			Map<String,File> map=super.getFileMap();
			for(String key : map.keySet()){
				model.setOid("");
				model.setImageType(imageType);
				model.setImageAdmin(ia);
				model.setThumRatio(thumRatio);
				model.setThumWidth(thumWidth);
				getService(ImageInfoService.class).saveImage(model, map.get(key));
				message.push("model", model);
				JSONMessage data=new JSONMessage();
				data.push("src", model.getImgPath());
				data.push("title", "");
				message.push("data", data);
				break;
			}
			message.push("code", 0);
		} catch (Exception e) {
			message.push("code", 1);
			message.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}
	/**
	 * 选择图片数据
	 * @return
	 */
	public JSONMessage imageList(){
		JSONMessage message=new JSONMessage();
		try{
			ImageAdmin ia=new ImageAdmin();
			if(StringUtil.isSpace(adminToken)||StringUtil.noSpace(adminToken).length()<3){
				AdminLogin admin=getSessionAdmin();
				if(null==admin) throw noLoginException;
				ia.setOid(admin.getOid());
			}else{
				ia.setOid(getService(ImageAdminService.class).getOid(adminToken));
			}
			message.push("list", getService(ImageInfoService.class).getImageList(ia, getPage(),imageType, isUsed));
			message.push("code", 0);
		}catch(Exception e){
			message.push("msg", e.getMessage());
			message.push("code", 1);
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}
	/**
	 * 选择图片页面
	 * @return
	 */
	public ActionResult selectImagePage(){
		ActionResult result=new ActionResult("manage/image/imageList");
		List<String> arr=new ArrayList<String>();
		if(!StringUtil.isSpace(selected)){
			for(String oid : selected.split(",")){
				arr.add(oid);
			}
		}
		result.setArray(arr);
		result.setHtmlBody(imageType);
		result.setPower(adminToken);
		result.setMap(new HashMap<String, Object>());
		result.getMap().put("thumRatio", thumRatio);
		result.getMap().put("thumWidth", thumWidth);
		result.getMap().put("openKey", getOpenKey());
		result.getMap().put("field", field);
		return result;
	}
	/**
	 * 删除图片方法
	 * @return
	 */
	public JSONMessage delete(){
		JSONMessage message=new JSONMessage();
		try{
			ImageAdmin ia=new ImageAdmin();
			if(StringUtil.isSpace(adminToken)||StringUtil.noSpace(adminToken).length()<3){
				AdminLogin admin=getSessionAdmin();
				if(null==admin) throw noLoginException;
				ia.setOid(admin.getOid());
			}else{
				ia.setOid(getService(ImageAdminService.class).getOid(adminToken));
			}
			message.push("oid", getService(ImageInfoService.class).delete(ia, imageOid,businessOid));
			message.push("msg", "删除成功");
			message.push("code", 0);
		}catch(Exception e){
			message.push("msg", e.getMessage());
			message.push("code", 1);
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}
	public JSONMessage imageInfo(){
		JSONMessage message=new JSONMessage();
		try{
			ImageInfo image=new ImageInfo();
			image.setOid(imageOid);
			image=ModelQueryUtil.getModel(image);
			message.push("model", image);
			message.push("code", 0);
		}catch(Exception e){
			message.push("msg", e.getMessage());
			message.push("code", 1);
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
		
	}
	/**
	 * 查看图片
	 * @return
	 * @throws Exception
	 */
	public ActionResult viewImage() throws Exception{
		ActionResult result=new ActionResult("manage/image/viewImage");
		result.setModel(new ImageInfo());
		result.getModel().setOid(imageOid);
		result.setModel(ModelQueryUtil.getModel(result.getModel()));
		return result;
	}
	public ActionResult viewImages() throws Exception {
		ActionResult result=new ActionResult("manage/image/viewImages");
		String[] oids=imageOid.split(",");
		result.setList(getService(ImageInfoService.class).getImageList(oids, new QueryPage(0,oids.length), QueryOrder.asc("createDate")));
		return result;
	}
	/**
	 * 上传图片
	 * @return
	 */
	public JSONMessage uploadBusinessImage(){
		JSONMessage message=new JSONMessage();
		try {
			ImageInfo model=new ImageInfo();
			ImageAdmin ia=new ImageAdmin();
			if(StringUtil.isSpace(adminToken)||StringUtil.noSpace(adminToken).length()<3){
				AdminLogin admin=getSessionAdmin();
				if(null==admin) throw noLoginException;
				ia.setOid(admin.getOid());
			}else{
				ia.setOid(getService(ImageAdminService.class).getOid(adminToken));
			}
			if(StringUtil.isSpace(businessOid)) throw new MException(this.getClass(),"业务主键为空!");
			Map<String,File> map=super.getFileMap();
			for(String key : map.keySet()){
				model.setOid("");
				model.setImageType(imageType);
				model.setImageAdmin(ia);
				model.setThumRatio(thumRatio);
				model.setThumWidth(thumWidth);
				getService(ImageInfoService.class).saveImageAndSelect(model, map.get(key),businessOid);
				message.push("model", model);
				JSONMessage data=new JSONMessage();
				data.push("src", model.getImgPath());
				data.push("title", "");
				message.push("data", data);
			}
			message.push("code", 0);
		} catch (Exception e) {
			message.push("code", 1);
			message.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}
	/**
	 * 删除图片方法 业务列表
	 * @return
	 */
	public JSONMessage deleteBusinessImage(){
		JSONMessage message=new JSONMessage();
		try{
			ImageAdmin ia=new ImageAdmin();
			if(StringUtil.isSpace(adminToken)||StringUtil.noSpace(adminToken).length()<3){
				AdminLogin admin=getSessionAdmin();
				if(null==admin) throw noLoginException;
				ia.setOid(admin.getOid());
			}else{
				ia.setOid(getService(ImageAdminService.class).getOid(adminToken));
			}
			if(StringUtil.isSpace(businessOid)) throw new MException(this.getClass(),"业务主键为空!");
			message.push("oid", getService(ImageInfoService.class).deleteBusiness(ia,imageOid,businessOid));
			message.push("msg", "删除成功");
			message.push("code", 0);
		}catch(Exception e){
			message.push("msg", e.getMessage());
			message.push("code", 1);
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}
	/**
	 * 查看对应业务的图片列表
	 * @return
	 */
	public ActionResult toBusinessImageList(){
		ActionResult result=new ActionResult("manage/image/businessImageList");
		result.setMap(new HashMap<String,Object>());
		result.getMap().put("imageType", imageType);
		result.getMap().put("businessOid", businessOid);
		result.getMap().put("adminToken", adminToken);
		result.getMap().put("thumRatio", thumRatio);
		result.getMap().put("thumWidth", thumWidth);
		result.setPower(null!=getPower()&&getPower().toString().equals("view")?false:true);
		return result;
	}
	/**
	 * 查看对应业务的图片数据
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public JSONMessage businessImageList() throws SQLException, MException{
		JSONMessage message=new JSONMessage();
		try{
			message.push("list", getService(ImageInfoService.class).getImageList(this.getBusinessOid(), getPage(), getImageType()));
			message.push("code", 0);
		}catch(Exception e){
			message.push("msg", e.getMessage());
			message.push("code", 1);
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}

	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}

	public Boolean getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public String getImageOid() {
		return imageOid;
	}
	public void setImageOid(String imageOid) {
		this.imageOid = imageOid;
	}
	public String getAdminToken() {
		return adminToken;
	}
	public void setAdminToken(String adminToken) {
		this.adminToken = adminToken;
	}
	public String getImageType() {
		return imageType;
	}
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	public String getBusinessOid() {
		return businessOid;
	}
	public void setBusinessOid(String businessOid) {
		this.businessOid = businessOid;
	}
	public Double getThumRatio() {
		return thumRatio;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public void setThumRatio(Double thumRatio) {
		this.thumRatio = thumRatio;
	}
	public Double getThumWidth() {
		return thumWidth;
	}
	public void setThumWidth(Double thumWidth) {
		this.thumWidth = thumWidth;
	}

	public String[] getSynchPath() {
		return synchPath;
	}

	public void setSynchPath(String[] synchPath) {
		this.synchPath = synchPath;
	}

	public String[] getSynchName() {
		return synchName;
	}

	public void setSynchName(String[] synchName) {
		this.synchName = synchName;
	}



}
