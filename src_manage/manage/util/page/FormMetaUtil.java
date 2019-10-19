package manage.util.page;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import m.system.exception.MException;
import m.system.util.JSONMessage;
import m.system.util.ObjectUtil;
import m.system.util.StringUtil;
import manage.util.page.button.ParamMeta;
import manage.util.page.form.FormButtonMeta;
import manage.util.page.form.FormButtonMeta.FormButtonEvent;
import manage.util.page.form.FormButtonMeta.FormButtonMethod;
import manage.util.page.form.FormFieldMeta;
import manage.util.page.form.FormFieldMeta.FormFieldType;
import manage.util.page.form.FormOtherMeta;
import manage.util.page.form.FormRowMeta;
import manage.util.page.form.FormViewUIMeta;

public class FormMetaUtil {

	/**
	 * 转换对象
	 * @param rows
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws MException
	 */
	public static List<Map<String,Object>> toRows(FormRowMeta[] rows,Map<String,Boolean> powerMap) throws ClassNotFoundException, SQLException, MException {
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		for(FormRowMeta row : rows){
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("line", row.splitLine());
			map.put("title", row.title());
			map.put("tabs", row.tabs());
			map.put("endTabs", row.endTabs());
			map.put("tabTitle", row.tabTitle());
			map.put("minWidth", row.minWidth());
			map.put("marginRight", row.marginRight());
			if(!StringUtil.isSpace(row.alert().title())) {
				Map<String,Object> alert=new HashMap<String, Object>();
				alert.put("type", row.alert().type().toString());
				alert.put("icon", row.alert().icon());
				alert.put("title", StringUtil.isSpace(row.alert().title())?"":ObjectUtil.toString(row.alert().title()));
				alert.put("desc", StringUtil.isSpace(row.alert().desc())?"":ObjectUtil.toString(row.alert().desc()));
				map.put("alert", alert);
			}
			Object[] vui=toViewUI(row.viewui());
			map.put("vulist", vui[0]);
			map.put("vplist", vui[1]);
			List<Map<String,Object>> fm=new ArrayList<Map<String,Object>>();
			for(FormFieldMeta field : row.fields()){
				Map<String,Object> m=new HashMap<String, Object>();
				m.put("title", field.title());
				m.put("message", StringUtil.isSpace(field.message())?"":ObjectUtil.toString(field.message()));
				m.put("titleWidth", field.titleWidth());
				if(field.hideTitle()){
					m.put("title", field.required()?" ":"");
					m.put("titleWidth", field.required()?13:0);
				}
				m.put("required", field.required());
				m.put("type", field.type());
				m.put("field", field.field());
				m.put("disabled", field.disabled());
				m.put("span", field.span());
				m.put("alertSpan", field.alertSpan()==0?field.span():field.alertSpan());
				if(!StringUtil.isSpace(field.alert().title())) {
					Map<String,Object> alert=new HashMap<String, Object>();
					alert.put("type", field.alert().type().toString());
					alert.put("icon", field.alert().icon());
					alert.put("title", StringUtil.isSpace(field.alert().title())?"":ObjectUtil.toString(field.alert().title()));
					alert.put("desc", StringUtil.isSpace(field.alert().desc())?"":ObjectUtil.toString(field.alert().desc()));
					m.put("alert", alert);
				}
				vui=toViewUI(field.viewui());
				m.put("vulist", vui[0]);
				m.put("vplist", vui[1]);
				if(((List<String>)vui[0]).size()>0) {
					m.put("viewuiSpan", field.viewuiSpan()==0?field.span():field.viewuiSpan());
				}
				if(field.type()==FormFieldType.ALERT) {
					m.put("title", "");
					m.put("titleWidth", 0);
					m.put("required", false);
				}
				if(field.type()==FormFieldType.IMAGE||field.height()>=0){
					m.put("height", field.height());
				}
				if(field.type()==FormFieldType.FILE) {
					m.put("fileType", field.fileType());
					m.put("filePath", field.filePath());
				}
				if(field.type()==FormFieldType.TEXTAREA){
					m.put("rows", field.rows());
				}
				m.put("hint", field.hint());
				m.put("suffix", field.suffix());
				m.put("nullHidden", field.nullHidden());
				m.put("hiddenField", field.hiddenField());
				m.put("hiddenValues", field.hiddenValues());
				m.put("showField", field.showField());
				m.put("showValues", field.showValues());
				m.put("clearField", field.clearField());
				m.put("thumWidth", field.thumWidth());
				m.put("thumRatio", field.thumRatio());
				if(field.type()==FormFieldType.DATE||field.type()==FormFieldType.DATETIME){
					m.put("dateFormat", field.dateFormat());
				}
				if(field.type()==FormFieldType.DOUBLE||field.type()==FormFieldType.INT){
					m.put("decimalCount", field.decimalCount());
					if(field.type()==FormFieldType.INT){
						m.put("decimalCount", 0);
					}
					String[] nr=field.numberRange().split("~");
					try{
						m.put("numberDown",Double.parseDouble(nr[0]));
					}catch(Exception e){
					}
					try{
						m.put("numberUp",Double.parseDouble(nr[1]));
					}catch(Exception e){
					}
				}
				if(field.type()==FormFieldType.SELECT||field.type()==FormFieldType.SELECT_NODE||field.type()==FormFieldType.CHECKBOX||field.type()==FormFieldType.RADIO||field.type()==FormFieldType.STEPS||field.type()==FormFieldType.TEXTAUTO){
					if(!StringUtil.isSpace(field.querySelect().modelClass())||!StringUtil.isSpace(field.dictType())){
						m.put("selectData", new ArrayList<Map<String,Object>>());
						JSONMessage json=QueryMetaUtil.toSelectParam(field.type().name(),field.querySelect(),field.dictType(),field.linkField());
						json.push("disabled", field.disabled());
						m.put("selectParam", json);
					}else{
						m.put("selectData", QueryMetaUtil.getSelectData(field.querySelectDatas()));
					}
				}
				if(field.type()==FormFieldType.IMAGE){
					m.put("imageType", field.imageType());
				}
				if(field.type()==FormFieldType.BUTTON){
					m.put("buttons", toButtons(field.buttons(),powerMap));
				}
				fm.add(m);
			}
			map.put("fields", fm);
			List<Map<String,Object>> om=new ArrayList<Map<String,Object>>();
			for(FormOtherMeta other : row.others()){
				Map<String,Object> m=new HashMap<String, Object>();
				m.put("title", other.title());
				JSONMessage json=new JSONMessage();
				json.push("url", other.url());
				if(!StringUtil.isSpace(other.linkField().field())){
					json.push("field", other.linkField().field());
					json.push("valueField", other.linkField().valueField());
				}
				m.put("param", json);
				om.add(m);
			}
			map.put("others", om);
			list.add(map);
		}
		return list;
	}
	/**
	 * 转换对象
	 * @param buttons
	 * @return
	 */
	public static List<Map<String,Object>> toButtons(FormButtonMeta[] buttons,Map<String,Boolean> powerMap) {
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		for(FormButtonMeta button : buttons){
			if("".equals(button.power())||null!=powerMap.get(button.power())){
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("title", button.title());
				map.put("icon", ButtonMetaUtil.getIcon(button.title(),button.icon()));
				map.put("style", button.style().toString());
				map.put("operField", button.operField());
				map.put("operValues", button.operValues());
				JSONMessage json=new JSONMessage();
				json.push("confirm", button.confirm());
				json.push("url", button.url());
				json.push("success", button.success());
				json.push("event", button.event());
				if(button.event()==FormButtonEvent.MODAL){
					json.push("width",button.modalWidth());
				}
				json.push("method", button.method());
				if(button.method()==FormButtonMethod.PARAMS_SUBMIT){
					json.push("params", getParamsList(button.params()));
				}
				map.put("param", json);
				list.add(map);
			}
		}
		return list;
	}
	private static List<String[]> getParamsList(ParamMeta[] bs){
		List<String[]> list=new ArrayList<String[]>();
		for(ParamMeta b : bs){
			list.add(new String[]{b.name(),b.field(),b.value()});
		}
		return list;
	}
	public static List<Map<String,Object>> toOthers(FormOtherMeta[] others){
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		for(FormOtherMeta other : others){
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("title", other.title());
			JSONMessage json=new JSONMessage();
			json.push("url", other.url());
			if(!StringUtil.isSpace(other.linkField().field())){
				json.push("field", other.linkField().field());
				json.push("valueField", other.linkField().valueField());
			}
			map.put("param", json);
			list.add(map);
		}
		return list;
	}
	public static Object[] toViewUI(FormViewUIMeta[] viewui){
		Pattern pattern=Pattern.compile("\\#\\{.+?\\}");
		List<String> vulist=new ArrayList<String>();
		List<String> vplist=new ArrayList<String>();
		for(FormViewUIMeta ui : viewui) {
			String f=ui.template();
			Matcher matcher=pattern.matcher(f);
			while(matcher.find()){
				String str=matcher.group();
				f=f.replace(str, "fields['"+str.substring(2,str.length()-1)+"']");
			}
			vulist.add(f);
			for(String p : ui.fields()) {
				vplist.add(p);
			}
		}
		return new Object[] {vulist,vplist};
	}
}
