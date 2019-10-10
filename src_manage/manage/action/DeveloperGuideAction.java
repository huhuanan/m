package manage.action;

import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;

import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.common.model.document.DocumentModelUtil;
import m.system.document.DocumentUtil;
@ActionMeta(name="manageDeveloperGuide")
public class DeveloperGuideAction extends ManageAction {
	
	public ActionResult toIndex() throws Exception{
		verifyAdminOperPower("manage_system_power");
		ActionResult result=new ActionResult("manage/developerGuide/index");
		result.setMap(new HashMap<String, Object>());
		result.getMap().put("actions",JSONObject.toJSONString(DocumentUtil.documentList()));
		result.getMap().put("models",JSONObject.toJSONString(DocumentModelUtil.documentList()));
		return result;
	}

	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}
}
