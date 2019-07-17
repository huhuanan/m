package m.common.model.util;

import java.util.List;

public class QueryParameter {
	private String sql;
	private List<Object> valueList;
	public QueryParameter(String sql,List<Object> valueList){
		this.sql=sql;
		this.valueList=valueList;
	}
	public List<Object> getValueList() {
		return valueList;
	}
	public void setValueList(List<Object> valueList) {
		this.valueList = valueList;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
}
