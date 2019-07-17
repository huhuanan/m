package manage.service;

import java.sql.SQLException;

import m.common.model.util.ModelCheckUtil;
import m.common.model.util.ModelUpdateUtil;
import m.common.service.Service;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.db.TransactionManager;
import m.system.exception.MException;
import m.system.util.GenerateID;
import m.system.util.StringUtil;
import manage.dao.GroupMenuLinkDao;
import manage.model.AdminGroup;

public class AdminGroupService extends Service {

	public String save(AdminGroup model) throws MException, SQLException {
		ModelCheckUtil.checkUniqueCombine(model, new String[]{"name"});
		if(StringUtil.isSpace(model.getOid())){
			model.setOid(GenerateID.generatePrimaryKey());
			model.setStatus("0");
			ModelUpdateUtil.insertModel(model);
			return "保存成功";
		}else{
			ModelUpdateUtil.updateModel(model, new String[]{"name","description","sort"});
			return "修改成功";
		}
	}
	public void delete(AdminGroup model) throws Exception {
		if(StringUtil.noSpace(model.getOid()).length()==1){
			throw new MException(this.getClass(),"默认管理员组不能删除");
		}
		DataRow dr=DBManager.queryFirstRow("select count(oid) num from os_admin_login where admin_group_oid=?",new String[]{model.getOid()});
		if(null!=dr&&dr.get(Long.class, "num")>0){
			throw new MException(this.getClass(),"组内有对应得管理员,不能删除");
		}
		TransactionManager tm=new TransactionManager();
		try{
			tm.begin();
			getDao(GroupMenuLinkDao.class).removeAllGroupMenuLink(model.getOid());
			ModelUpdateUtil.deleteModel(model);
			tm.commit();
		}catch(Exception e){
			tm.rollback();
			throw e;
		}
	}
}
