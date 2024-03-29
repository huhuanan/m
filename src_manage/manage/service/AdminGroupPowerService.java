package manage.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.common.model.util.ModelQueryList;
import m.common.model.util.ModelUpdateUtil;
import m.common.model.util.QueryCondition;
import m.common.service.Service;
import m.system.db.DBManager;
import m.system.db.DataRow;
import m.system.exception.MException;
import m.system.util.GenerateID;
import m.system.util.StringUtil;
import manage.model.AdminGroupLink;
import manage.model.AdminGroupPower;
import manage.model.AdminLogin;
import manage.run.ModuleInitRun;

public class AdminGroupPowerService extends Service {
	/**
	 * 获取用户对应的权限
	 * @param admin_oid
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public Map<String,Boolean> getPowerMap(String admin_oid) throws SQLException, MException{
		Map<String,Boolean> powerMap=new HashMap<String, Boolean>();
		List<AdminGroupPower> list=ModelQueryList.getModelList(AdminGroupPower.class, new String[] {"name"}, null,
			QueryCondition.or(new QueryCondition[]{
				QueryCondition.in("adminGroup.oid",
					ModelQueryList.instance(AdminLogin.class,
						new String[] {"adminGroup.oid"}, null,
						QueryCondition.eq("oid", admin_oid)
					)
				),
				QueryCondition.in("adminGroup.oid",
					ModelQueryList.instance(AdminGroupLink.class,
						new String[] {"adminGroup.oid"}, null,
						QueryCondition.eq("admin.oid", admin_oid)
					)
				)
			})
		);
		for(AdminGroupPower power : list){
			powerMap.put(power.getName(), true);
		}
		return powerMap;
	}
	public List<String> getPowerTitleList(String admin_oid) throws SQLException, MException{
		List<String> powerList=new ArrayList<String>();
		Map<String,Boolean> powerMap=getPowerMap(admin_oid);
		List<String[]> plist=ModuleInitRun.getPowerList();
		for(String[] ps : plist){
			Boolean b=powerMap.get(ps[0]);
			if(null!=b&&powerMap.get(ps[0])) {
				powerList.add(ps[1]);
			}
		}
		return powerList;
	}
	/**
	 * 获取用户组对应的权限
	 * @param admin_group_oid
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public Map<String,Boolean> getPowerMapByGroup(String admin_group_oid) throws SQLException, MException{
		Map<String,Boolean> powerMap=new HashMap<String, Boolean>();
		List<AdminGroupPower> list=ModelQueryList.getModelList(AdminGroupPower.class, new String[] {"name"}, null,
			QueryCondition.eq("adminGroup.oid",admin_group_oid)
		);
		for(AdminGroupPower power : list){
			powerMap.put(power.getName(), true);
		}
		return powerMap;
	}
	private String getAdminGroupPowerOid(AdminGroupPower model) throws SQLException{
		DataRow dr=DBManager.queryFirstRow("select oid from os_admin_group_power gm where gm.admin_group_oid=? and gm.name=?",
			new String[]{model.getAdminGroup().getOid(),model.getName()});
		if(null!=dr){
			return dr.get(String.class,"oid");
		}else{
			return null;
		}
	}
	public void addAdminGroupPower(AdminGroupPower model) throws SQLException, MException{
		model.setOid(getAdminGroupPowerOid(model));
		if(StringUtil.isSpace(model.getOid())){
			model.setOid(GenerateID.generatePrimaryKey());
			ModelUpdateUtil.insertModel(model);
		}
	}
	public void removeAdminGroupPower(AdminGroupPower model) throws SQLException, MException{
		model.setOid(getAdminGroupPowerOid(model));
		if(model.getOid().length()==1){
			throw new MException(this.getClass(),"初始化权限不能取消!");
		}
		if(!StringUtil.isSpace(model.getOid())){
			ModelUpdateUtil.deleteModel(model);
		}
	}
}
