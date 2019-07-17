package m.system.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.system.RuntimeData;

/**
 * 数据库操作类
 * @author Administrator
 *
 */
public class DBManager {

	public static int executeUpdate(String sql) throws SQLException{
		return executeUpdate(sql,null);
	}
	public static int executeUpdate(String sql, Object[] params) throws SQLException {
		if(RuntimeData.getDebug()) System.out.println("print:"+sql);
		Connection conn=null;
		PreparedStatement ps=null;
		int num=0;
		try {
			conn=TransactionManager.getConnection();
			ps=conn.prepareStatement(sql);
			// 参数赋值
			if (params!=null) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}
			// 执行
			num = ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			// 释放资源
			if(null!=ps) ps.close();
		}
		return num;
	}
	public static void batchUpdate(String sql, List<Object[]> paramList) throws SQLException{
		if(RuntimeData.getDebug()) System.out.println("print:"+sql);
		Connection conn=null;
		PreparedStatement ps=null;
		try {
			conn=TransactionManager.getConnection();
			ps=conn.prepareStatement(sql);
			// 参数赋值
			for(Object[] params : paramList){
				if (params!=null) {
					for (int i = 0; i < params.length; i++) {
						ps.setObject(i + 1, params[i]);
					}
					ps.addBatch();
				}
			}
			// 执行
			ps.executeBatch();
		} catch (SQLException e) {
			throw e;
		} finally {
			// 释放资源
			if(null!=ps) ps.close();
		}
	}
	public static void batchUpdate(String[] sqls) throws SQLException{
		if(RuntimeData.getDebug()){
			for(String sql : sqls) System.out.println("print:"+sql);
		}
		Connection conn=null;
		Statement ps=null;
		try {
			conn=TransactionManager.getConnection();
			ps=conn.createStatement();
			// 参数赋值
			for(String sql : sqls){
				if (sql!=null&&!"".equals(sql.trim())) {
					ps.addBatch(sql.trim());
				}
			}
			// 执行
			ps.executeBatch();
		} catch (SQLException e) {
			throw e;
		} finally {
			// 释放资源
			if(null!=ps) ps.close();
		}
	}

	public static DataSet executeQuery(String sql) throws SQLException {
		return executeQuery(sql,null);
	}
	public static DataSet executeQuery(String sql, Object[] params) throws SQLException {
		if(RuntimeData.getDebug()) System.out.println("print:"+sql);
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSetMetaData rsmd=null;
		ResultSet rs=null;
		int columnCount=0;
		List<Object> list = new ArrayList<Object>();
		try {
			conn=TransactionManager.getConnection();
			ps=conn.prepareStatement(sql);
			if (params!=null) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}
			// 执行SQL获得结果集
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			// 获得结果集列数
			columnCount = rsmd.getColumnCount();
			// 将ResultSet的结果保存到List中
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					map.put(rsmd.getColumnLabel(i), rs.getObject(i));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			// 释放资源
			if(null!=ps) ps.close();
			if(null!=rs) rs.close();
		}
		return new DataSet(list);
	}
	public static DataRow queryFirstRow(String sql) throws SQLException{
		return queryFirstRow(sql,null);
	}
	public static DataRow queryFirstRow(String sql, Object[] params) throws SQLException{
		if(RuntimeData.getDebug()) System.out.println("print:"+sql);
		if(sql.toLowerCase().indexOf("limit")==-1) sql=new StringBuffer(sql).append(" LIMIT 1").toString();
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSetMetaData rsmd=null;
		ResultSet rs=null;
		int columnCount=0;
		Map<String, Object> map=null;
		try {
			conn=TransactionManager.getConnection();
			ps=conn.prepareStatement(sql);
			if (params!=null) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}
			// 执行SQL获得结果集
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();
			// 获得结果集列数
			columnCount = rsmd.getColumnCount();
			// 将ResultSet的结果保存到List中
			if (rs.next()) {
				map = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					map.put(rsmd.getColumnLabel(i), rs.getObject(i));
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			// 释放资源
			if(null!=ps) ps.close();
			if(null!=rs) rs.close();
		}
		return null!=map?new DataRow(map):null;
	}
}
