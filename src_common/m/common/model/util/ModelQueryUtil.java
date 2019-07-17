package m.common.model.util;

import java.sql.Timestamp;
import java.util.Map;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.config.ModelConfig;
import m.common.model.type.FieldType;
import m.system.RuntimeData;
import m.system.db.DBManager;
import m.system.db.DataSet;
import m.system.exception.MException;
import m.system.util.ClassUtil;
import m.system.util.StringUtil;

public class ModelQueryUtil {
	/**
	 * 查询实体类
	 * @param <T>
	 * @param obj 查询的对象, 主键oid不能为空
	 * @return
	 * @throws Exception
	 */
	public static <T extends Model> T getModel(T obj) throws Exception{
		return getModel(obj,0);
	}
	/**
	 * 查询实体类
	 * @param <T>
	 * @param obj 查询的对象, 主键oid不能为空
	 * @param level 查询@LinkTableMeta属性,小于0就不往下查询 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model> T getModel(T obj, int level) throws Exception{
		if(null==obj || StringUtil.isSpace(obj.getOid())){
			return null;
		}
		// 获得对象的类型
		Class<T> clazz=(Class<T>) obj.getClass();
		T returnObject=clazz.getConstructor(new Class[]{}).newInstance(new Object[]{});  
		TableMeta tableMeta=ModelConfig.getTableMeta(clazz);
		if(null==tableMeta) throw new MException(ModelQueryUtil.class,"没有对应的表!");
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		StringBuffer fieldString=new StringBuffer("oid");
		for(String key : fieldMap.keySet()){
			FieldMeta field=fieldMap.get(key);
			fieldString.append(",").append(field.name());
		}
		for(String key : linkTableMap.keySet()){
			LinkTableMeta field=linkTableMap.get(key);
			fieldString.append(",").append(field.name());
		}
		StringBuffer sql=new StringBuffer("SELECT ");
		sql.append(fieldString).append(" FROM ").append(tableMeta.name()).append(" WHERE oid=?");
		
		DataSet ds=DBManager.executeQuery(sql.toString(),new String[]{obj.getOid()});
		if(ds.size()>0){
			returnObject.setOid(ds.get(String.class, 0, "oid"));
			for(String key : fieldMap.keySet()){
				FieldMeta field=fieldMap.get(key);
				if(FieldType.STRING.equals(field.type())){
					String v=ds.get(String.class, 0, field.name());
					if(null!=v){
						StringBuffer buffer=RuntimeData.testStaticDomain(new StringBuffer(clazz.getSimpleName()).append(".").append(key).toString());
						ClassUtil.setFieldValue(returnObject, key, buffer.append(v));
					}
				}else if(FieldType.INT.equals(field.type())){
					ClassUtil.setFieldValue(returnObject, key, ds.get(Integer.class, 0, field.name()));
				}else if(FieldType.DOUBLE.equals(field.type())){
					ClassUtil.setFieldValue(returnObject, key, ds.get(Double.class, 0, field.name()));
				}else if(FieldType.DATE.equals(field.type())){
					ClassUtil.setFieldValue(returnObject, key, ds.get(Timestamp.class, 0, field.name()));
				}
			}
			for(String key : linkTableMap.keySet()){
				LinkTableMeta field=linkTableMap.get(key);
				Object value=ds.get(0, field.name());
				if(null!=value){
					Model bean=ClassUtil.newInstance(field.table());
					ClassUtil.setFieldValue(bean, "oid", value);
					ClassUtil.setFieldValue(returnObject, key, level>0?getModel(bean,level-1):bean);
				}else {//没值就放空对象, 和ModelQueryList.getModel 保持一致
					ClassUtil.setFieldValue(returnObject, key, ClassUtil.newInstance(field.table()));
				}
			}
		}
		if(StringUtil.isSpace(returnObject.getOid())) return null;
		return returnObject;
	}
}
