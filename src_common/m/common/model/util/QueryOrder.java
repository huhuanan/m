package m.common.model.util;

import m.common.model.Model;
import m.system.exception.MException;

public class QueryOrder {
	private String name;
	private String oper;
	public QueryOrder(){};
	private QueryOrder(String name,String oper){
		this.name=name;
		this.oper=oper;
	}
	public String toSqlString(String a,Class<? extends Model> clazz,ModelQueryList modelQueryList) throws MException{
		return new StringBuffer(modelQueryList.getFieldNameSql(a, this.name, clazz, false)).append(" ").append(oper).toString();
	}
	public static QueryOrder asc(String name){
		return new QueryOrder(name,"asc");
	}
	public static QueryOrder desc(String name){
		return new QueryOrder(name,"desc");
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOper() {
		return oper;
	}
	public void setOper(String oper) {
		this.oper = oper;
	}
}
