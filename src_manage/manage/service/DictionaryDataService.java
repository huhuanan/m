package manage.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import m.common.model.util.ModelCheckUtil;
import m.common.model.util.ModelQueryList;
import m.common.model.util.ModelUpdateUtil;
import m.common.model.util.QueryCondition;
import m.common.model.util.QueryOrder;
import m.common.model.util.QueryPage;
import m.common.service.Service;
import m.system.exception.MException;
import m.system.util.GenerateID;
import m.system.util.StringUtil;
import manage.model.DictionaryData;

public class DictionaryDataService extends Service {

	/**
	 * 获取全部区域, 供选择框使用
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public List<DictionaryData> getAllDictionaryData() throws SQLException, MException{
		return ModelQueryList.getModelList(DictionaryData.class, 
			new String[]{"oid","name"}, 
			null, 
			QueryCondition.eq("status", "0")
		);
	}
	public List<DictionaryData> getDictionaryDataList(String searchText,String dictionaryType_oid,QueryPage page,QueryOrder order) throws SQLException, MException{
		List<QueryCondition> conditionList=new ArrayList<QueryCondition>();
		if(!StringUtil.isSpace(searchText)){
			searchText=searchText.trim();
			String[] st=searchText.split("\\s+");
			conditionList.addAll(QueryCondition.manyLike("name", st));
			conditionList.addAll(QueryCondition.manyLike("value", st));
		}
		return ModelQueryList.getModelList(DictionaryData.class,
			new String[]{"oid","name","value","sort","status"},
			page,
			QueryCondition.and(new QueryCondition[]{
				QueryCondition.eq("dictionaryType.oid",dictionaryType_oid),
				QueryCondition.or(conditionList.toArray(new QueryCondition[]{}))
			}),
			order
		);
	}
	public void save(DictionaryData model) throws Exception {
		ModelCheckUtil.check(model);
		ModelCheckUtil.checkUniqueCombine(model, new String[]{"dictionaryType.oid","value"});
		if(model.getValue().indexOf(",")>=0){
			throw new MException(this.getClass(),"值不能包含英文逗号");
		}else if(model.getName().indexOf(",")>=0){
			throw new MException(this.getClass(),"名称不能包含英文逗号");
		}
		if(StringUtil.isSpace(model.getOid())){
			model.setOid(GenerateID.generatePrimaryKey());
			model.setStatus("0");
			ModelUpdateUtil.insertModel(model);
		}else{
			ModelUpdateUtil.updateModel(model,new String[]{"name","value","sort"});
		}
	}
}
