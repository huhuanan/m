package manage.util.page;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.common.model.util.QueryCondition;
import m.system.exception.MException;
import m.system.util.DateUtil;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;
import manage.util.page.query.LinkFieldMeta;
import manage.util.page.query.QueryMeta;
import manage.util.page.query.QueryMeta.QueryType;
import manage.util.page.query.QuerySelectMeta;
import manage.util.page.query.SelectConditionMeta;
import manage.util.page.query.SelectDataMeta;

public class QueryMetaUtil {
	/**
	 * 查询条件list对象
	 * @param qms
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws MException
	 */
	public static List<Map<String,Object>> toList(QueryMeta[] qms) throws ClassNotFoundException, SQLException, MException{
		List<Map<String,Object>> querys=new ArrayList<Map<String,Object>>();
		for(QueryMeta qm : qms){
			Map<String,Object> query=new HashMap<String,Object>();
			query.put("field", qm.field());
			query.put("name", qm.name());
			query.put("hint", qm.hint());
			query.put("type", qm.type());
			query.put("value", qm.value());
			query.put("width", qm.width());
			query.put("clearField", qm.clearField());
			if(qm.type()==QueryType.SELECT||qm.type()==QueryType.SELECT_NODE){
				if(!StringUtil.isSpace(qm.querySelect().modelClass())||!StringUtil.isSpace(qm.dictType())){
					query.put("selectParam",toSelectParam(qm.type().name(),qm.querySelect(),qm.dictType(),qm.linkField()));
				}else{
					query.put("selectData", getSelectData(qm.querySelectDatas()));
				}
			}else if(qm.type()==QueryType.DATE_RANGE){
				query.put("dateFormat", qm.dateFormat());
			}
			querys.add(query);
		}
		return querys;
	}
	/**
	 * 是否包含除隐藏域以外的查询条件
	 * @param qms
	 * @return
	 */
	public static boolean hasNoHiddenQuery(QueryMeta[] qms){
		for(QueryMeta qm : qms){
			if(qm.type()!=QueryType.HIDDEN){
				return true;
			}
		}
		return false;
	}
	/**
	 * 查询条件数据
	 * @param qs
	 * @param da
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws MException
	 */
	@SuppressWarnings("unchecked")
	public static List<String[]> getSelectData(SelectDataMeta[] da) throws ClassNotFoundException, SQLException, MException{
		List<String[]> data=new ArrayList<String[]>();
		for(SelectDataMeta d : da){
			data.add(new String[]{d.value(),d.title(),d.parentValue()});
		}
		return data;
	}
	/**
	 * 查询json
	 * @param querySelectMeta
	 * @param dictType
	 * @param linkFieldMeta
	 * @return
	 */
	public static JSONMessage toSelectParam(String type,QuerySelectMeta querySelectMeta,String dictType, LinkFieldMeta linkFieldMeta){
		JSONMessage json=new JSONMessage();
		json.push("modelClass", querySelectMeta.modelClass());
		json.push("title", querySelectMeta.title());
		json.push("titleExpression", querySelectMeta.titleExpression());
		json.push("value", querySelectMeta.value());
		json.push("sortField", querySelectMeta.sortField());
		json.push("conditions", toConditionParams(querySelectMeta.conditions()));
		if(!StringUtil.isSpace(linkFieldMeta.field())){
			json.push("linkField", linkFieldMeta.field());
			json.push("valueField", linkFieldMeta.valueField());
		}
		if(type==QueryType.SELECT_NODE.name()) {
			json.push("parentField", querySelectMeta.parentField());
			json.push("parentValue", querySelectMeta.parentValue());
		}
		json.push("dictType", dictType);
		json.push("session", querySelectMeta.session().field());
		return json;
	}
	private static JSONMessage[] toConditionParams(SelectConditionMeta[] conditions){
		JSONMessage[] js=new JSONMessage[conditions.length];
		for(int i=0;i<conditions.length;i++){
			js[i]=new JSONMessage();
			js[i].push("field", conditions[i].field());
			js[i].push("value", conditions[i].value());
			js[i].push("type", conditions[i].type());
		}
		return js;
	}
	/**
	 * 转换成查询条件
	 * @param qms
	 * @param params
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public static List<QueryCondition> convertQuery(Map<String,String> params,QueryMeta[] querys) throws ClassNotFoundException{
		List<QueryCondition> list=new ArrayList<QueryCondition>();
		for(QueryMeta qm : querys){
			if(qm.type()==QueryType.DATE_RANGE){
				String paramDown=params.get(new StringBuffer().append(qm.field()).append("down").toString());
				String paramUp=params.get(new StringBuffer().append(qm.field()).append("up").toString());
				if(!StringUtil.isSpace(paramDown)){
					Date d=qm.dateFormat().indexOf("H")==-1?DateUtil.getStartDay(DateUtil.format(paramDown, qm.dateFormat())):DateUtil.format(paramDown, qm.dateFormat());
					list.add(QueryCondition.ge(qm.field(), d));
				}
				if(!StringUtil.isSpace(paramUp)){
					Date d=qm.dateFormat().indexOf("H")==-1?DateUtil.getEndDay(DateUtil.format(paramUp, qm.dateFormat())):DateUtil.format(paramUp, qm.dateFormat());
					list.add(QueryCondition.le(qm.field(), d));
				}
			}else if(qm.type()==QueryType.INT_RANGE){
				String paramDown=params.get(new StringBuffer().append(qm.field()).append("down").toString());
				String paramUp=params.get(new StringBuffer().append(qm.field()).append("up").toString());
				if(!StringUtil.isSpace(paramDown)){
					list.add(QueryCondition.ge(qm.field(), Integer.parseInt(paramDown)));
				}
				if(!StringUtil.isSpace(paramUp)){
					list.add(QueryCondition.le(qm.field(), Integer.parseInt(paramUp)));
				}
			}else if(qm.type()==QueryType.DOUBLE_RANGE){
				String paramDown=params.get(new StringBuffer().append(qm.field()).append("down").toString());
				String paramUp=params.get(new StringBuffer().append(qm.field()).append("up").toString());
				if(!StringUtil.isSpace(paramDown)){
					list.add(QueryCondition.ge(qm.field(), Double.parseDouble(paramDown)));
				}
				if(!StringUtil.isSpace(paramUp)){
					list.add(QueryCondition.le(qm.field(), Double.parseDouble(paramUp)));
				}
			}
			String param=params.get(qm.field());
			if(StringUtil.isSpace(param)){
				continue;
			}else if(qm.type()==QueryType.TEXT
					||qm.type()==QueryType.HIDDEN){
				if(qm.likeMode()){
					list.add(QueryCondition.like(qm.field(), param));
				}else{
					list.add(QueryCondition.eq(qm.field(), param));
				}
			}else if(qm.type()==QueryType.SELECT||qm.type()==QueryType.SELECT_NODE){
				if(qm.muchValue()){
					String[] vs=param.split(",");
					List<QueryCondition> subList=new ArrayList<QueryCondition>();
					for(String value : vs){
						if(StringUtil.isSpace(value)){
							subList.add(QueryCondition.isEmpty(qm.field()));
						}else{
							subList.add(QueryCondition.eq(qm.field(), value));
						}
					}
					list.add(QueryCondition.or(subList.toArray(new QueryCondition[]{})));
				}else{
					list.add(QueryCondition.eq(qm.field(), param));
				}
			}
		}
		return list;
	}
	/**
	 * 转化成查询条件,
	 * @param searchText 空则返回为null
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static QueryCondition convertSearchQuery(String searchText,String searchField) throws ClassNotFoundException{
		if(StringUtil.isSpace(searchText)) return null;
		String[] fields=searchField.split(",");
		List<QueryCondition> list=new ArrayList<QueryCondition>();
		if(!StringUtil.isSpace(searchText)){
			searchText=searchText.trim();
			String[] st=searchText.split("\\s+");
			for(String field : fields){
				if(!StringUtil.isSpace(field)){
					list.addAll(QueryCondition.manyLike(field, st));
				}
			}
		}
		return QueryCondition.or(list.toArray(new QueryCondition[]{}));
	}
}
