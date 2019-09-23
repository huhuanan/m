package manage.action;

import java.io.File;
import java.util.Map;

import m.common.action.ActionMeta;
import m.system.RuntimeData;
import m.system.util.JSONMessage;
import manage.model.IconInfo;
import manage.service.IconInfoService;

@ActionMeta(name="manageIconInfo")
public class IconInfoAction extends ManageAction {

	/**
	 * 上传文件
	 * @return
	 */
	public JSONMessage upload(){
		JSONMessage message=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			Map<String,File> map=super.getFileMap();
			for(String key : map.keySet()){
				String name=map.get(key).getName();
				IconInfo model=new IconInfo();
				model.setName(name.substring(name.indexOf("_")+1));
				getService(IconInfoService.class).saveIcon(model,map.get(key));
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
	 * 获取icon列表
	 * @return
	 */
	public JSONMessage iconList(){
		JSONMessage message=new JSONMessage();
		try{
			verifyAdminOperPower("manage_system_power");
			message.push("list", getService(IconInfoService.class).getIconList(getPage()));
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

}
