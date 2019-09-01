package manage.action;

import m.common.action.ActionMeta;
import m.system.RuntimeData;
import m.system.util.JSONMessage;
import manage.model.AdminGroupLink;
import manage.service.AdminGroupLinkService;

@ActionMeta(name="manageAdminGroupLink")
public class AdminGroupLinkAction extends ManageAction {
	public AdminGroupLink model;
	
	public String adminGroupOid;
	public String[] adminOids;
	
	public JSONMessage setLink() {
		setLogContent("关联", "关联角色");
		JSONMessage result=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			getService(AdminGroupLinkService.class).setLink(adminGroupOid,adminOids);
			result.push("code", 0);
			result.push("msg", "保存成功");
		} catch (Exception e) {
			result.push("code", 1);
			result.push("msg", e.getMessage());
			setLogError(e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return result;
	}
	public JSONMessage getLink() {
		JSONMessage result=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			result.push("map", getService(AdminGroupLinkService.class).getLink(adminGroupOid));
			result.push("code", 0);
		} catch (Exception e) {
			result.push("code", 1);
			result.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}
	public AdminGroupLink getModel() {
		return model;
	}
	public void setModel(AdminGroupLink model) {
		this.model = model;
	}

	public String getAdminGroupOid() {
		return adminGroupOid;
	}

	public void setAdminGroupOid(String adminGroupOid) {
		this.adminGroupOid = adminGroupOid;
	}

	public String[] getAdminOids() {
		return adminOids;
	}

	public void setAdminOids(String[] adminOids) {
		this.adminOids = adminOids;
	}

}
