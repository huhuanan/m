package m.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import m.common.action.Action;
import m.common.dao.Dao;
import m.common.model.HostInfo;
import m.common.model.SystemInfoModel;
import m.common.service.HostInfoService;
import m.common.service.Service;
import m.system.exception.MException;
import m.system.util.ClassUtil;
import m.system.util.StringUtil;

public class RuntimeData {
	private static Map<String,Class<? extends Action>> actionData;
	public static Map<String, Class<? extends Action>> getActionData() {
		return actionData;
	}
	//set
	public static <T extends Action> void fillAction(String action_name,Class<T> action_class){
		if(null==actionData){
			actionData=new HashMap<String, Class<? extends Action>>();
		}
		actionData.put(action_name, action_class);
	}
	//get
	public static Class<? extends Action> getAction(String action_name){
		if(null==actionData){
			return null;
		}
		return actionData.get(action_name);
	}
	private static Map<Class<? extends Service>,Service> serviceData;
	/**
	 * 获取service实例
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws MException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Service> T getService(Class<? extends T> clazz) throws MException{
		if(null==serviceData){
			serviceData=new HashMap<Class<? extends Service>, Service>();
		}
		T service=(T) serviceData.get(clazz);
		if(null==service){
			service=ClassUtil.newInstance(clazz);
			serviceData.put(clazz, service);
		}
		return service;
	}
	private static Map<Class<? extends Dao>,Dao> daoData;
	/**
	 * 获取dao实例
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws MException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Dao> T getDao(Class<? extends T> clazz) throws MException{
		if(null==daoData){
			daoData=new HashMap<Class<? extends Dao>, Dao>();
		}
		T dao=(T) daoData.get(clazz);
		if(null==dao){
			dao=ClassUtil.newInstance(clazz);
			daoData.put(clazz, dao);
		}
		return dao;
	}

	private static String classPath;
	private static String domainClass;
	private static String logClass;
	private static String systemClass;
	private static String webPath;
	private static String serverIp;
	private static int serverPort;
	private static String filePath;
	private static String secretField;
	private static String staticField;
	private static HostInfo hostInfo;//主机信息,
	private static Boolean debug;
	
	private static Set<String> staticPropertyList=null;
	public static StringBuffer testStaticDomain(String property){
		if(null==staticPropertyList) {
			synchronized (RuntimeData.class) {
				if(null==staticPropertyList){
					staticPropertyList=new HashSet<String>();
					String[] sp=staticField.split(",");
					for(String s : sp){
						staticPropertyList.add(s);
					}
				}
			}
		}
		if(staticPropertyList.contains(property)){
			return new StringBuffer(getStaticDomain());
		}else{
			return new StringBuffer();
		}
	}
	private static Set<String> secretFieldList=null;
	public static boolean isSecretField(String property) {
		if(null==secretFieldList) {
			synchronized (RuntimeData.class) {
				if(null==secretFieldList) {
					secretFieldList=new HashSet<String>();
					String[] sf=secretField.split(",");
					for(String s : sf) {
						secretFieldList.add(s);
					}
				}
			}
		}
		return secretFieldList.contains(property);
	}
	private static long lastLong=0l;
	private static String staticDomain="";
	private static String staticMode="";
	public static String getStaticDomain() {
		if((!StringUtil.isSpace(systemClass))&&System.currentTimeMillis()-lastLong>5000) {
			try {
				lastLong=System.currentTimeMillis();
				SystemInfoModel model=((SystemInfoModel)ClassUtil.newInstance(systemClass)).getUniqueModel();
				if(null!=model) {
					staticDomain=StringUtil.noSpace(model.getStaticDomain());
					staticMode=StringUtil.noSpace(model.getStaticMode());
				}
			} catch (MException e) {
				e.printStackTrace();
			}
		}
		if("A".equals(staticMode)) {
			return staticDomain;
		}else if("B".equals(staticMode)) {
			return HostInfoService.getRandomIP(null);
		}else if("C".equals(staticMode)) {
			return HostInfoService.getRandomIP(staticDomain);
		}else {
			return "";
		}
	}
	/**
	 * 是否允许随意访问文件路径
	 * @return
	 */
	public static boolean accessFilePath() {
		if("A".equals(staticMode)||"C".equals(staticMode)) {
			return true;
		}else {
			return false;
		}
	}
	
	public static String getClassPath() {
		return classPath;
	}
	public static void setClassPath(String classPath) {
		RuntimeData.classPath = classPath;
	}
	public static String getWebPath() {
		return webPath;
	}
	public static void setWebPath(String webPath) {
		RuntimeData.webPath = webPath;
	}
	public static String getSecretField() {
		return secretField;
	}
	public static void setSecretField(String secretField) {
		RuntimeData.secretField = secretField;
	}
	public static String getFilePath() {
		return filePath;
	}
	public static void setFilePath(String filePath) {
		RuntimeData.filePath = filePath;
	}
	public static Boolean getDebug() {
		return debug;
	}
	public static void setDebug(Boolean debug) {
		RuntimeData.debug = debug;
	}
	public static String getDomainClass() {
		return domainClass;
	}
	public static void setDomainClass(String domainClass) {
		RuntimeData.domainClass = domainClass;
	}
	public static String getLogClass() {
		return logClass;
	}
	public static void setLogClass(String logClass) {
		RuntimeData.logClass = logClass;
	}
	public static String getSystemClass() {
		return systemClass;
	}
	public static void setSystemClass(String systemClass) {
		RuntimeData.systemClass = systemClass;
	}
	public static String getServerIp() {
		return serverIp;
	}
	public static void setServerIp(String serverIp) {
		RuntimeData.serverIp = serverIp;
	}
	public static int getServerPort() {
		return serverPort;
	}
	public static void setServerPort(int serverPort) {
		RuntimeData.serverPort = serverPort;
	}
	public static HostInfo getHostInfo() {
		return hostInfo;
	}
	public static void setHostInfo(HostInfo hostInfo) {
		RuntimeData.hostInfo = hostInfo;
	}
	public static String getStaticField() {
		return staticField;
	}
	public static void setStaticField(String staticField) {
		RuntimeData.staticField = staticField;
	}
	
}
