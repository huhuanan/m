package m;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.common.model.util.ModelQueryList;
import m.common.model.util.QueryPage;
import m.system.exception.MException;
import m.system.listener.InitListener;
import manage.model.AdminLogin;

public class Test {
	public static void main(String[] a) throws SQLException, MException {
		InitListener.initDBConfig();
		
		Map<String,String> eMap=new HashMap<String,String>();
		eMap.put("loginCount", "sum(#{loginCount})");
		List<AdminLogin> list=ModelQueryList.getModelList(AdminLogin.class, new String[]{"realname"}, new QueryPage(0,10),null,eMap,true, null);
		
		System.out.println(list.size());
		
	}
}
