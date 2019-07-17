package manage.service;

import java.sql.SQLException;

import m.common.model.util.ModelUpdateUtil;
import m.common.service.Service;
import m.system.db.DBManager;
import m.system.db.DataRow;
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
}
