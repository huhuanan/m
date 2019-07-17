package manage.service;

import java.sql.SQLException;

import m.common.service.Service;
import m.system.db.DBManager;

public class SystemLogService extends Service {

	public String clear() throws SQLException {
		DBManager.executeUpdate("delete  from os_system_log");
		return "清除成功";
	}
	
}
