package m.common.model.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.config.ModelConfig;
import m.system.RuntimeData;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.db.DataSet;
import m.system.exception.MException;
import m.system.lang.PageInfo;
import m.system.util.ClassUtil;
import m.system.util.StringUtil;


public class ModelQueryList {
	private Class<? extends Model> table;
	private String[] fieldNames;
	private QueryCondition condition;
	private QueryOrder[] orders;
	private QueryPage page;
	private int int_a=0;
	private boolean isGroup;
	private boolean useStaticField=true;

	private Map<String,String> tableAMap;
	private QueryParameter parameter;
	private QueryParameter countParameter;
	private Map<String,String> linkTableSqlMap;
	private List<String> fieldNameSqlList;
	private Map<String,String> fieldToAliasMap;
	private Map<String,String> expressionMap;
	private ModelQueryList(Class<? extends Model> clazz,String[] fieldNames,QueryPage page,QueryCondition condition,boolean isGroup,Map<String,String> expressionMap,QueryOrder... orders){
		this.table=clazz;
		List<String> fieldList=new ArrayList<String>();
		for(String f : fieldNames){
			fieldList.addAll(coverToFieldList(clazz, "", f));
		}
		this.fieldNames=fieldList.toArray(new String[]{});
		this.page=page;
		this.condition=condition;
		this.isGroup=isGroup;
		this.orders=orders;
		this.expressionMap=expressionMap;
		this.tableAMap=new HashMap<String, String>();
		this.linkTableSqlMap=new LinkedHashMap<String, String>();
		this.fieldToAliasMap=new HashMap<String, String>();
	}
	public boolean getUseStaticField() {
		return useStaticField;
	}
	public ModelQueryList setUseStaticField(boolean useStaticField) {
		this.useStaticField = useStaticField;
		return this;
	}
	public String getAlias4Field(String field){
		return this.fieldToAliasMap.get(field);
	}
	//转换属性，*转换成所有属性
	private <T extends Model> List<String> coverToFieldList(Class<T> clazz,String prefix,String attr){
		List<String> fieldList=new ArrayList<String>();
		String ps=StringUtil.isSpace(prefix)?"":new StringBuffer(prefix).append(".").toString();
		if("*".equals(attr)){
			fieldList.add(new StringBuffer(ps).append("oid").toString());
			Map<String,FieldMeta> fs=ModelConfig.getFieldMetaMap(clazz);
			for(String key : fs.keySet()){
				if(!RuntimeData.isSecretField(new StringBuffer(clazz.getSimpleName()).append(".").append(key).toString())) {//秘密字段不转换
					fieldList.add(new StringBuffer(ps).append(key).toString());
				}
			}
			Map<String,LinkTableMeta> ls=ModelConfig.getLinkTableMetaMap(clazz);
			for(String key : ls.keySet()){
				fieldList.add(new StringBuffer(ps).append(key).append(".oid").toString());
			}
		}else{
			int i=attr.indexOf(".");
			if(i==-1){
				fieldList.add(new StringBuffer(ps).append(attr).toString());
			}else{
				String fn=attr.substring(0, i);
				String newField=attr.substring(i+1);
				LinkTableMeta linkTable=ModelConfig.getLinkTableMetaMap(clazz).get(fn);
				if(null!=linkTable){
					fieldList.addAll(coverToFieldList(linkTable.table(), new StringBuffer(ps).append(fn).toString(), newField));
				}
			}
		}
		return fieldList;
	}
	/**
	 * 获取fieldNames对应的sqlList
	 * @return
	 * @throws MException 
	 */
	private List<String> getFieldNameSqlList() throws MException{
		if(null==this.fieldNameSqlList){
			List<String> fieldNameSqlList=new ArrayList<String>();
			if(null!=this.fieldNames){
				for(String fieldName : this.fieldNames){
					String fn=getFieldNameSql("t0", fieldName, table, true);
					String exp=this.expressionMap.get(fieldName);
					if(!StringUtil.isSpace(exp)) {
						Pattern pattern=Pattern.compile("\\#\\{.+?\\}");
						Matcher matcher=pattern.matcher(exp);
						while(matcher.find()){
							String str=matcher.group();
							String ns=str.substring(2,str.length()-1);
							String fn1=getFieldNameSql("t0", ns, table, true);
							fieldToAliasMap.put(ns, fn1.substring(fn1.lastIndexOf(" ")+1));
							exp=exp.replace(str, fn1.substring(0,fn1.lastIndexOf(" ")));
						}
						fieldNameSqlList.add(exp+fn.substring(fn.lastIndexOf(" ")));
					}else{
						if(!StringUtil.isSpace(fn)){
							fieldToAliasMap.put(fieldName, fn.substring(fn.lastIndexOf(" ")+1));
							fieldNameSqlList.add(fn);
						}
					}
				}
			}
			this.fieldNameSqlList=fieldNameSqlList;
		}
		return this.fieldNameSqlList;
	}
	/**
	 * 递归方法,  返回对应的查询的fieldName, 并添加需要left join的 table
	 * @param a
	 * @param fieldName
	 * @param fieldMap
	 * @param linkTableMap
	 * @return
	 * @throws MException 
	 */
	protected String getFieldNameSql(String a,String fieldName,Class<? extends Model> clazz,boolean hasColName) throws MException{
		if("oid".equals(fieldName)){
			if(hasColName){
				return new StringBuffer(a).append(".oid ").append(a).append("_oid").toString();
			}else{
				return new StringBuffer(a).append(".oid").toString();
			}
		}
		int i=fieldName.indexOf(".");
		if(i==-1){
			FieldMeta fieldMeta=ModelConfig.getFieldMetaMap(clazz).get(fieldName);
			if(null!=fieldMeta){
				if(hasColName){
					return new StringBuffer(a).append(".").append(fieldMeta.name()).append(" ").append(a).append("_").append(fieldName).toString();
				}else{
					return new StringBuffer(a).append(".").append(fieldMeta.name()).toString();
				}
			}
		}else{
			String fn=fieldName.substring(0, i);
			String newField=fieldName.substring(i+1);
			LinkTableMeta linkTable=ModelConfig.getLinkTableMetaMap(clazz).get(fn);
			if(null!=linkTable){
				TableMeta jtm=ModelConfig.getTableMeta(linkTable.table());
				String afn=new StringBuffer(a).append(".").append(fn).toString();
				if(newField.equals("oid")){
					if(hasColName){
						return new StringBuffer(a).append(".").append(linkTable.name()).append(" ").append(a).append("_").append(fn).toString();
					}else{
						return new StringBuffer(a).append(".").append(linkTable.name()).toString();
					}
				}else{
					String linka=this.tableAMap.get(afn);
					if(null==linka){
						linka=new StringBuffer("t").append(int_a+1).toString();
						StringBuffer linkTableSql=new StringBuffer()
							.append(" LEFT JOIN ").append(jtm.name()).append(" ").append(linka)
							.append(" ON t").append(int_a+1).append(".oid=").append(a).append(".").append(linkTable.name());
						this.linkTableSqlMap.put(afn,linkTableSql.toString());
						this.tableAMap.put(afn, linka);
						int_a++;
					}
					return getFieldNameSql(linka, newField, linkTable.table(), hasColName);
				}
			}else {
				throw new MException(this.getClass(), new StringBuffer("没有找到属性 ").append(fieldName).toString());
			}
		}
		return null;
	}
	/**
	 * 返回 from段sql,  先执行getFieldNameSqlList()再调用才有效
	 * @return
	 */
	private String getFromSql(){
		TableMeta tableMeta=ModelConfig.getTableMeta(table);
		StringBuffer from=new StringBuffer();
		from.append(" FROM ").append(tableMeta.name()).append(" t0 ");
		if(null!=this.linkTableSqlMap){
			for(String linkTableSql : this.linkTableSqlMap.values()){
				from.append(linkTableSql);
			}
		}
		return from.toString();
	}
	/**
	 * 返回查询sql 和参数
	 * @return
	 * @throws MException 
	 */
	public QueryParameter getQueryParameter() throws MException{
		if(null==this.parameter){
			List<String> fnSqlList=getFieldNameSqlList();
			if(null!=this.condition){
				this.parameter=this.condition.toQueryParameter("t0", table, this);
			}else{
				this.parameter=new QueryParameter("",new ArrayList<Object>());
			}
			StringBuffer orderSql=new StringBuffer();
			if(null!=this.orders){
				for(QueryOrder order : this.orders){
					if(null!=order&&!StringUtil.isSpace(order.getName()))
						orderSql.append(order.toSqlString("t0", table, this)).append(",");
				}
				orderSql.append(QueryOrder.desc("oid").toSqlString("t0", table, this));
			}
			String from=getFromSql();
			StringBuffer sql=new StringBuffer();
			for(String fnSql : fnSqlList){
				sql.append(",").append(fnSql);
			}
			sql=new StringBuffer(sql.substring(1)).insert(0, "SELECT ").append(from);
			if(!StringUtil.isSpace(this.parameter.getSql())){
				sql.append(" WHERE ").append(this.parameter.getSql());
			}
			if(this.isGroup){
				StringBuffer group=new StringBuffer();
				for(String fnSql : fnSqlList){
					String g=fnSql.split(" ")[0];
					String lg=g.toUpperCase();
					if(lg.indexOf("SUM(")>-1||lg.indexOf("COUNT(")>-1||lg.indexOf("AVG(")>-1||lg.indexOf("MAX(")>-1||lg.indexOf("MIN(")>-1) {
						//聚合函数不分组
					}else {
						group.append(",").append(g);
					}
				}
				if(group.length()>0) {
					sql.append(" GROUP BY ").append(group.substring(1));
				}
			}
			if(orderSql.length()>0){
				sql.append(" ORDER BY ").append(orderSql);
			}
			if(null!=this.page){
				sql.append(" LIMIT ").append(this.page.getIndex()).append(",").append(this.page.getNum()).append(" ");
			}
			this.parameter.setSql(sql.toString());
		}
		return this.parameter;
	}
	public QueryParameter getCountParameter() throws MException{
		if(null==this.countParameter){
			this.countParameter=getQueryParameter();
			StringBuffer sql=new StringBuffer();
			sql=new StringBuffer("SELECT count(*) num FROM (").append(this.countParameter.getSql().split(" LIMIT ")[0]).append(") a");
			this.countParameter.setSql(sql.toString());
		}
		return this.countParameter;
	}
//	/**
//	 * 返回查询实例化
//	 */
//	public static <T extends Model> ModelQueryList instance(Class<T> clazz,String[] fieldNames,QueryPage page){
//		return new ModelQueryList(clazz,fieldNames,page,null,false,new HashMap<String,String>(), new QueryOrder[]{});
//	}
	/**
	 * 返回查询实例化
	 */
	public static <T extends Model> ModelQueryList instance(Class<T> clazz,String[] fieldNames,QueryPage page,QueryCondition condition){
		return new ModelQueryList(clazz,fieldNames,page,condition,false,new HashMap<String,String>(), new QueryOrder[]{});
	}
	/**
	 * 返回查询实例化
	 */
	public static <T extends Model> ModelQueryList instance(Class<T> clazz,String[] fieldNames,QueryPage page,QueryCondition condition,QueryOrder... orders){
		return new ModelQueryList(clazz,fieldNames,page,condition,false,new HashMap<String,String>(),orders);
	}
	/**
	 * 返回查询实例化
	 * @param expressionMap  sql表达式 map<fieldname,exp>  exp中的fieldname用${}括着    exp例如:concat(#{name},'(',#{backgroud.thumPath},')')
	 */
	public static <T extends Model> ModelQueryList instance(Class<T> clazz,String[] fieldNames,QueryPage page,QueryCondition condition,Map<String,String> expressionMap,QueryOrder... orders){
		return new ModelQueryList(clazz,fieldNames,page,condition,false,expressionMap,orders);
	}
	/**
	 * 查询列表
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model> List<T> getModelList(ModelQueryList modelQueryList) throws SQLException, MException{
		List<T> list=new ArrayList<T>();
		QueryParameter qp=modelQueryList.getQueryParameter();
		DataSet ds=DBManager.executeQuery(qp.getSql(), qp.getValueList().toArray(new Object[]{}));
		for(DataRow row : ds.rows()){
			T model=(T) ClassUtil.newInstance(modelQueryList.table);
			for(String fn : modelQueryList.fieldNames){
				fillAttribute(model, "t0" ,"" , fn, modelQueryList.tableAMap, row, modelQueryList.getUseStaticField());
			}
			list.add(model);
		}
		return list;
	}
	/**
	 * 查询列表
	 */
	public static <T extends Model> List<T> getModelList(Class<T> clazz,String[] fieldNames,QueryPage page,QueryCondition condition,QueryOrder... orders) throws SQLException, MException{
		ModelQueryList modelQueryList=new ModelQueryList(clazz,fieldNames,page,condition,false,new HashMap<String,String>(),orders);
		return getModelList(modelQueryList);
	}
	/**
	 * 查询列表
	 * @param expressionMap  sql表达式 map<fieldname,exp>  exp中的fieldname用#{}括着    exp例如:concat(#{name},'(',#{backgroud.thumPath},')')
	 */
	public static <T extends Model> List<T> getModelList(Class<T> clazz,String[] fieldNames,QueryPage page,QueryCondition condition,Map<String,String> expressionMap,QueryOrder... orders) throws SQLException, MException{
		ModelQueryList modelQueryList=new ModelQueryList(clazz,fieldNames,page,condition,false,expressionMap,orders);
		return getModelList(modelQueryList);
	}
	/**
	 * 查询列表
	 */
	public static <T extends Model> List<T> getModelList(Class<T> clazz,String[] fieldNames,QueryPage page,QueryCondition condition,boolean isGroup,QueryOrder... orders) throws SQLException, MException{
		ModelQueryList modelQueryList=new ModelQueryList(clazz,fieldNames,page,condition,isGroup,new HashMap<String,String>(),orders);
		return getModelList(modelQueryList);
	}
	/** 
	 * 查询列表
	 * @param expressionMap  sql表达式 map<fieldname,exp>  exp中的fieldname用#{}括着    exp例如:concat(#{name},'(',#{backgroud.thumPath},')')
	 */
	public static <T extends Model> List<T> getModelList(Class<T> clazz,String[] fieldNames,QueryPage page,QueryCondition condition,Map<String,String> expressionMap,boolean isGroup,QueryOrder... orders) throws SQLException, MException{
		ModelQueryList modelQueryList=new ModelQueryList(clazz,fieldNames,page,condition,isGroup,expressionMap,orders);
		return getModelList(modelQueryList);
	}
	/**
	 * 查询带分页信息
	 */
	public static PageInfo getModelPageInfo(ModelQueryList modelQueryList) throws SQLException, MException{
		List<Model> list=getModelList(modelQueryList);
		QueryParameter qp=modelQueryList.getCountParameter();
		DataRow ds=DBManager.queryFirstRow(qp.getSql(), qp.getValueList().toArray(new Object[]{}));
		Integer count=null;
		if(null!=ds){
			count=ds.get(Long.class, "num").intValue();
		}
		QueryPage page=modelQueryList.page;
		if(null==page){
			page=new QueryPage();
		}
		return new PageInfo(list,count,page.getIndex(),page.getNum());
	}
//	/**
//	 * 查询带分页信息
//	 */
//	public static <T extends Model> PageInfo getModelPageInfo(Class<T> clazz,String[] fieldNames,QueryPage page,QueryCondition condition,QueryOrder... orders) throws SQLException, MException{
//		ModelQueryList modelQueryList=new ModelQueryList(clazz,fieldNames,page,condition,false,new HashMap<String,String>(),orders);
//		return getModelPageInfo(modelQueryList);
//	}
//	/**
//	 * 查询带分页信息
//	 */
//	public static <T extends Model> PageInfo getModelPageInfo(Class<T> clazz,String[] fieldNames,QueryPage page,QueryCondition condition,boolean isGroup,QueryOrder... orders) throws SQLException, MException{
//		ModelQueryList modelQueryList=new ModelQueryList(clazz,fieldNames,page,condition,isGroup,new HashMap<String,String>(),orders);
//		return getModelPageInfo(modelQueryList);
//	}
	
