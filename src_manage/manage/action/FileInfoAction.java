package manage.action;

import java.io.File;
import java.util.Map;

import m.common.action.ActionMeta;
import m.system.RuntimeData;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;
import manage.model.AdminLogin;
import manage.model.FileInfo;
import manage.model.ImageAdmin;
import manage.service.FileInfoService;
import manage.service.ImageAdminService;

@ActionMeta(name="manageFileInfo")
public class FileInfoAction extends ManageAction {
	private String adminToken;
	private String field;
	private String type;
	private String path;
	
	/**
	 * 上传文件
	 * @return
	 */
	public JSONMessage upload(){
		JSONMessage message=new JSONMessage();
		try {
			FileInfo model=new FileInfo();
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
				String name=map.get(key).getName();
				model.setOid("");
				model.setType(type);
				model.setPath(path);
				model.setName(name.substring(name.indexOf("_")+1));
				model.setImageAdmin(ia);
				getService(FileInfoService.class).saveFile(model, map.get(key));
				message.push("model", model);
				message.push("field", field);
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

	public String getAdminToken() {
		return adminToken;
	}

	public void setAdminToken(String adminToken) {
		this.adminToken = adminToken;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	
	

}
