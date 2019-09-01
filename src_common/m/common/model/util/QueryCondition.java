package m.common.model.util;

import java.util.ArrayList;
import java.util.List;

import m.common.model.Model;
import m.system.exception.MException;

public class QueryCondition {

	private enum OperType {
		EQ,LIKE,ISNULL,NOTNULL,ISEMPTY,NOTEMPTY,GT,GE,LT,LE,BETWEEN,IN,NOTIN,INS,INSQL,NOTINSQL,OR,AND,NOT
	}
	
	private String name;
	private OperType oper;
	private Object value;
	private boolean isField;
	private QueryCondition[] conditions;
	
	private QueryCondition(String name,OperType oper,Object value){
		this.name=name;
		this.oper=oper;
		this.value=value;
		this.isField=false;
	}
	private QueryCondition(String[] names,OperType oper) throws MException{
		if(names.length!=2) throw new MException(QueryCondition.class,"names数组个数为2!");
		this.name=names[0];
		this.oper=oper;
		this.value=names[1];
		this.isField=true;
	}
	private QueryCondition(QueryCondition[] conditions,OperType oper){
		this.conditions=conditions;
		this.oper=oper;
	}
	protected QueryParameter toQueryParameter(String a,Class<? extends Model> clazz,ModelQueryList modelQueryList) throws MException{
		StringBuffer sql=new StringBuffer();
		List<Object> valueList=new ArrayList<Object>();
		if(null!=conditions){
			if(this.oper.equals(OperType.AND)){
				for(int i=0;i<this.conditions.length;i++){
					QueryParameter qp=this.conditions[i].toQueryParameter(a,clazz, modelQueryList);
					if(qp.getSql().length()>0){
						sql.append(i==0?"":" AND ").append(qp.getSql());
						valueList.addAll(qp.getValueList());
					}
				}
				if(sql.length()>0) sql.insert(0, "(").append(")");
			}else if(this.oper.equals(OperType.OR)){
				for(int i=0;i<this.conditions.length;i++){
					QueryParameter qp=this.conditions[i].toQueryParameter(a,clazz, modelQueryList);
					if(qp.getSql().length()>0){
						sql.append(i==0?"":" OR ").append(qp.getSql());
						valueList.addAll(qp.getValueList());
					}
				}
				if(sql.length()>0) sql.insert(0, "(").append(")");
			}else if(this.oper.equals(OperType.NOT)){
				if(null!=this.conditions[0]){
					QueryParameter qp=this.conditions[0].toQueryParameter(a,clazz, modelQueryList);
					if(qp.getSql().length()>0){
						sql.append(" (NOT ").append(qp.getSql()).append(")");
						valueList.addAll(qp.getValueList());
					}
				}
			}
		}else if(this.isField){
			if(this.oper.equals(OperType.EQ)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append("=")
					.append(modelQueryList.getFieldNameSql(a, this.value.toString(), clazz, false)).append(" ");
			}else if(this.oper.equals(OperType.LIKE)){
				sql.append(" INSTR(").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(",")
					.append(modelQueryList.getFieldNameSql(a, this.value.toString(), clazz, false)).append(")>0 ");
			}else if(this.oper.equals(OperType.GT)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(">")
					.append(modelQueryList.getFieldNameSql(a, this.value.toString(), clazz, false)).append(" ");
			}else if(this.oper.equals(OperType.GE)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(">=")
					.append(modelQueryList.getFieldNameSql(a, this.value.toString(), clazz, false)).append(" ");
			}else if(this.oper.equals(OperType.LT)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append("<")
					.append(modelQueryList.getFieldNameSql(a, this.value.toString(), clazz, false)).append(" ");
			}else if(this.oper.equals(OperType.LE)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append("<=")
					.append(modelQueryList.getFieldNameSql(a, this.value.toString(), clazz, false)).append(" ");
			}
		}else{
			if(this.oper.equals(OperType.EQ)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append("=? ");
				valueList.add(this.value);
			}else if(this.oper.equals(OperType.LIKE)){
				sql.append(" INSTR(").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(",?)>0 ");
				valueList.add(this.value);
			}else if(this.oper.equals(OperType.ISNULL)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(" IS NULL ");
			}else if(this.oper.equals(OperType.NOTNULL)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(" IS NOT NULL ");
			}else if(this.oper.equals(OperType.ISEMPTY)){
				sql.append(" (").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(" IS NULL OR ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append("='')");
			}else if(this.oper.equals(OperType.NOTEMPTY)){
				sql.append(" (").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(" IS NOT NULL AND ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append("!='')");
			}else if(this.oper.equals(OperType.GT)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(">? ");
				valueList.add(this.value);
			}else if(this.oper.equals(OperType.GE)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(">=? ");
				valueList.add(this.value);
			}else if(this.oper.equals(OperType.LT)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append("<? ");
				valueList.add(this.value);
			}else if(this.oper.equals(OperType.LE)){
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append("<=? ");
				valueList.add(this.value);
			}else if(this.oper.equals(OperType.IN)){
				QueryParameter qp=((ModelQueryList) this.value).getQueryParameter();
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(" IN(").append(qp.getSql()).append(") ");
				valueList.addAll(qp.getValueList());
			}else if(this.oper.equals(OperType.INSQL)){
				QueryParameter qp=(QueryParameter) this.value;
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(" IN(").append(qp.getSql()).append(") ");
				valueList.addAll(qp.getValueList());
			}else if(this.oper.equals(OperType.NOTIN)){
				QueryParameter qp=((ModelQueryList) this.value).getQueryParameter();
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(" NOT IN(").append(qp.getSql()).append(") ");
				valueList.addAll(qp.getValueList());
			}else if(this.oper.equals(OperType.NOTINSQL)){
				QueryParameter qp=(QueryParameter) this.value;
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(" NOT IN(").append(qp.getSql()).append(") ");
				valueList.addAll(qp.getValueList());
			}else if(this.oper.equals(OperType.INS)){
				Object[] objs=(Object[]) this.value;
				StringBuffer ins=new StringBuffer();
				for(Object obj : objs){
					ins.append(",?");
					valueList.add(obj);
				}
				sql.append(" ").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(" IN(").append(ins.substring(1)).append(")") ;
			}else if(this.oper.equals(OperType.BETWEEN)){
				Object[] objs=(Object[]) this.value;
				sql.append(" (").append(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(" BETWEEN ? AND ?) ");
				valueList.add(objs[0]);
				valueList.add(objs[1]);
			}
		}
		return new QueryParameter(sql.toString(),valueList);
	}
	/**
	 * 等于
	 * @param name
	 * @param value
	 * @return
	 */
	public static QueryCondition eq(String name,Object value){
		return new QueryCondition(name,OperType.EQ,value);
	}
	public static QueryCondition eq(String[] names) throws MException{
		return new QueryCondition(names,OperType.EQ);
	}
	/**
	 * 包含
	 * @param name
	 * @param value
	 * @return
	 */
	public static QueryCondition like(String name,Object value){
		return new QueryCondition(name,OperType.LIKE,value);
	}
	public static QueryCondition like(String[] names) throws MException{
		return new QueryCondition(names,OperType.LIKE);
	}
	/**
	 * 包含 生成多个QueryCondition
	 * @param name
	 * @param value
	 * @return
	 */
	public static List<QueryCondition> manyLike(String name,Object[] value){
		List<QueryCondition> qcs=new ArrayList<QueryCondition>();
		for(int i=0;i<value.length;i++){
			qcs.add(like(name, value[i]));
		}
		return qcs;
	}
	/**
	 * 为null
	 * @param name
	 * @return
	 */
	public static QueryCondition isNull(String name){
		return new QueryCondition(name,OperType.ISNULL,null);
	}
	/**
	 * 不为null
	 * @param name
	 * @return
	 */
	public static QueryCondition notNull(String name){
		return new QueryCondition(name,OperType.NOTNULL,null);
	}
	/**
	 * 为空
	 * @param name
	 * @return
	 */
	public static QueryCondition isEmpty(String name){
		return new QueryCondition(name, OperType.ISEMPTY,null);
	}
	/**
	 * 不为空
	 * @param name
	 * @return
	 */
	public static QueryCondition notEmpty(String name){
		return new QueryCondition(name, OperType.NOTEMPTY,null);
	}
	/**
	 * 大于
	 * @param name
	 * @param value
	 * @return
	 */
	public static QueryCondition gt(String name,Object value){
		return new QueryCondition(name,OperType.GT,value);
	}
	public static QueryCondition gt(String[] names) throws MException{
		return new QueryCondition(names,OperType.GT);
	}
	/**
	 * 大于等于
	 * @param name
	 * @param value
	 * @return
	 */
	public static QueryCondition ge(String name,Object value){
		return new QueryCondition(name,OperType.GE,value);
	}
	public static QueryCondition ge(String[] names) throws MException{
		return new QueryCondition(names,OperType.GE);
	}
	/**
	 * 小于
	 * @param name
	 * @param value
	 * @return
	 */
	public static QueryCondition lt(String name,Object value){
		return new QueryCondition(name,OperType.LT,value);
	}
	public static QueryCondition lt(String[] names) throws MException{
		return new QueryCondition(names,OperType.LT);
	}
	/**
	 * 小于等于
	 * @param name
	 * @param value
	 * @return
	 */
	public static QueryCondition le(String name,Object value){
		return new QueryCondition(name,OperType.LE,value);
	}
	public static QueryCondition le(String[] names) throws MException{
		return new QueryCondition(names,OperType.LE);
	}
	/**
	 * in(select)
	 * @param name
	 * @param value
	 * @return
	 */
	public static QueryCondition in(String name,ModelQueryList value){
		return new QueryCondition(name,OperType.IN,value);
	}
	public static QueryCondition in(String name,QueryParameter value){
		return new QueryCondition(name,OperType.INSQL,value);
	}
	
	/**
	 * not in(select)
	 * @param name
	 * @param value
	 * @return
	 */
	public static QueryCondition notIn(String name,ModelQueryList value){
		return new QueryCondition(name,OperType.NOTIN,value);
	}
	public static QueryCondition notIn(String name,QueryParameter value){
		return new QueryCondition(name,OperType.NOTINSQL,value);
	}
	/**
	 * in(?,?...)
	 * @param name
	 * @param values
	 * @return
	 */
	public static QueryCondition in(String name,Object[] values){
		return new QueryCondition(name,OperType.INS,values);
	}
	/**
	 * 在两个value之间
	 * @param name
	 * @param values
	 * @return
	 * @throws MException
	 */
	public static QueryCondition between(String name,Object[] values) throws MException{
		if(values.length!=2) throw new MException(QueryCondition.class,"values数组个数为2!");
		return new QueryCondition(name,OperType.BETWEEN,values);
	}
	/**
	 * 多个条件按or连接
	 * @param conditions
	 * @return
	 */
	public static QueryCondition or(QueryCondition[] conditions){
		return new QueryCondition(conditions,OperType.OR);
	}
	/**
	 * 多个条件按and连接
	 * @param conditions
	 * @return
	 */
	public static QueryCondition and(QueryCondition[] conditions){
		return new QueryCondition(conditions,OperType.AND);
	}
	/**
	 * 条件取反
	 * @param condition
	 * @return
	 */
	public static QueryCondition not(QueryCondition condition){
		if(null!=condition){
			return new QueryCondition(new QueryCondition[]{condition},OperType.NOT);
		}else{
			return null;
		}
	}
	
}
