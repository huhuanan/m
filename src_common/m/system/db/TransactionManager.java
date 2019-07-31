package m.system.db;

import java.sql.Connection;
import java.sql.SQLException;

import m.system.cache.CacheUtil;
import m.system.exception.MException;
import m.system.util.StringUtil;
/**
 * 最外层事务生效
 * @author Administrator
 *
 */
public class TransactionManager {
	//定义ThreadLocal静态变量，确定存取类型为Connection
	private static ThreadLocal<Connection> dbConnection = new ThreadLocal<Connection>(); 
	private static ThreadLocal<Boolean> dbRun = new ThreadLocal<Boolean>();  
	
	private Boolean isRun=false;
	private String synchKey;
	public TransactionManager(){
		if(null==dbRun.get()){
			dbRun.set(false);
		}
	}
	private static int initCount=0;
	/**
	 * 线程开始   初始化数据库连接
	 * @throws MException
	 */
	public static void initConnection() throws MException {
		if(null!=dbConnection.get()) return;
		Connection conn=DBConnection.get();
		if(null!=conn){
			try {//设置自动提交
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			//将Connection设置到ThreadLocal
			dbConnection.set(conn);
		}else{
			if(initCount>50) throw new MException(TransactionManager.class, "初始化数据库连接超时");
			initCount++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			initConnection();
		}
	}

	/**
	 * 线程结束前 关闭数据库连接
	 */
	public static void closeConnection() {
		DBConnection.close();
		dbConnection.remove(); 
	}
	/**
	 * 停止应用的时候调用 关闭所有连接  
	 */
	public static void closeAllConection() {
		DBConnection.closeAll();
	}
	protected static Connection getConnection() {
		Connection conn = dbConnection.get();
		//如果在当前线程中没有绑定相应的Connection
//		if (conn==null) {
//			conn=createConnection();
//		}else{
//			try {
//				if(!conn.isValid(1)){
//					throw new MException(TransactionManager.class,"连接无效");
//				}
////				if(conn instanceof com.mysql.jdbc.Connection) {
////					((com.mysql.jdbc.Connection)conn).ping();//ping通mysql方法，如果超时会抛异常  
////				}
//			} catch (Exception e) {
//				conn=createConnection();
//				System.out.println("数据库链接超时,自动重新创建!"+e.getMessage());
//			}
//		}
		return conn;
	}


	/**
	 * 事务开始
	 * @throws SQLException 
	 */
	public void begin() throws SQLException { 
		if(!dbRun.get()&&!isRun){
			dbRun.set(true);
			isRun=true;
			Connection conn = getConnection();
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false); //手动提交
			}
		}
	}
	/**
	 * 开始事务 
	 * @param key 唯一执行主键 20个长度
	 * @throws Exception
	 */
	public void begin(String key) throws Exception {
		begin();
		if(!StringUtil.isSpace(key)) {
			synchKey=CacheUtil.executeSynch(key);
		}
	}
	 
	/** 
	 * 事务提交 
	 * @throws SQLException 
	 */
	public void commit() throws SQLException {
		if(!StringUtil.isSpace(synchKey)) {
			CacheUtil.releaseSynch(synchKey);
			synchKey=null;
		}
		if(dbRun.get()&&isRun){
			dbRun.remove();
			isRun=false;
			Connection conn = getConnection();
			if (!conn.getAutoCommit()) {
				conn.commit();
				conn.setAutoCommit(true);
			}
		}
	}

	/** 
	 * 事务回滚 
	 * @throws SQLException 
	 */
	public void rollback() {
		if(!StringUtil.isSpace(synchKey)) {
			CacheUtil.releaseSynch(synchKey);
			synchKey=null;
		}
		if(dbRun.get()&&isRun){
			dbRun.remove();
			isRun=false;
			Connection conn = getConnection();
			try {
				if (!conn.getAutoCommit()) {
					conn.rollback();
					conn.setAutoCommit(true);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
