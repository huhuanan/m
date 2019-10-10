package m.system.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import m.common.model.util.QueryParameter;
import m.system.util.StringUtil;
/**
 * sql拼接
 */
public class SqlBuffer {
	private Integer maxSeconds;
	private StringBuffer sql=new StringBuffer();
	private List<Object> paramList=new ArrayList<Object>();
	public SqlBuffer() {
		this.maxSeconds=DBConfig.getMaxConnect();
	}
	public SqlBuffer(int maxSeconds) {
		this.maxSeconds=maxSeconds;
	}
	public SqlBuffer append(String sqlPart) {
		if(StringUtil.isSpace(sqlPart)) return this;
		sql.append(sqlPart).append(" ");
		return this;
	}
	/**
	 * 添加sql片段
	 * @param sqlPart 片段为空则不添加
	 * @param param 参数
	 * @return
	 */
	public SqlBuffer append(String sqlPart,Object param) {
		if(StringUtil.isSpace(sqlPart)) return this;
		sql.append(sqlPart).append(" ");
		paramList.add(param);
		return this;
	}
	/**
	 * 添加sql片段
	 * @param sqlPart
	 * @param params
	 * @return
	 */
	public SqlBuffer append(String sqlPart,Object[] params) {
		if(StringUtil.isSpace(sqlPart)) return this;
		sql.append(sqlPart).append(" ");
		for(Object obj : params) {
			paramList.add(obj);
		}
		return this;
	}
	/**
	 * 返回sql参数
	 * @return
	 */
	public QueryParameter toQueryParameter() {
		return new QueryParameter(sql.toString(),paramList);
	}
	/**
	 * 执行更新
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdate() throws SQLException {
		return DBManager.executeUpdate(sql.toString(), paramList.toArray(new Object[] {}),maxSeconds);
	}
	/**
	 * 执行查询
	 * @return
	 * @throws SQLException
	 */
	public DataSet executeQuery() throws SQLException {
		return DBManager.executeQuery(sql.toString(), paramList.toArray(new Object[] {}),maxSeconds);
	}
	/**
	 * 执行查询第一行
	 * @return
	 * @throws SQLException
	 */
	public DataRow queryFirstRow() throws SQLException {
		return DBManager.queryFirstRow(sql.toString(), paramList.toArray(new Object[] {}),maxSeconds);
	}
}
