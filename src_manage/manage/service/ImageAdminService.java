package manage.service;

import java.sql.SQLException;

import m.common.service.Service;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.exception.MException;

public class ImageAdminService extends Service {
	public String getOid(String token) throws SQLException, MException{
		DataRow row=DBManager.queryFirstRow("select oid from v_image_admin where token=?",new String[]{token});
		if(null!=row){
			return row.get(String.class,"oid");
		}
		throw new MException(this.getClass(),"登陆异常!");
	}
}
