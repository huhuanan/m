package m.common.model.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alipay.api.internal.util.StringUtils;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.config.ModelConfig;
import m.common.model.type.FieldType;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.exception.MException;
import m.system.util.ArrayUtil;
import m.system.util.ClassUtil;
import m.system.util.StringUtil;

public class ModelCheckUtil {
	public static void check(Model model) throws MException{
		check(model,null);
	}
	public static void check(Model model,String[] fields) throws MException{
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(model.getClass());
		for(String field : fieldMap.keySet()){
			if(fields!=null&&!ArrayUtil.isContain(fields, field)) continue;
			FieldMeta meta=fieldMap.get(field);
			Object value=ClassUtil.getFieldValue(model, field);
			if(meta.notnull()){
				if(null==value){
					throw new MException(ModelCheckUtil.class,new StringBuffer("(").append(meta.description().split("\\|")[0]).append(")不能为空!").toString());
				}else if(FieldType.STRING.equals(meta.type())&&((String)value).length()==0){
					throw new MException(ModelCheckUtil.class,new StringBuffer("(").append(meta.description().split("\\|")[0]).append(")不能为空!").toString());
				}
			}
			if(FieldType.STRING.equals(meta.type())&&null!=value&&value.toString().length()>meta.length()){
				throw new MException(ModelCheckUtil.class,new StringBuffer("(").append(meta.description().split("\\|")[0]).append(")长度不能大于").append(meta.length()).append("!").toString());
			}
		}
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(model.getClass());
		for(String field : linkTableMap.keySet()){
			if(fields!=null&&!ArrayUtil.isContain(fields, field)) continue;
			LinkTableMeta meta=linkTableMap.get(field);
			Object value=ClassUtil.getFieldValue(model, field);
			if(meta.notnull()){
				if(!(null!=value&&!StringUtil.isSpace(((Model)value).getOid()))){
					throw new MException(ModelCheckUtil.class,new StringBuffer("(").append(meta.description().split("\\|")[0]).append(")不能为空!").toString());
				}
			}
		}
	}
	public static void checkNotNull(Model model,String[] fields) throws MException{
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(model.getClass());
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(model.getClass());
		for(String field : fields){
			FieldMeta fmeta=fieldMap.get(field);
			if(null!=fmeta){
				Object value=ClassUtil.getFieldValue(model, field);
				if(null==value){
					throw new MException(ModelCheckUtil.class,new StringBuffer("(").append(fmeta.description().split("\\|")[0]).append(")不能为空!").toString());
				}else if(FieldType.STRING.equals(fmeta.type())&&((String)value).length()==0){
					throw new MException(ModelCheckUtil.class,new StringBuffer("(").append(fmeta.description().split("\\|")[0]).append(")不能为空!").toString());
				}
			}
			int n=field.indexOf(".");
			LinkTableMeta lmeta=linkTableMap.get(field.substring(0,n>0?n:field.length()));
			if(null!=lmeta){
				Object value=ClassUtil.getFieldValue(model, field.substring(0,n>0?n:field.length()));
				if(!(null!=value&&!StringUtil.isSpace(((Model)value).getOid()))){
					throw new MException(ModelCheckUtil.class,new StringBuffer("(").append(lmeta.description().split("\\|")[0]).append(")不能为空!").toString());
				}
			}
		}
	}
	/**
	 * 检查模型类唯一组合,先检测这些字段不能为空
	 * @param model
	 * @param fields
	 * @throws MException
	 * @throws SQLException
	 */
	public static void checkUniqueCombine(Model model,String[] fields) throws MException, SQLException{
		checkUniqueCombine(model,fields,null);
	}
	/**
	 * 检查模型类唯一组合,先检测这些字段不能为空
	 * @param model
	 * @param fields
	 * @param errorMessage 错误消息
	 * @throws MException
	 * @throws SQLException
	 */
	public static void checkUniqueCombine(Model model,String[] fields,String errorMessage) throws MException, SQLException{
		//checkNotNull(model,fields);
		TableMeta table=ModelConfig.getTableMeta(model.getClass());
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(model.getClass());
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(model.getClass());
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT oid from ").append(table.name()).append(" where 1=1 ");
		List<Object> params=new ArrayList<Object>();
		List<String> fn=new ArrayList<String>();
		for(String field : fields){
			FieldMeta fmeta=fieldMap.get(field);
			if(null!=fmeta){
				Object value=ClassUtil.getFieldValue(model, field);
				sql.append(" and ").append(fmeta.name()).append("=? ");
				params.add(value);
				fn.add(fmeta.description().split("\\|")[0]);
			}
			int n=field.indexOf(".");
			LinkTableMeta lmeta=linkTableMap.get(field.substring(0,n>0?n:field.length()));
			if(null!=lmeta){
				Object value=ClassUtil.getFieldValue(model, field.substring(0,n>0?n:field.length()));
				sql.append(" and ").append(lmeta.name()).append("=? ");
				params.add(((Model)value).getOid());
				fn.add(lmeta.description().split("\\|")[0]);
			}
		}
		if(!StringUtil.isSpace(model.getOid())){
			sql.append(" and oid!=? ");
			params.add(model.getOid());
		}
		DataRow dr=DBManager.queryFirstRow(sql.toString(),params.toArray(new Object[]{}));
		if(null!=dr){
			if(StringUtil.isSpace(errorMessage)) {
				throw new MException(ModelCheckUtil.class,new StringBuffer("(")
				.append(ArrayUtil.connection(fn.toArray(new String[]{}), "+"))
				.append(")已存在!").toString());
			}else {
				throw new MException(ModelCheckUtil.class, errorMessage);
			}
		}
	}
}
