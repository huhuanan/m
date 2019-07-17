package m.system.db;

public class DBConfig {
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	private static String initConnect;
	private static Integer maxConnect;
	/**
	 * 
	 * @param flag true为自动关闭连接, false不关闭连接
	 */
	public static void initConfig(String driver,String url,String username,String password){
		DBConfig.driver=driver;
		DBConfig.url=url;
		DBConfig.username=username;
		DBConfig.password=password;
	}
	
	public static String getDriver() {
		return driver;
	}
	public static String getUrl() {
		return url;
	}
	public static String getUsername() {
		return username;
	}
	public static String getPassword() {
		return password;
	}

	public static String getInitConnect() {
		return initConnect;
	}
	public static void setInitConnect(String initConnect) {
		DBConfig.initConnect = initConnect;
	}

	public static Integer getMaxConnect() {
		return maxConnect;
	}

	public static void setMaxConnect(Integer maxConnect) {
		DBConfig.maxConnect = maxConnect;
	}
}
