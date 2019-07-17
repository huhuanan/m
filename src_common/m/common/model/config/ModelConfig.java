package m.common.model.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;

public class ModelConfig {
	private static String tableSchema;
	private static List<Class<? extends Model>> tableList;
	private static Map<Class<? extends Model>,TableMeta> tableMap;
	private static Map<Class<? extends Model>,Map<String,FieldMeta>> fieldMetaMap;
	private static Map<Class<? extends Model>,Map<String,LinkTableMeta>> linkTableMetaMap;
	//set
	public static void setTableSchema(String tableSchema) {
		ModelConfig.tableSchema = tableSchema;
	}
	public static <T extends Model> void fillModelInfo(Class<T> clazz,TableMeta tableMeta,
			Map<String,FieldMeta> fieldMap,Map<String,LinkTableMeta> linkTableMap){
		if(null==tableMap){
			tableList=new ArrayList<Class<? extends Model>>();
			tableMap=new HashMap<Class<? extends Model>, TableMeta>();
			fieldMetaMap=new HashMap<Class<? extends Model>, Map<String,FieldMeta>>();
			linkTableMetaMap=new HashMap<Class<? extends Model>, Map<String,LinkTableMeta>>();
		}
		tableList.add(clazz);
		tableMap.put(clazz, tableMeta);
		fieldMetaMap.put(clazz, fieldMap);
		linkTableMetaMap.put(clazz, linkTableMap);
	}
	//get
	public static <T extends Model> TableMeta getTableMeta(Class<T> clazz){
		if(null!=tableMap)
			return tableMap.get(clazz);
		else
			return null;
	}
	public static <T extends Model> Map<String,FieldMeta> getFieldMetaMap(Class<T> clazz){
		if(null!=fieldMetaMap)
			return fieldMetaMap.get(clazz);
		else
			return null;
	}
	public static <T extends Model> Map<String,LinkTableMeta> getLinkTableMetaMap(Class<T> clazz){
		if(null!=linkTableMetaMap)
			return linkTableMetaMap.get(clazz);
		else
			return null;
	}
	public static String getTableSchema() {
		return tableSchema;
	}
	public static List<Class<? extends Model>> getTableList() {
		return tableList;
	}
}
