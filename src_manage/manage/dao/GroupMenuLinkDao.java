package manage.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import m.common.dao.Dao;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.db.DataSet;

public class GroupMenuLinkDao extends Dao {
	public Map<String,Object> getGroupMenuLink(String group_oid) throws SQLException{
		Map<String,Object> map=new HashMap<String, Object>();
		DataSet ds=DBManager.executeQuery("SELECT menu_oid,oid FROM os_group_menu_link where admin_group_oid=?", new String[]{group_oid});
		for(DataRow dr : ds.rows()){
			map.put(dr.get(String.class,"menu_oid"), dr.get(String.class,"oid"));
		}
		return map;
	}
	public String getMenuOid(String group_oid,String menu_oid) throws SQLException{
		DataRow row=DBManager.queryFirstRow("SELECT mi.oid FROM os_menu_info mi left join os_group_menu_link gm on mi.oid=gm.menu_oid and gm.admin_group_oid=?  where mi.oid=? and (gm.menu_oid=? or mi.is_public='Y')", new String[]{group_oid,menu_oid,menu_oid});
		if(null==row){
			return null;
		}else{
			return row.get(String.class, "oid");
		}
	}
	public void removeAllGroupMenuLink(String admin_group_oid) throws SQLException{
		DBManager.executeUpdate("delete from os_group_menu_link where admin_group_oid=?",new String[]{admin_group_oid});
	}
}
