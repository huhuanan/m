package manage.service;

import java.sql.SQLException;
import java.util.List;

import m.common.model.util.ModelQueryList;
import m.common.model.util.QueryCondition;
import m.common.model.util.QueryOrder;
import m.common.model.util.QueryPage;
import m.common.service.Service;
import m.system.exception.MException;
import manage.model.AdminGroup;
import manage.model.GroupMenuLink;
import manage.model.MenuInfo;
import manage.model.ModuleInfo;

public class ModuleService extends Service {
	/**
	 * 获取管理员组模块
	 * @param group
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public List<ModuleInfo> getModuleList4Group(AdminGroup group) throws SQLException, MException{
		return ModelQueryList.getModelList(ModuleInfo.class, 
				new String[]{"oid","name","urlPath","icoStyle"}, 
				new QueryPage(0,100), 
				QueryCondition.or(new QueryCondition[]{QueryCondition.eq("isPublic", "Y"),
					QueryCondition.in("oid", 
						ModelQueryList.instance(GroupMenuLink.class, 
							new String[]{"module.oid"}, 
							null, 
							QueryCondition.eq("adminGroup.oid",group.getOid())
						)
					)
				}), 
				QueryOrder.asc("sort")
			);
	}
	public List<ModuleInfo> getModuleList() throws SQLException, MException{
		return ModelQueryList.getModelList(ModuleInfo.class, 
				new String[]{"oid","name","urlPath","icoStyle","isPublic"}, 
				new QueryPage(0,100), 
				null, 
				QueryOrder.asc("sort")
			);
	}
	/**
	 * 获取管理员组菜单
	 * @param module_oid
	 * @param group
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public List<MenuInfo> getMenuList4Group(String module_oid,AdminGroup group) throws SQLException, MException {
		QueryCondition condition=QueryCondition.and(new QueryCondition[]{
				QueryCondition.eq("moduleInfo.oid", module_oid),
				QueryCondition.isEmpty("parentMenu.oid"),
				QueryCondition.or(new QueryCondition[]{QueryCondition.eq("isPublic", "Y"),
					QueryCondition.in("oid", 
						ModelQueryList.instance(GroupMenuLink.class, 
							new String[]{"menu.oid"}, 
							null, 
							QueryCondition.eq("adminGroup.oid",group.getOid())
						)
					)
				}), 
			});
		return ModelQueryList.getModelList(MenuInfo.class,
				new String[]{"oid","name","urlPath","icoStyle","description","todoClass"},
				new QueryPage(0,100),
				condition,
				QueryOrder.asc("sort")
			);
	}
	public List<MenuInfo> getMenuList(String module_oid) throws SQLException, MException {
		return ModelQueryList.getModelList(MenuInfo.class,
				new String[]{"oid","name","urlPath","icoStyle","isPublic","description","todoClass"},
				new QueryPage(0,100),
				QueryCondition.and(new QueryCondition[]{
					QueryCondition.eq("moduleInfo.oid", module_oid),
					QueryCondition.isEmpty("parentMenu.oid")
				}),
				QueryOrder.asc("sort")
			);
	}
	/**
	 * 获取管理员组菜单
	 * @param module_oid
	 * @param group
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public List<MenuInfo> getMenuList4Group(String module_oid,String menu_oid,AdminGroup group) throws SQLException, MException {
		QueryCondition condition=QueryCondition.and(new QueryCondition[]{
				QueryCondition.eq("moduleInfo.oid", module_oid),
				QueryCondition.eq("parentMenu.oid", menu_oid),
				QueryCondition.or(new QueryCondition[]{QueryCondition.eq("isPublic", "Y"),
					QueryCondition.in("oid", 
						ModelQueryList.instance(GroupMenuLink.class, 
							new String[]{"menu.oid"}, 
							null, 
							QueryCondition.eq("adminGroup.oid",group.getOid())
						)
					)
				}), 
			});
		return ModelQueryList.getModelList(MenuInfo.class,
				new String[]{"oid","name","urlPath","icoStyle","description"},
				new QueryPage(0,100),
				condition,
				QueryOrder.asc("sort")
			);
	}
	public List<MenuInfo> getMenuList(String module_oid,String menu_oid) throws SQLException, MException {
		return ModelQueryList.getModelList(MenuInfo.class,
				new String[]{"oid","name","urlPath","icoStyle","isPublic","description"},
				new QueryPage(0,100),
				QueryCondition.and(new QueryCondition[]{
					QueryCondition.eq("moduleInfo.oid", module_oid),
					QueryCondition.eq("parentMenu.oid", menu_oid)
				}),
				QueryOrder.asc("sort")
			);
	}
}
