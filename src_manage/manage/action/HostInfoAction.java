package manage.action;

import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.common.service.HostInfoService;
import m.system.exception.MException;

@ActionMeta(name="manageHostInfo")
public class HostInfoAction extends ManageAction {
	
	public ActionResult toList() throws MException{
		ActionResult result=new ActionResult("manage/hostInfo/hostInfoList");
		result.setList(getService(HostInfoService.class).getList());
		return result;
	}
	
	
	
	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}

}