	private static <T extends Model> void fillAttribute(T model,String a,String fname,String attr,Map<String,String> tableAMap,DataRow row,boolean useStaticField) throws MException, SQLException{
		if("oid".equals(attr)){
			ClassUtil.setFieldValue(model, attr, row.get(new StringBuffer(a).append("_oid").toString()));
		}
		int i=attr.indexOf(".");
		if(i==-1){
			Object v=row.get(new StringBuffer(a).append("_").append(attr).toString());
			try {
				if(v instanceof String && useStaticField){
					StringBuffer buffer=RuntimeData.testStaticDomain(new StringBuffer(model.getClass().getSimpleName()).append(".").append(attr).toString());
					ClassUtil.setFieldValue(model, attr, buffer.append(v));
				}else{
					ClassUtil.setFieldValue(model, attr, v);
				}
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}else{
			String fn=attr.substring(0, i);
			String newField=attr.substring(i+1);
			Object lm=ClassUtil.getFieldValue(model, fn);
			if(null==lm){
				Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(model.getClass());
				lm=ClassUtil.newInstance(linkTableMap.get(fn).table());
			}
			String aa="";
			for(String key : tableAMap.keySet()){
				if(key.equals(new StringBuffer(a).append(".").append(fn).toString())){
					aa=tableAMap.get(key);
					break;
				}
			}
			if(newField.equals("oid")){
				Object tmp=row.get(new StringBuffer(a).append("_").append(fn).toString());
				if(null!=tmp){
					ClassUtil.setFieldValue((Model)lm, "oid", tmp);
				}
				ClassUtil.setFieldValue(model, fn, lm);
			}else{
				fillAttribute((Model)lm, aa, new StringBuffer(aa).append(".").append(fn).toString(), newField, tableAMap, row, useStaticField);
				ClassUtil.setFieldValue(model, fn, lm);
			}
		}
	}
	@SuppressWarnings("unchecked")
	public static <T extends Model> T getModel(T obj,int level) throws Exception{
		List<T> list=(List<T>) getModelList(obj.getClass(), getFieldList(obj.getClass(),"",level).toArray(new String[]{}), new QueryPage(0,1), QueryCondition.eq("oid", obj.getOid()));
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public static <T extends Model> T getModel(T obj,String[] fieldNames) throws Exception{
		return getModel(obj,fieldNames,true);
	}
	/**
	 * 获取对象
	 * @param obj
	 * @param fieldNames
	 * @param isStatic 静态
	 * @return
	 * @throws Exception
	 */
	public static <T extends Model> T getModel(T obj,String[] fieldNames,boolean isStatic) throws Exception{
		ModelQueryList util=ModelQueryList.instance(obj.getClass(), fieldNames, new QueryPage(0,1), QueryCondition.eq("oid", obj.getOid()));
		util.setUseStaticField(isStatic);
		List<T> list=(List<T>)util.getModelList(util);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	public static<T extends Model> T getModel(Class<T> clazz,String[] fieldNames,QueryCondition condition) throws SQLException, MException {
		List<T> list=(List<T>) getModelList(clazz, fieldNames, new QueryPage(0,1), condition,QueryOrder.desc("oid"));
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	private static <T extends Model> List<String> getFieldList(Class<T> clazz,String prefix,int level) throws MException{
		List<String> fieldList=new ArrayList<String>();
		Map<String,FieldMeta> fieldMap=ModelConfig.getFieldMetaMap(clazz);
		Map<String,LinkTableMeta> linkTableMap=ModelConfig.getLinkTableMetaMap(clazz);
		fieldList.add(new StringBuffer(prefix).append(StringUtil.isSpace(prefix)?"":".").append("oid").toString());
		for(String field : fieldMap.keySet()){
			fieldList.add(new StringBuffer(prefix).append(StringUtil.isSpace(prefix)?"":".").append(field).toString());
		}
		if(level>0){
			for(String field : linkTableMap.keySet()){
				fieldList.addAll(getFieldList(linkTableMap.get(field).table(),new StringBuffer(prefix).append(StringUtil.isSpace(prefix)?"":".").append(field).toString(),level-1));
			}
		}else{
			for(String field : linkTableMap.keySet()){
				fieldList.add(new StringBuffer(new StringBuffer(prefix).append(StringUtil.isSpace(prefix)?"":".").append(field).toString()).append(".oid").toString());
			}
		}
		return fieldList;
	}
}