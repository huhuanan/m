package manage.run;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;

import m.common.model.util.ModelQueryList;
import m.system.SystemSessionTask;
import manage.model.AdminLogin;

public class AdminLoginSessionTask extends SystemSessionTask {

	@Override
	public void created(HttpSessionEvent e) {
		addSessionNum(1);
	}

	@Override
	public void destroyed(HttpSessionEvent e) {
		addSessionNum(-1);
	}

	@Override
	public void added(String key, Object value) {
		if("login_admin_oid".equals(key)) {
			addLoginNum(1);
		}
	}

	@Override
	public void removed(String key, Object value) {
		if("login_admin_oid".equals(key)) {
			sessionAdminMap.remove(value.toString());
			addLoginNum(-1);
		}
	}

	private static Map<String,AdminLogin> sessionAdminMap=new HashMap<String, AdminLogin>();

	public static void setSessionAdminOid(HttpSession session,String oid) {
		session.setAttribute("login_admin_oid", oid);
	}
	public static void removeSessionAdminOid(HttpSession session) {
		session.removeAttribute("login_admin_oid");
	}
	public static Object getSessionAdminOid(HttpSession session) {
		return session.getAttribute("login_admin_oid");
	}
	/**
	 * 获取sessionAdmin缓存
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public static AdminLogin getSessionAdmin(String oid) throws Exception{
		if(null==sessionAdminMap.get(oid)) {
			AdminLogin admin=new AdminLogin();
			admin.setOid(oid);
			admin=ModelQueryList.getModel(admin,1);
			admin.setPassword("");
			sessionAdminMap.put(admin.getOid(), admin);
		}
		return sessionAdminMap.get(oid);
	}
	/**
	 * 重置sessionAdmin缓存
	 * @param adminOid
	 */
	public static void resetSessionAdmin(String adminOid){
		AdminLogin session=sessionAdminMap.get(adminOid);
		if(null!=session){
			try {
				getSessionAdmin(adminOid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
