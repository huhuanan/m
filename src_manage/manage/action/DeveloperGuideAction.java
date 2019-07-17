package manage.action;

import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;

import m.common.action.Action;
import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.common.model.document.DocumentModelUtil;
import m.system.document.DocumentUtil;
import m.system.exception.MException;
@ActionMeta(name="manageDeveloperGuide")
public class DeveloperGuideAction extends Action {
	
	public ActionResult toIndex() throws MException{
		ActionResult result=new ActionResult("manage/developerGuide/index");
		result.setMap(new HashMap<String, Object>());
		result.getMap().put("actions",JSONObject.toJSONString(DocumentUtil.documentList()));
		result.getMap().put("models",JSONObject.toJSONString(DocumentModelUtil.documentList()));
		System.out.println(result.getMap().get("models"));
		return result;
	}
}
