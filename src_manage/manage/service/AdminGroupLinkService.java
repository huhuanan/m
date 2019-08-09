package manage.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.common.model.util.ModelQueryList;
import m.common.model.util.QueryCondition;
import m.common.service.Service;
import m.system.db.DBManager;
import m.system.db.TransactionManager;
import m.system.exception.MException;
import m.system.util.GenerateID;
import manage.model.AdminGroupLink;

public class AdminGroupLinkService extends Service {
	
	public void setLink(String adminGroupOid,String[] adminOids) throws Exception {
		TransactionManager tm=new TransactionManager();
		try {
			tm.begin();
			DBManager.executeUpdate("delete from os_admin_group_link where admin_group_oid=?",new String[] {adminGroupOid});
			List<Object[]> list=new ArrayList<Object[]>();
			for(String oid : adminOids) {
				list.add(new Object[] {GenerateID.generatePrimaryKey(),adminGroupOid,oid});
			}
			DBManager.batchUpdate("insert into os_admin_group_link(oid,admin_group_oid,admin_oid) value(?,?,?)", list);
			tm.commit();
		}catch(Exception e) {
			tm.rollback();
			throw e;
		}
	}
	public Map<String,Boolean> getLink(String adminGroupOid) throws SQLException, MException{
		List<AdminGroupLink> list=ModelQueryList.getModelList(AdminGroupLink.class, new String[] {"*"}, null, QueryCondition.eq("adminGroup.oid", adminGroupOid));
		Map<String,Boolean> map=new HashMap<String, Boolean>();
		for(AdminGroupLink link : list) {
			map.put(link.getAdmin().getOid(), true);
		}
		return map;
	}
}
