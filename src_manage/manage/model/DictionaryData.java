package manage.model;

import java.util.List;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;
import m.common.model.util.ModelQueryList;
import m.common.model.util.QueryCondition;
import m.common.model.util.QueryOrder;
import m.system.cache.FlushCacheList;

@TableMeta(name="os_dictionary_data",description="字典值表")
public class DictionaryData extends StatusModel implements FlushCacheList {

	@LinkTableMeta(name="dictionary_type_oid",table=DictionaryType.class,notnull=true,description="所属字典")
	private DictionaryType dictionaryType;
	
	@FieldMeta(name="name",type=FieldType.STRING,length=50,notnull=true,description="名称")
	private String name;
	@FieldMeta(name="value",type=FieldType.STRING,length=20,notnull=true,description="值")
	private String value;
	@FieldMeta(name="sort",type=FieldType.INT,description="排序")
	private Integer sort;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DictionaryType getDictionaryType() {
		return dictionaryType;
	}
	public void setDictionaryType(DictionaryType dictionaryType) {
		this.dictionaryType = dictionaryType;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	/**
	 * key 字典类型 
	 */
	public List<DictionaryData> getCacheList(String key) throws Exception {
		return ModelQueryList.getModelList(DictionaryData.class, 
			new String[]{"*"}, 
			null, 
			QueryCondition.and(new QueryCondition[]{
				QueryCondition.eq("dictionaryType.type",key),
				QueryCondition.eq("status", "0")
			}),
			QueryOrder.asc("sort")
		);
	}
}
