package manage.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.common.model.Model;
import m.common.model.util.ModelQueryList;
import m.common.model.util.QueryCondition;
import m.common.model.util.QueryOrder;
import m.system.RuntimeData;
import m.system.exception.MException;
import m.system.util.ClassUtil;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;
import manage.model.AdminLogin;
import manage.model.DictionaryData;
import manage.util.page.query.SelectConditionMeta.SelectConditionType;
import manage.util.tag.DictionaryUtil;

@ActionMeta(name="managePageUtil")
public class PageUtilAction extends ManageAction {

	@Override
	public Class<? extends ManageAction> getActionClass() {
		return null;
	}

	private String field;
	//private String type;
	//private Boolean disabled;
	private String modelClass;
	private String title;
	private String titleExpression;
	private String value;
	private List<Map<String,String>> conditions;
	private String linkField;
	private String valueField;
	private String valueFieldValue;
	private String sortField;
	private String parentField;
	private String parentValue;
	private String dictType;
	private String session;
	
	
	public JSONMessage getSelectData(){
		JSONMessage msg=new JSONMessage();
		msg.push("field", field);
		//msg.push("type", type);
		//msg.push("disabled", disabled);
		List<JSONMessage> data=new ArrayList<JSONMessage>();
		try {
			AdminLogin admin=getSessionAdmin();
			if(null==admin) throw noLoginException;
			if(!StringUtil.isSpace(modelClass)){
				List<QueryCondition> clist=new ArrayList<QueryCondition>();
				if(null!=conditions){
					for(Map<String,String> qcm : conditions){
						String t=qcm.get("type");
						if(t.equals(SelectConditionType.LIKE.toString())){
							clist.add(QueryCondition.like(qcm.get("field"), qcm.get("value")));
						}else if(t.equals(SelectConditionType.EQ.toString())){
							clist.add(QueryCondition.eq(qcm.get("field"), qcm.get("value")));
						}else if(t.equals(SelectConditionType.IS_NULL.toString())){
							clist.add(QueryCondition.isEmpty(qcm.get("field")));
						}else if(t.equals(SelectConditionType.IS_NOT_NULL.toString())){
							clist.add(QueryCondition.not(QueryCondition.isNull(qcm.get("field"))));
						}else if(t.equals(SelectConditionType.IS_EMPTY.toString())) {
							clist.add(QueryCondition.isEmpty(qcm.get("field")));
						}else if(t.equals(SelectConditionType.IS_NOT_EMPTY.toString())) {
							clist.add(QueryCondition.not(QueryCondition.isEmpty(qcm.get("field"))));
						}else if(t.equals(SelectConditionType.GE.toString())){
							clist.add(QueryCondition.ge(qcm.get("field"), qcm.get("value")));
						}else if(t.equals(SelectConditionType.GT.toString())){
							clist.add(QueryCondition.gt(qcm.get("field"), qcm.get("value")));
						}else if(t.equals(SelectConditionType.LE.toString())){
							clist.add(QueryCondition.le(qcm.get("field"), qcm.get("value")));
						}else if(t.equals(SelectConditionType.LT.toString())){
							clist.add(QueryCondition.lt(qcm.get("field"), qcm.get("value")));
						}else if(t.equals(SelectConditionType.NOT_EQ)){
							clist.add(QueryCondition.not(QueryCondition.eq(qcm.get("field"), qcm.get("value"))));
						}
					}
				}
				if(!StringUtil.isSpace(linkField)){
					String[] farr=linkField.split("\\|");
					String[] varr=valueFieldValue.split("\\|");
					for(int i=0;i<farr.length;i++) {
						clist.add(QueryCondition.eq(farr[i], varr[i]));
					}
				}
				if(!StringUtil.isSpace(session)){
					clist.add(QueryCondition.eq(session, admin.getOid()));
				}
				Map<String,String> emap=new HashMap<String, String>();
				if(!StringUtil.isSpace(this.titleExpression)) {
					emap.put(this.title,this.titleExpression);
				}
				List<Model> list = ModelQueryList.getModelList(
					(Class<Model>)ClassUtil.getClass(modelClass), 
					new String[] {this.title,this.value,this.parentField}, 
					null, 
					QueryCondition.and(clist.toArray(new QueryCondition[]{})),
					emap,
					QueryOrder.asc(StringUtil.isSpace(sortField)?"oid":sortField)
				);
				if(StringUtil.isSpace(this.parentField)) {
					for(Model m : list){
						JSONMessage dm=new JSONMessage();
						dm.push("value", ClassUtil.getFieldValue(m, value));
						dm.push("label", ClassUtil.getFieldValue(m, title));
						data.add(dm);
					}
				}else {
					for(Model m : list){
						Object pv=ClassUtil.getFieldValue(m, parentField);
						if(StringUtil.isSpace(parentValue)&&(null==pv||StringUtil.isSpace(pv.toString()))
								||(!StringUtil.isSpace(parentValue))&&null!=pv&&StringUtil.noSpace(pv.toString()).equals(parentValue)) {
							JSONMessage dm=new JSONMessage();
							dm.push("value", ClassUtil.getFieldValue(m, value));
							dm.push("label", ClassUtil.getFieldValue(m, title));
							List<JSONMessage> arr=getChildren(dm.get("value").toString(), list);
							if(arr.size()>0) dm.push("children", arr);
							data.add(dm);
						}
					}
				}
			}else{
				List<DictionaryData> dictList=DictionaryUtil.get(dictType);
				for(DictionaryData dd : dictList){
					if(dd.getStatus().equals("0")){
						JSONMessage dm=new JSONMessage();
						dm.push("value", dd.getValue());
						dm.push("label", dd.getName());
						data.add(dm);
					}
				}
			}
		} catch (Exception e) {
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		msg.push("data",data);
		return msg;
	}
	private List<JSONMessage> getChildren(String parentValue,List<Model> list) throws MException {
		List<JSONMessage> ls=new ArrayList<JSONMessage>();
		for(Model m : list){
			Object pv=ClassUtil.getFieldValue(m, parentField);
			if(null!=pv&&pv.toString().equals(parentValue)) {
				JSONMessage dm=new JSONMessage();
				dm.push("value", ClassUtil.getFieldValue(m, value));
				dm.push("label", ClassUtil.getFieldValue(m, title));
				List<JSONMessage> arr=getChildren(dm.get("value").toString(), list);
				if(arr.size()>0) dm.push("children", arr);
				ls.add(dm);
			}
		}
		return ls;
	}


	private String selected;
	private Boolean edit;
	/**
	 * 选择地图页面
	 * @return
	 */
	public ActionResult selectMapPage(){
		ActionResult result=new ActionResult("manage/pageUtil/mapPage");
		result.setMap(new HashMap<String, Object>());
		result.getMap().put("selected", selected);
		result.getMap().put("openKey", getOpenKey());
		result.getMap().put("edit", edit);
		result.getMap().put("field", field);
		return result;
	}
	public String getModelClass() {
		return modelClass;
	}


	public void setModelClass(String modelClass) {
		this.modelClass = modelClass;
	}


	public String getSortField() {
		return sortField;
	}


	public Boolean getEdit() {
		return edit;
	}
	public void setEdit(Boolean edit) {
		this.edit = edit;
	}
	public void setSortField(String sortField) {
		this.sortField = sortField;
	}


	public String getTitle() {
		return title;
	}


	public String getParentField() {
		return parentField;
	}
	public void setParentField(String parentField) {
		this.parentField = parentField;
	}
	public String getParentValue() {
		return parentValue;
	}
	public void setParentValue(String parentValue) {
		this.parentValue = parentValue;
	}
	public void setTitle(String title) {
		this.title = title;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public List<Map<String, String>> getConditions() {
		return conditions;
	}


	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public void setConditions(List<Map<String, String>> conditions) {
		this.conditions = conditions;
	}


	public String getField() {
		return field;
	}


	public void setField(String field) {
		this.field = field;
	}


	public String getValueField() {
		return valueField;
	}


	public void setValueField(String valueField) {
		this.valueField = valueField;
	}


	public String getValueFieldValue() {
		return valueFieldValue;
	}


	public void setValueFieldValue(String valueFieldValue) {
		this.valueFieldValue = valueFieldValue;
	}


	public String getLinkField() {
		return linkField;
	}


	public void setLinkField(String linkField) {
		this.linkField = linkField;
	}


	public String getDictType() {
		return dictType;
	}


	public void setDictType(String dictType) {
		this.dictType = dictType;
	}


	public String getSession() {
		return session;
	}


	public String getTitleExpression() {
		return titleExpression;
	}
	public void setTitleExpression(String titleExpression) {
		this.titleExpression = titleExpression;
	}
	public void setSession(String session) {
		this.session = session;
	}



}
