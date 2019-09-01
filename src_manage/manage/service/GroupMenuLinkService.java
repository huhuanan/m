package manage.service;

import java.sql.SQLException;
import java.util.List;

import m.common.model.util.ModelUpdateUtil;
import m.common.service.Service;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.db.TransactionManager;
import m.system.exception.MException;
import m.system.util.GenerateID;
import m.system.util.StringUtil;
import manage.model.GroupMenuLink;

public class GroupMenuLinkService extends Service {

	private String getGroupMenuLinkOid(GroupMenuLink model) throws SQLException{
		DataRow dr=DBManager.queryFirstRow("select oid from os_group_menu_link gm where gm.admin_group_oid=? and gm.module_oid=? and gm.menu_oid=?",
			new String[]{model.getAdminGroup().getOid(),model.getModule().getOid(),model.getMenu().getOid()});
		if(null!=dr){
			return dr.get(String.class,"oid");
		}else{
			return null;
		}
	}
	public void addGroupMenuLink(GroupMenuLink model) throws SQLException, MException{
		model.setOid(getGroupMenuLinkOid(model));
		if(StringUtil.isSpace(model.getOid())){
			model.setOid(GenerateID.generatePrimaryKey());
			ModelUpdateUtil.insertModel(model);
		}
	}
	public void removeGroupMenuLink(GroupMenuLink model) throws SQLException, MException{
		model.setOid(getGroupMenuLinkOid(model));
		if(model.getOid().length()==1){
			throw new MException(this.getClass(),"初始化权限不能取消!");
		}
		if(!StringUtil.isSpace(model.getOid())){
			ModelUpdateUtil.deleteModel(model);
		}
	}
	public void saveAll(String adminGroupOid,List<GroupMenuLink> modelList) throws Exception {
		TransactionManager tm=new TransactionManager();
		try {
			tm.begin();
			DBManager.executeUpdate("delete from os_group_menu_link where length(oid)>1 and admin_group_oid=?",new String[] {adminGroupOid});
			for(GroupMenuLink model : modelList) {
				addGroupMenuLink(model);
			}
			tm.commit();
		}catch(Exception e) {
			tm.rollback();
			throw e;
		}
	}
}
