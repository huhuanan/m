package manage.action;

import m.system.RuntimeData;
import m.system.util.JSONMessage;
import manage.model.StatusModel;
import manage.service.StatusService;

public abstract class StatusAction extends ManageAction{

	public abstract StatusModel getModel();
	public abstract String getStatusPower();
	/**
	 * 恢复
	 * @return
	 */
	public JSONMessage doRecovery(){
		JSONMessage message=new JSONMessage();
		try {
			verifyAdminOperPower(getStatusPower());
			getService(StatusService.class).doRecovery(getModel());
			doRecoveryCallback();
			message.push("code", 0);
			message.push("msg", "已恢复!");
		} catch (Exception e) {
			message.push("code", 1);
			message.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}
	public void doRecoveryCallback(){ }
	/**
	 * 停用
	 * @return
	 */
	public JSONMessage doDisable(){
		JSONMessage message=new JSONMessage();
		try {
			verifyAdminOperPower(getStatusPower());
			getService(StatusService.class).doDisable(getModel());
			doDisableCallback();
			message.push("code", 0);
			message.push("msg", "已停用!");
		} catch (Exception e) {
			message.push("code", 1);
			message.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}
	public void doDisableCallback(){ }
}
