package manage.action;

import java.sql.SQLException;

import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.system.RuntimeData;
import m.system.exception.MException;
import m.system.util.JSONMessage;
import manage.model.AdminGroupPower;
import manage.run.ModuleInitRun;
import manage.service.AdminGroupPowerService;

@ActionMeta(name="manageAdminGroupPower")
public class AdminGroupPowerAction extends ManageAction {
	private AdminGroupPower model;
	private String adminOid;
	public ActionResult setAdminGroupPowerPage() throws SQLException, MException{
		ActionResult result=new ActionResult("manage/adminGroupPower/setAdminGroupPowerPage");
		result.setArray(ModuleInitRun.getPowerList());
		result.setPower(getAdminOperPower(model.getAdminGroup()));
		result.setModel(model);
		return result;
	}
	public JSONMessage getPower4Admin() {
		JSONMessage message=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			message.push("powers", getService(AdminGroupPowerService.class).getPowerTitleList(adminOid));
			message.push("code", 0);
		} catch (Exception e) {
			message.push("code", 1);
			message.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}

	public JSONMessage addAdminGroupPower(){
		JSONMessage message=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			getService(AdminGroupPowerService.class).addAdminGroupPower(model);
			message.push("code", 0);
			message.push("msg", "添加权限成功!");
		} catch (Exception e) {
			message.push("code", 1);
			message.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}
	public JSONMessage removeAdminGroupPower(){
		JSONMessage message=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			getService(AdminGroupPowerService.class).removeAdminGroupPower(model);
			message.push("code", 0);
			message.push("msg", "移除权限成功!");
		} catch (Exception e) {
			message.push("code", 1);
			message.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}
	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}

	public String getAdminOid() {
		return adminOid;
	}
	public void setAdminOid(String adminOid) {
		this.adminOid = adminOid;
	}
	public AdminGroupPower getModel() {
		return model;
	}

	public void setModel(AdminGroupPower model) {
		this.model = model;
	}

}
