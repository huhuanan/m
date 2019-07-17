package m.system.listener;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import m.common.socket.HostSocketUtil;
import m.system.RuntimeData;
import m.system.SystemInit;
import m.system.db.DBConfig;
import m.system.db.TransactionManager;
import m.system.util.StringUtil;

public class InitListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg0) {
		HostSocketUtil.closeClient();
		HostSocketUtil.closeServer();
		TransactionManager.closeConnection();
		TransactionManager.closeAllConection();
	}
	public void contextInitialized(ServletContextEvent arg0) {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/mconfig.properties");
		InputStream dbStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("dbconfig.properties");
		Properties p = new Properties();
		Properties dbp = new Properties();
		try {
			p.load(inputStream);
			dbp.load(dbStream);
			//加载配置信息
			RuntimeData.setClassPath(Thread.currentThread().getContextClassLoader().getResource("/").getPath());
			RuntimeData.setWebPath(RuntimeData.getClassPath().substring(0, RuntimeData.getClassPath().indexOf("WEB-INF/classes/")));
			RuntimeData.setFilePath(StringUtil.noSpace(p.getProperty("file_path")));
			RuntimeData.setSecretField(StringUtil.noSpace(p.getProperty("secret_field")));
			RuntimeData.setStaticField(StringUtil.noSpace(p.getProperty("static_field")));
			RuntimeData.setDebug(StringUtil.noSpace(p.getProperty("debug")).equals("true")?true:false);
			RuntimeData.setDomainClass(StringUtil.noSpace(p.getProperty("domain_class")));
			RuntimeData.setLogClass(StringUtil.noSpace(p.getProperty("log_class")));
			RuntimeData.setSystemClass(StringUtil.noSpace(p.getProperty("systeminfo_class")));
			
			DBConfig.initConfig(dbp.getProperty("db_driver"), dbp.getProperty("db_url"), dbp.getProperty("db_username"), dbp.getProperty("db_password"));
			DBConfig.setInitConnect(dbp.getProperty("db_init_connect"));
			DBConfig.setMaxConnect(Integer.parseInt(dbp.getProperty("db_max_connect","20")));
			TransactionManager.initConnection();
			String tableSchema=dbp.getProperty("table_schema");
			if(!StringUtil.isSpace(tableSchema)){
				SystemInit.setTableSchema(tableSchema);
			}
			String modelPack=StringUtil.noSpace(p.getProperty("model_pack"));
			SystemInit.initModel(modelPack);
			
			String taskClass=StringUtil.noSpace(p.getProperty("task_class"));
			SystemInit.setTaskClass(taskClass);
			String initClass=StringUtil.noSpace(p.getProperty("init_class"));
			SystemInit.setInitClass(initClass);
			
			String server_ip=StringUtil.noSpace(dbp.getProperty("server_ip"));
			int server_port=StringUtil.isSpace(dbp.getProperty("server_port"))?8128:Integer.parseInt(dbp.getProperty("server_port"));
			RuntimeData.setServerIp(server_ip);
			RuntimeData.setServerPort(server_port);
			SystemInit.initServerGroup(server_ip, server_port);
			
			String actionPack=StringUtil.noSpace(p.getProperty("action_pack"));
			SystemInit.initAction(actionPack);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**本地执行sql时调用
	 * 初始化数据库相关
	 */
	public static void initDBConfig(){
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/mconfig.properties");
		InputStream dbStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("dbconfig.properties");
		Properties p = new Properties();
		Properties dbp = new Properties();
		try {
			p.load(inputStream);
			dbp.load(dbStream);
			//加载配置信息
			RuntimeData.setClassPath(Thread.currentThread().getContextClassLoader().getResource("/").getPath());
			//RuntimeData.setWebPath(RuntimeData.getClassPath().substring(0, RuntimeData.getClassPath().indexOf("WEB-INF/classes/")));
			RuntimeData.setFilePath(StringUtil.noSpace(p.getProperty("file_path")));
			RuntimeData.setSecretField(StringUtil.noSpace(p.getProperty("secret_field")));
			RuntimeData.setStaticField(StringUtil.noSpace(p.getProperty("static_field")));
			RuntimeData.setDebug(StringUtil.noSpace(p.getProperty("debug")).equals("true")?true:false);
			RuntimeData.setDomainClass(StringUtil.noSpace(p.getProperty("domain_class")));
			RuntimeData.setLogClass(StringUtil.noSpace(p.getProperty("log_class")));
			RuntimeData.setSystemClass(StringUtil.noSpace(p.getProperty("systeminfo_class")));
			
			DBConfig.initConfig(dbp.getProperty("db_driver"), dbp.getProperty("db_url"), dbp.getProperty("db_username"), dbp.getProperty("db_password"));
			DBConfig.setInitConnect(dbp.getProperty("db_init_connect"));
			DBConfig.setMaxConnect(Integer.parseInt(dbp.getProperty("db_max_connect","20")));
			TransactionManager.initConnection();
			String tableSchema=dbp.getProperty("table_schema");
			if(!StringUtil.isSpace(tableSchema)){
				SystemInit.setTableSchema(tableSchema);
			}
			String modelPack=StringUtil.noSpace(p.getProperty("model_pack"));
			SystemInit.initModel(modelPack);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
