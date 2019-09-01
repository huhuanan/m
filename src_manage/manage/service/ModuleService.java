package manage.service;

import java.sql.SQLException;
import java.util.List;

import m.common.model.util.ModelQueryList;
import m.common.model.util.QueryCondition;
import m.common.model.util.QueryOrder;
import m.common.model.util.QueryPage;
import m.common.service.Service;
import m.system.exception.MException;
import m.system.util.JSONMessage;
import manage.model.AdminGroup;
import manage.model.AdminGroupLink;
import manage.model.AdminLogin;
import manage.model.GroupMenuLink;
import manage.model.MenuInfo;
import manage.model.ModuleInfo;

public class ModuleService extends Service {
	
	public String fillModulesJSON(String key,JSONMessage json,AdminLogin admin,boolean hasDesc) throws SQLException, MException {
		String defaultMenuOid=null;
		List<ModuleInfo> moduleList=getModuleList4Group(admin);
		JSONMessage modules=new JSONMessage();
		for(ModuleInfo moduleInfo : moduleList){
			JSONMessage module=new JSONMessage();
			module.push("oid", moduleInfo.getOid());
			module.push("name", moduleInfo.getName());
			module.push("icon", moduleInfo.getIcoStyle());
			JSONMessage menus1=new JSONMessage();
			List<MenuInfo> menulist1=getMenuList4Group(moduleInfo.getOid(), admin);
			if(menulist1.size()>0){
				for(MenuInfo menuInfo1 : menulist1){
					JSONMessage menu1=new JSONMessage();
					menu1.push("oid", menuInfo1.getOid());
					menu1.push("name", menuInfo1.getName());
					menu1.push("icon", menuInfo1.getIcoStyle());
					List<MenuInfo> menulist2=getMenuList4Group(moduleInfo.getOid(),menuInfo1.getOid(), admin);
					if(menulist2.size()>0){
						JSONMessage menu2=new JSONMessage();
						for(MenuInfo menuInfo2 : menulist2){
							if(null==defaultMenuOid)defaultMenuOid=menuInfo2.getOid();
							menu2.push(menuInfo2.getOid(), menuInfo2.getName()+(hasDesc?"|"+menuInfo2.getDescription():""));
						}
						menu1.push("menus", menu2);
						menus1.push(menuInfo1.getOid(), menu1);
					}
				}
				module.push("menus", menus1);
				modules.push(moduleInfo.getOid(), module);
			}
		}
		json.push(key, modules);
		return defaultMenuOid;
	}
	
	/**
	 * 获取管理员组模块
	 * @param group
	 * @return
	 * @throws SQLException
	 * @throws MException
	 */
	public List<ModuleInfo> getModuleList4Group(AdminLogin admin) throws SQLException, MException{
		return ModelQueryList.getModelList(ModuleInfo.class, 
				new String[]{"oid","name","urlPath","icoStyle"}, 
				new QueryPage(0,100), 
				QueryCondition.or(new QueryCondition[]{QueryCondition.eq("isPublic", "Y"),
					QueryCondition.in("oid", 
						ModelQueryList.instance(GroupMenuLink.class, 
							new String[]{"module.oid"}, null, 
							QueryCondition.or(new QueryCondition[]{
								QueryCondition.eq("adminGroup.oid",admin.getAdminGroup().getOid()),
								QueryCondition.eq("adminGroup.status","0"),
								QueryCondition.in("adminGroup.oid",
									ModelQueryList.instance(AdminGroupLink.class,
										new String[] {"adminGroup.oid"}, null,
										QueryCondition.and(new QueryCondition[] {
											QueryCondition.eq("admin.oid", admin.getOid()),
											QueryCondition.eq("adminGroup.status","0")
										})
									)
								)
							})
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
	public List<MenuInfo> getMenuList4Group(String module_oid,AdminLogin admin) throws SQLException, MException {
		QueryCondition condition=QueryCondition.and(new QueryCondition[]{
				QueryCondition.eq("moduleInfo.oid", module_oid),
				QueryCondition.isEmpty("parentMenu.oid"),
				QueryCondition.or(new QueryCondition[]{QueryCondition.eq("isPublic", "Y"),
					QueryCondition.in("oid", 
						ModelQueryList.instance(GroupMenuLink.class, 
							new String[]{"menu.oid"}, null, 
							QueryCondition.or(new QueryCondition[]{
								QueryCondition.eq("adminGroup.oid",admin.getAdminGroup().getOid()),
								QueryCondition.eq("adminGroup.status","0"),
								QueryCondition.in("adminGroup.oid",
									ModelQueryList.instance(AdminGroupLink.class,
										new String[] {"adminGroup.oid"}, null,
										QueryCondition.and(new QueryCondition[] {
											QueryCondition.eq("admin.oid", admin.getOid()),
											QueryCondition.eq("adminGroup.status","0")
										})
									)
								)
							})
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
	public List<MenuInfo> getMenuList4Group(String module_oid,String menu_oid,AdminLogin admin) throws SQLException, MException {
		QueryCondition condition=QueryCondition.and(new QueryCondition[]{
				QueryCondition.eq("moduleInfo.oid", module_oid),
				QueryCondition.eq("parentMenu.oid", menu_oid),
				QueryCondition.or(new QueryCondition[]{QueryCondition.eq("isPublic", "Y"),
					QueryCondition.in("oid", 
						ModelQueryList.instance(GroupMenuLink.class, 
							new String[]{"menu.oid"}, null, 
							QueryCondition.or(new QueryCondition[]{
								QueryCondition.eq("adminGroup.oid",admin.getAdminGroup().getOid()),
								QueryCondition.eq("adminGroup.status","0"),
								QueryCondition.in("adminGroup.oid",
									ModelQueryList.instance(AdminGroupLink.class,
										new String[] {"adminGroup.oid"}, null,
										QueryCondition.and(new QueryCondition[] {
											QueryCondition.eq("admin.oid", admin.getOid()),
											QueryCondition.eq("adminGroup.status","0")
										})
									)
								)
							})
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
