package manage.util.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.system.util.JSONMessage;
import m.system.util.StringUtil;
import manage.util.page.button.ButtonMeta;
import manage.util.page.button.ButtonMeta.ButtonEvent;
import manage.util.page.button.DropButtonMeta;
import manage.util.page.button.ParamMeta;
import manage.util.page.table.TableColLink;

public class ButtonMetaUtil {
	public static String getIcon(String title,String icon){
		if(StringUtil.isSpace(icon)){
			if("新增".equals(title)){
				return "&#xe6b9;";
			}else if("修改".equals(title)){
				return "&#xe69e;";
			}else if("删除".equals(title)){
				return "&#xe69d;";
			}else if("发布".equals(title)){
				return "&#xe828;";
			}else if("审核".equals(title)){
				return "&#xe6b1;";
			}else if("保存".equals(title)){
				return "&#xe747;";
			}else{
				return icon;
			}
		}else{
			return icon;
		}
	}
	public List<Map<String,Object>> toList(ButtonMeta[] bms,Map<String,Boolean> powerMap){
		List<Map<String,Object>> buttons=new ArrayList<Map<String,Object>>();
		for(ButtonMeta bm : bms){
			if("".equals(bm.power())||null!=powerMap.get(bm.power())&&powerMap.get(bm.power())){
				Map<String,Object> button=new HashMap<String,Object>();
				button.put("title", bm.title());
				button.put("icon", getIcon(bm.title(),bm.icon()));
				button.put("style", bm.style().toString());
//				button.put("disabledField", bm.disabledField().replaceAll("\\.", "_"));
//				button.put("disabledValues", bm.disabledValues());
				button.put("hiddenField", bm.hiddenField().replaceAll("\\.", "_"));
				button.put("hiddenValues", bm.hiddenValues());
				button.put("showField", bm.showField().replaceAll("\\.", "_"));
				button.put("showValues", bm.showValues());
				JSONMessage json=new JSONMessage();
				json.push("event", bm.event());
				json.push("confirm", bm.confirm());
				json.push("url", bm.url());
				json.push("success", bm.success());
				json.push("useOther", bm.useQueryParams());
				json.push("queryParams", getParamsList(bm.queryParams()));
				json.push("params", getParamsList(bm.params()));
				if(bm.event()==ButtonEvent.MODAL){
					json.push("width", bm.modalWidth());
				}
				button.put("param",json);
				buttons.add(button);
			}
		}
		return buttons;
	}
	public Map<String,Object> toPamams(TableColLink bm,Map<String,Boolean> powerMap) {
		if("".equals(bm.power())||null!=powerMap.get(bm.power())&&powerMap.get(bm.power())){
			Map<String,Object> link=new HashMap<String,Object>();
			link.put("hiddenField", bm.hiddenField().replaceAll("\\.", "_"));
			link.put("hiddenValues", bm.hiddenValues());
			link.put("showField", bm.showField().replaceAll("\\.", "_"));
			link.put("showValues", bm.showValues());
			JSONMessage json=new JSONMessage();
			json.push("event", bm.event());
			json.push("confirm", bm.confirm());
			json.push("url", bm.url());
			json.push("success", bm.success());
			json.push("useOther", bm.useQueryParams());
			json.push("queryParams", getParamsList(bm.queryParams()));
			json.push("params", getParamsList(bm.params()));
			if(bm.event()==ButtonEvent.MODAL){
				json.push("width", bm.modalWidth());
			}
			link.put("param",json);
			return link;
		}else{
			return null;
		}
	}
	private List<String[]> getParamsList(ParamMeta[] bs){
		List<String[]> list=new ArrayList<String[]>();
		for(ParamMeta b : bs){
			list.add(new String[]{b.name(),b.field(),b.value()});
		}
		return list;
	}
	

	public List<Map<String,Object>> toList(DropButtonMeta[] bms,Map<String,Boolean> powerMap){
		List<Map<String,Object>> dbs=new ArrayList<Map<String,Object>>();
		for(DropButtonMeta bm : bms) {
			if("".equals(bm.power())||null!=powerMap.get(bm.power())&&powerMap.get(bm.power())){
				Map<String,Object> button=new HashMap<String,Object>();
				button.put("title", bm.title());
				button.put("icon", getIcon(bm.title(),bm.icon()));
				button.put("style", bm.style().toString());
//				button.put("disabledField", bm.disabledField().replaceAll("\\.", "_"));
//				button.put("disabledValues", bm.disabledValues());
				button.put("hiddenField", bm.hiddenField().replaceAll("\\.", "_"));
				button.put("hiddenValues", bm.hiddenValues());
				button.put("showField", bm.showField().replaceAll("\\.", "_"));
				button.put("showValues", bm.showValues());
				button.put("buttons",toList(bm.buttons(),powerMap));
				dbs.add(button);
			}
		}
		return dbs;
	}
}
