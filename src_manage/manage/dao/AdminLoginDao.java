package manage.dao;

import java.sql.SQLException;
import java.util.Date;

import m.common.dao.Dao;
import m.common.model.util.ModelUpdateUtil;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.exception.MException;
import manage.model.AdminLogin;

public class AdminLoginDao extends Dao {
	/**
	 * 根据用户名和密码获取oid
	 * @param username
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public String getUserOid(String username,String password) throws SQLException{
		DataRow dr=DBManager.queryFirstRow("select oid from os_admin_login where username=? and password=?",new String[]{username,password});
		if(null!=dr){
			return dr.get(String.class,"oid");
		}else{
			return null;
		}
	}
	/**
	 * 更新最后登录时间和ip
	 * @param admin
	 * @param ip
	 * @throws MException
	 */
	public void updateLastInfo(AdminLogin admin,String ip) throws MException{
		admin.setLastLoginTime(new Date());
		admin.setLastLoginIp(ip);
		if(null==admin.getLoginCount()){
			admin.setLoginCount(1);
		}else{
			admin.setLoginCount(admin.getLoginCount()+1);
		}
		ModelUpdateUtil.updateModel(admin, new String[]{"lastLoginTime","lastLoginIp","loginCount"});
	}
}
