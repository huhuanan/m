package manage.util.page;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.system.exception.MException;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;
import manage.util.page.button.ParamMeta;
import manage.util.page.form.FormButtonMeta;
import manage.util.page.form.FormButtonMeta.FormButtonEvent;
import manage.util.page.form.FormButtonMeta.FormButtonMethod;
import manage.util.page.form.FormFieldMeta;
import manage.util.page.form.FormFieldMeta.FormFieldType;
import manage.util.page.form.FormOtherMeta;
import manage.util.page.form.FormRowMeta;

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
			List<Map<String,Object>> fm=new ArrayList<Map<String,Object>>();
			for(FormFieldMeta field : row.fields()){
				Map<String,Object> m=new HashMap<String, Object>();
				m.put("title", field.title());
				m.put("message", field.message());
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
				if(field.type()==FormFieldType.SELECT||field.type()==FormFieldType.CHECKBOX||field.type()==FormFieldType.RADIO||field.type()==FormFieldType.STEPS){
					if(!StringUtil.isSpace(field.querySelect().modelClass())||!StringUtil.isSpace(field.dictType())){
						m.put("selectData", new ArrayList<Map<String,Object>>());
						JSONMessage json=QueryMetaUtil.toSelectParam(field.querySelect(),field.dictType(),field.linkField());
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
}
