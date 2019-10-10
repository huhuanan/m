package m.common.action;

import java.io.File;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import m.common.dao.Dao;
import m.common.model.LogModel;
import m.common.model.SessionModel;
import m.common.model.UserModel;
import m.common.service.Service;
import m.system.RuntimeData;
import m.system.cache.CacheUtil;
import m.system.exception.MException;
import m.system.util.ClassUtil;
import m.system.util.StringUtil;


public abstract class Action {
	public final static String SESSION_NAME="session_name";
	/**
	 * 获取当前操作员
	 * @return
	 */
	public UserModel getSessionLogUser(){
		return null;
	}
	private LogModel logModel=null;
	/**
	 * 设置日志的描述
	 * @param description
	 */
	public void setLogContent(String operType,String description){
		String logClass=RuntimeData.getLogClass();
		UserModel user=getSessionLogUser();
		if(!StringUtil.isSpace(logClass)&&null!=user){
			try {
				if(null==logModel){
					logModel=ClassUtil.newInstance(logClass.trim());
					logModel.setUsername(user.getUsername());
					logModel.setRealname(user.getRealname());
					logModel.setUserType(user.getUserType());
					logModel.setCreateDate(new Date());
					logModel.setOperIp(getIpAddress());
					logModel.setOperResult("请求完成");
				}
				logModel.setOperType(operType);
				logModel.setDescription(description);
			} catch (MException e) {
				e.printStackTrace();
			}
		}
	}
	public void setLogError(String errorMessage){
		if(null!=logModel){
			logModel.setOperResult("请求失败");
			logModel.setResultException(errorMessage);
		}
	}
	/**
	 * 获取service实例
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws MException
	 */
	public <T extends Service> T getService(Class<? extends T> clazz) throws MException{
		return RuntimeData.getService(clazz);
	}
	/**
	 * 获取dao实例
	 * @param <T>
	 * @param clazz
	 * @return
	 * @throws MException
	 */
	public <T extends Dao> T getDao(Class<? extends T> clazz) throws MException{
		return RuntimeData.getDao(clazz);
	}
	public Dao getDao() throws MException{
		return RuntimeData.getDao(Dao.class);
	}
	public Service getService() throws MException{
		return RuntimeData.getService(Service.class);
	}
	/**
	 * 设置 session model
	 * @param model
	 */
	public <T extends SessionModel> void setSessionModel(T model) {
		String sn=getSessionCookie()+"_login";
		if(null!=sn) {
			CacheUtil.push(sn, model);
		}
	}
	/**
	 * 获取 session model
	 * @param clazz
	 * @return
	 */
	public <T extends SessionModel> T getSessionModel(Class<T> clazz) {
		String sn=getSessionCookie()+"_login";
		if(null!=sn) {
			Object obj=CacheUtil.get(sn);
			if(null!=obj) {
				return (T) obj;
			}
		}
		return null;
	}
	/**
	 * 清除 session model
	 */
	public void removeSessionModel() {
		String sn=getSessionCookie()+"_login";
		if(null!=sn) {
			CacheUtil.clear(sn);
		}
	}
	/**
	 * 获取当前登录的session
	 * @return
	 */
	public String getSessionCookie() {
		Cookie[] cs=request.getCookies();
		if(null!=cs){
			for(int i=0;i<cs.length;i++){
				if(cs[i].getName().equals(Action.SESSION_NAME)){
					return cs[i].getValue();
				}
			}
		}
		return getAuthorization();
	}
	
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String authorization;
	private Map<String,File> fileMap;
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getAuthorization() {
		return authorization;
	}
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	/**
	 * 获取上传文件流的Map
	 * @return
	 */
	public Map<String, File> getFileMap() {
		return fileMap;
	}
	public void setFileMap(Map<String, File> fileMap) {
		this.fileMap = fileMap;
	}
	public LogModel getLogModel() {
		return logModel;
	}
	public void setLogModel(LogModel logModel) {
		this.logModel = logModel;
	}

	private String requestBody;
	public String getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	public String getIpAddress() { 
		String ip = getRequest().getHeader("x-forwarded-for"); 
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = getRequest().getHeader("Proxy-Client-IP"); 
		} 
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = getRequest().getHeader("WL-Proxy-Client-IP"); 
		} 
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = getRequest().getRemoteAddr(); 
		} 
		return ip;
	}
}
