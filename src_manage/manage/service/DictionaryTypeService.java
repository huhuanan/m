package manage.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import m.common.model.util.ModelQueryList;
import m.common.model.util.QueryCondition;
import m.common.model.util.QueryOrder;
import m.common.model.util.QueryPage;
import m.common.service.Service;
import m.system.exception.MException;
import m.system.util.StringUtil;
import manage.model.DictionaryData;
import manage.model.DictionaryType;

public class DictionaryTypeService extends Service {

	public List<DictionaryType> getDictionaryTypeList(String searchText,QueryPage page,QueryOrder order) throws SQLException, MException{
		List<QueryCondition> conditionList=new ArrayList<QueryCondition>();
		if(!StringUtil.isSpace(searchText)){
			searchText=searchText.trim();
			String[] st=searchText.split("\\s+");
			conditionList.addAll(QueryCondition.manyLike("name", st));
			conditionList.addAll(QueryCondition.manyLike("type", st));
		}
		return ModelQueryList.getModelList(DictionaryType.class,
			new String[]{"oid","name","type"},
			page,
			QueryCondition.or(conditionList.toArray(new QueryCondition[]{})),
			order
		);
	}

	public List<DictionaryData> getDictionaryDataList() throws SQLException, MException{
		List<QueryCondition> conditionList=new ArrayList<QueryCondition>();
		conditionList.add(QueryCondition.eq("dictionaryType.oid","101"));
		QueryCondition queryCondition=QueryCondition.and(new QueryCondition[]{
				QueryCondition.and(conditionList.toArray(new QueryCondition[]{}))
			});
		return ModelQueryList.getModelList(DictionaryData.class,
			new String[]{"oid","value"},
			null,
			queryCondition);
	}
}
