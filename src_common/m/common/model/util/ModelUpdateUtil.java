package m.common.model.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.config.ModelConfig;
import m.system.db.DBManager;
import m.system.exception.MException;
import m.system.util.ArrayUtil;
import m.system.util.ClassUtil;
import m.system.util.StringUtil;

public class ModelUpdateUtil {
	/**
	 * 全字段插入数据库.
	 * @param <T>
	 * @param obj
	 * @throws MException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model> int insertModel(T obj) throws MException {
		if(StringUtil.isSpace(obj.getOid())){
			throw new MException(ModelQueryUtil.class,"主键值为空!");
		}
		// 获得对象的类型
		Class<T> clazz=(Class<T>) obj.getClass();
		TableMeta tableMeta=ModelConfig.getTableMeta(clazz);
		if(null==tableMeta) throw new MException(ModelQueryUtil.class,"没有对应的表!");
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		StringBuffer fieldString=new StringBuffer("oid");
		StringBuffer valueString=new StringBuffer("?");
		List<Object> valueParams=new ArrayList<Object>();
		List<Object> linkTableParams=new ArrayList<Object>();
		for(String key : fieldMap.keySet()){
			FieldMeta field=fieldMap.get(key);
			fieldString.append(",").append(field.name());
			valueString.append(",?");
			valueParams.add(ClassUtil.getFieldValue(obj, key));
		}
		for(String key : linkTableMap.keySet()){
			LinkTableMeta linkTable=linkTableMap.get(key);
			fieldString.append(",").append(linkTable.name());
			valueString.append(",?");
			Object bean=ClassUtil.getFieldValue(obj, key);
			if(null==bean){
				linkTableParams.add(null);
			}else{
				linkTableParams.add(ClassUtil.getFieldValue(bean, "oid"));
			}
		}
		StringBuffer sql=new StringBuffer("INSERT INTO ").append(tableMeta.name());
		sql.append("(").append(fieldString).append(") VALUES(").append(valueString).append(")");

		try {
			List<Object> paramList=new ArrayList<Object>();
			paramList.add(obj.getOid());
			for(int i=0;i<valueParams.size();i++){
				paramList.add(valueParams.get(i));
			}
			for(int i=0;i<linkTableParams.size();i++){
				paramList.add(linkTableParams.get(i));
			}
			return DBManager.executeUpdate(sql.toString(),paramList.toArray(new Object[]{}));
		} catch (SQLException e) {
			throw new MException(ModelUpdateUtil.class,"全量插入失败!"+e.getMessage());
		}
	}
	/**
	 * 全字段批量插入数据库
	 * @param <T>
	 * @param objs
	 * @throws MException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model> void insertModels(T[] objs) throws MException {
		if(objs.length<1) return;
		// 获得对象的类型
		Class<T> clazz=(Class<T>) objs[0].getClass();
		TableMeta tableMeta=ModelConfig.getTableMeta(clazz);
		if(null==tableMeta) throw new MException(ModelQueryUtil.class,"没有对应的表!");
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		StringBuffer fieldString=new StringBuffer("oid");
		StringBuffer valueString=new StringBuffer("?");
		List<FieldMeta> fieldParams=new ArrayList<FieldMeta>();
		List<List<Object>> valueParamsList=new ArrayList<List<Object>>();
		List<List<Object>> linkTableParamsList=new ArrayList<List<Object>>();
		for(int i=0;i<objs.length;i++){
			List<Object> valueParams=new ArrayList<Object>();
			valueParams.add(objs[i].getOid());
			for(String key : fieldMap.keySet()){
				if(i==0){
					FieldMeta field=fieldMap.get(key);
					fieldParams.add(field);
					fieldString.append(",").append(field.name());
					valueString.append(",?");
				}
				valueParams.add(ClassUtil.getFieldValue(objs[i], key));
			}
			valueParamsList.add(valueParams);
			List<Object> linkTableParams=new ArrayList<Object>();
			for(String key : linkTableMap.keySet()){
				if(i==0){
					LinkTableMeta linkTable=linkTableMap.get(key);
					fieldString.append(",").append(linkTable.name());
					valueString.append(",?");
				}
				Object bean=ClassUtil.getFieldValue(objs[i], key);
				if(null==bean){
					linkTableParams.add(null);
				}else{
					linkTableParams.add(ClassUtil.getFieldValue(bean, "oid"));
				}
			}
			linkTableParamsList.add(linkTableParams);
		}
		StringBuffer sql=new StringBuffer("INSERT INTO ").append(tableMeta.name());
		sql.append("(").append(fieldString).append(") VALUES(").append(valueString).append(")");

		try {
			List<Object[]> paramList=new ArrayList<Object[]>();
			for(int i=0;i<valueParamsList.size();i++){
				valueParamsList.get(i).addAll(linkTableParamsList.get(i));
				paramList.add(valueParamsList.get(i).toArray(new Object[]{}));
			}
			DBManager.batchUpdate(sql.toString(), paramList);
		} catch (SQLException e) {
			throw new MException(ModelUpdateUtil.class,"批量全插入失败!"+e.getMessage());
		}
	}
	/**
	 * 全字段更新记录.
	 * @param <T>
	 * @param obj
	 * @throws Exception
	 */
	public static <T extends Model> int updateModel(T obj) throws Exception {
		return updateModel(obj,null);
	}
	/**
	 * 根据fields内容更新属性 
	 * @param <T>
	 * @param obj 包含oid值
	 * @param fields
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model> int updateModel(T obj,String[] fields) throws MException {
		boolean flag=null==fields?false:true;
		if(StringUtil.isSpace(obj.getOid())){
			throw new MException(ModelQueryUtil.class,"主键值为空!");
		}
		// 获得对象的类型
		Class<T> clazz=(Class<T>) obj.getClass();
		TableMeta tableMeta=ModelConfig.getTableMeta(clazz);
		if(null==tableMeta) throw new MException(ModelQueryUtil.class,"没有对应的表!");
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		StringBuffer fieldString=new StringBuffer("");
		StringBuffer whereString=new StringBuffer(" WHERE oid=?");
		List<FieldMeta> fieldParams=new ArrayList<FieldMeta>();
		List<Object> valueParams=new ArrayList<Object>();
		List<Object> linkTableParams=new ArrayList<Object>();
		for(String key : fieldMap.keySet()){
			if(!flag||ArrayUtil.isContain(fields, key)){
				FieldMeta field=fieldMap.get(key);
				fieldString.append(",").append(field.name()).append("=?");
				fieldParams.add(field);
				valueParams.add(ClassUtil.getFieldValue(obj, key));
			}
		}
		for(String key : linkTableMap.keySet()){
			if(!flag||ArrayUtil.isContain(fields, key+".oid")){
				LinkTableMeta linkTable=linkTableMap.get(key);
				fieldString.append(",").append(linkTable.name()).append("=?");
				Object bean=ClassUtil.getFieldValue(obj, key);
				if(null==bean){
					linkTableParams.add(null);
				}else{
					linkTableParams.add(ClassUtil.getFieldValue(bean, "oid"));
				}
			}
		}
		if(fieldString.length()<=0){
			throw new MException(ModelUpdateUtil.class,"没有更新的属性!");
		}
		StringBuffer sql=new StringBuffer("UPDATE ").append(tableMeta.name());
		sql.append(" SET ").append(fieldString.substring(1)).append(whereString);

		try {
			List<Object> paramList=new ArrayList<Object>();
			for(int i=0;i<valueParams.size();i++){
				paramList.add(valueParams.get(i));
			}
			for(int i=0;i<linkTableParams.size();i++){
				paramList.add(linkTableParams.get(i));
			}
			paramList.add(obj.getOid());
			return DBManager.executeUpdate(sql.toString(),paramList.toArray(new Object[]{}));
		} catch (SQLException e) {
			throw new MException(ModelUpdateUtil.class,"更新失败!"+e.getMessage());
		}
	}
	/**
	 * 批量更新 根据oid
	 * @param objs
	 * @throws MException
	 */
	public static <T extends Model> void updateModels(T[] objs) throws MException {
		updateModels(objs,null);
	}
	/**
	 * 全字段批量更新数据
	 * @param <T>
	 * @param objs
	 * @throws MException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model> void updateModels(T[] objs,String[] fields) throws MException {
		boolean flag=null==fields?false:true;
		if(objs.length<1) return;
		// 获得对象的类型
		Class<T> clazz=(Class<T>) objs[0].getClass();
		TableMeta tableMeta=ModelConfig.getTableMeta(clazz);
		if(null==tableMeta) throw new MException(ModelQueryUtil.class,"没有对应的表!");
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		StringBuffer fieldString=new StringBuffer("");
		StringBuffer whereString=new StringBuffer(" WHERE oid=?");
		List<FieldMeta> fieldParams=new ArrayList<FieldMeta>();
		List<LinkTableMeta> linkTableParams=new ArrayList<LinkTableMeta>();
		List<List<Object>> valueParamsList=new ArrayList<List<Object>>();
		List<List<Object>> linkValueParamsList=new ArrayList<List<Object>>();
		for(int i=0;i<objs.length;i++){
			List<Object> valueParams=new ArrayList<Object>();
			for(String key : fieldMap.keySet()){
				if(!flag||ArrayUtil.isContain(fields, key)){
					FieldMeta field=fieldMap.get(key);
					if(i==0){
						fieldString.append(",").append(field.name()).append("=?");
						fieldParams.add(field);
					}
					valueParams.add(ClassUtil.getFieldValue(objs[i], key));
				}
			}
			valueParamsList.add(valueParams);
			List<Object> linkValueParams=new ArrayList<Object>();
			for(String key : linkTableMap.keySet()){
				if(!flag||ArrayUtil.isContain(fields, key)||ArrayUtil.isContain(fields, new StringBuffer(key).append(".oid").toString())){
					LinkTableMeta field=linkTableMap.get(key);
					if(i==0){
						fieldString.append(",").append(field.name()).append("=?");
						linkTableParams.add(field);
					}
					Object bean=ClassUtil.getFieldValue(objs[i], key);
					if(null==bean){
						linkValueParams.add(null);
					}else{
						linkValueParams.add(ClassUtil.getFieldValue(bean, "oid"));
					}
				}
			}
			linkValueParams.add(objs[i].getOid());
			linkValueParamsList.add(linkValueParams);
		}
		StringBuffer sql=new StringBuffer("UPDATE ").append(tableMeta.name());
		sql.append(" SET ").append(fieldString.substring(1)).append(whereString);
		
		try {
			List<Object[]> paramList=new ArrayList<Object[]>();
			for(int i=0;i<valueParamsList.size();i++){
				valueParamsList.get(i).addAll(linkValueParamsList.get(i));
				paramList.add(valueParamsList.get(i).toArray(new Object[]{}));
			}
			DBManager.batchUpdate(sql.toString(), paramList);
		} catch (SQLException e) {
			throw new MException(ModelUpdateUtil.class,"批量全更新失败!"+e.getMessage());
		}
	}
	/**
	 * 删除数据,有oid属性即可
	 * @param <T>
	 * @param obj
	 * @throws MException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model> int deleteModel(T obj) throws MException {
		// 获得对象的类型
		Class<T> clazz=(Class<T>) obj.getClass();
		TableMeta tableMeta=ModelConfig.getTableMeta(clazz);
		if(null==tableMeta) throw new MException(ModelQueryUtil.class,"没有对应的表!");
		String keyValue=obj.getOid();
		if(StringUtil.isSpace(keyValue)){
			throw new MException(ModelQueryUtil.class,"主键值为空!");
		}
		StringBuffer sql=new StringBuffer("DELETE FROM ").append(tableMeta.name());
		sql.append(" WHERE oid=?");
		
		try {
			return DBManager.executeUpdate(sql.toString(),new Object[]{keyValue});
		} catch (SQLException e) {
			throw new MException(ModelUpdateUtil.class,"删除操作失败!"+e.getMessage());
		}
	}
	/**
	 * 批量删除
	 * @param <T>
	 * @param objs
	 * @throws MException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model> void deleteModels(T[] objs) throws MException {
		// 获得对象的类型
		Class<T> clazz=(Class<T>) objs[0].getClass();
		TableMeta tableMeta=ModelConfig.getTableMeta(clazz);
		if(null==tableMeta) throw new MException(ModelQueryUtil.class,"没有对应的表!");
		StringBuffer sql=new StringBuffer("DELETE FROM ").append(tableMeta.name());
		sql.append(" WHERE oid=?");
		
		try {
			List<Object[]> paramList=new ArrayList<Object[]>();
			for(T obj : objs){
				paramList.add(new Object[]{obj.getOid()});
			}
			DBManager.batchUpdate(sql.toString(), paramList);
		} catch (SQLException e) {
			throw new MException(ModelUpdateUtil.class,"批量删除操作失败!"+e.getMessage());
		}
	}
	/**
	 * 根据条件更新
	 * @param obj
	 * @param fields
	 * @param condition  判断属性
	 * @return 返回更新条数
	 * @throws MException
	 * @throws SQLException 
	 */
	public static <T extends Model> int update(T obj,String[] fields,QueryCondition condition) throws MException, SQLException{
		// 获得对象的类型
		Class<T> clazz=(Class<T>) obj.getClass();
		TableMeta tableMeta=ModelConfig.getTableMeta(clazz);
		if(null==tableMeta) throw new MException(ModelQueryUtil.class,"没有对应的表!");
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		StringBuffer fieldString=new StringBuffer("");
		List<FieldMeta> fieldParams=new ArrayList<FieldMeta>();
		List<Object> valueParams=new ArrayList<Object>();
		List<Object> linkTableParams=new ArrayList<Object>();
		for(String key : fieldMap.keySet()){
			if(ArrayUtil.isContain(fields, key)){
				FieldMeta field=fieldMap.get(key);
				fieldString.append(",").append(field.name()).append("=?");
				fieldParams.add(field);
				valueParams.add(ClassUtil.getFieldValue(obj, key));
			}
		}
		for(String key : linkTableMap.keySet()){
			if(ArrayUtil.isContain(fields, key)||ArrayUtil.isContain(fields, new StringBuffer(key).append(".oid").toString())){
				LinkTableMeta linkTable=linkTableMap.get(key);
				fieldString.append(",").append(linkTable.name()).append("=?");
				Object bean=ClassUtil.getFieldValue(obj, key);
				if(null==bean){
					linkTableParams.add(null);
				}else{
					linkTableParams.add(ClassUtil.getFieldValue(bean, "oid"));
				}
			}
		}
		if(fieldString.length()<=0){
			throw new MException(ModelUpdateUtil.class,"没有更新的属性!");
		}
		List<T> list=ModelQueryList.getModelList(clazz, new String[]{"oid"}, null, condition);
		if(list.size()<=0){
			return 0;
		}
		StringBuffer sql=new StringBuffer("UPDATE ").append(tableMeta.name());
		sql.append(" SET ").append(fieldString.substring(1));
		sql.append(" WHERE oid in(");
		for(int i=0,len=list.size();i<len;i++){
			if(i!=0) sql.append(",");
			sql.append("'").append(list.get(i).getOid()).append("'");
		}
		sql.append(")");
		try {
			List<Object> paramList=new ArrayList<Object>();
			paramList.addAll(valueParams);
			paramList.addAll(linkTableParams);
			return DBManager.executeUpdate(sql.toString(),paramList.toArray(new Object[]{}));
		} catch (SQLException e) {
			throw new MException(ModelUpdateUtil.class,"更新属性失败!"+e.getMessage());
		}
	}
}
