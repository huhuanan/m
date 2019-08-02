package m.system.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {
	private Boolean isUse;
	private Connection connection;
	private DBConnection(Boolean isUse,Connection connection){
		this.isUse=isUse;
		this.connection=connection;
	}
	//////////
	public static int getUseLinkNum(){
		if(null==conns) return -1;
		int n=0;
		for(DBConnection db : conns){
			if(null!=db && db.isUse) n++;
		}
		return n;
	}
	static DBConnection[] conns=null;
	static Map<String,Integer> connlist=new HashMap<String,Integer>();
	protected static Connection get() {
		if(null==conns) {
			synchronized(DBConnection.class) {
				if(null==conns)
					conns=new DBConnection[DBConfig.getMaxConnect()];
			}
		}
		synchronized(DBConnection.class) {
			for(int i=0;i<conns.length;i++){
				DBConnection conn=conns[i];
				if(null==conn||null==conn.connection){
					//System.out.println(Thread.currentThread().getName()+"-----------创建数据库连接--");
					conns[i]=new DBConnection(true, createConnection());
					connlist.put(Thread.currentThread().getName(), i);
					//System.out.println("=======获取连接====>>>>>"+i);
					return conns[i].connection;
				}else if(!conn.isUse){
					try {
						if(!conn.connection.isValid(1)){
							conn.connection=null;
							System.out.println(Thread.currentThread().getName()+"-----------数据库连接超时--");
							return get();
						}
					} catch (SQLException e) {
						conn.connection=null;
						System.out.println(Thread.currentThread().getName()+"-----------数据库连接验证异常--");
						return get();
					}
					conn.isUse=true;
					connlist.put(Thread.currentThread().getName(), i);
					//System.out.println("=======获取连接====>>>>>"+i);
					return conn.connection;
				}
			}
		}
		return null;
	}
	protected static void close() {
		if(null==conns)return;
		Integer index=connlist.get(Thread.currentThread().getName());
		if(null==index)return;
		DBConnection conn=conns[index];
		if(null!=conn&&conn.isUse){
//			try {
//				conn.connection.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
			//System.out.println("=======释放连接====<<<<<"+index);
			conn.isUse=false;
		}
	}
	protected static void closeAll() {
		for(DBConnection conn : conns) {
			try {
				if(null!=conn&&null!=conn.connection)
					conn.connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			conn.isUse=false;
		}
	}
	
	private static Connection createConnection(){
		try {
			Class.forName(DBConfig.getDriver());
			Connection conn = DriverManager.getConnection(DBConfig.getUrl(), DBConfig.getUsername(), DBConfig.getPassword());
			conn.setAutoCommit(true);
			if(null!=DBConfig.getInitConnect()&&!"".equals(DBConfig.getInitConnect())){
				Statement ps=null;
				try {
					ps=conn.createStatement();
					ps.executeUpdate(DBConfig.getInitConnect());
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					ps.close();
				}
			}
			return conn;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
