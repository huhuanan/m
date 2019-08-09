package manage.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import m.common.action.ActionMeta;
import m.common.action.ActionResult;
import m.common.model.Model;
import m.common.model.util.ModelQueryList;
import m.system.RuntimeData;
import m.system.exception.MException;
import m.system.util.JSONMessage;
import m.system.util.StringUtil;
import manage.dao.GroupMenuLinkDao;
import manage.model.AdminGroup;
import manage.model.AdminLogin;
import manage.model.GroupMenuLink;
import manage.model.MenuInfo;
import manage.model.ModuleInfo;
import manage.service.GroupMenuLinkService;
import manage.service.ModuleService;
import manage.util.page.button.ButtonMeta;
import manage.util.page.button.ButtonMeta.ButtonEvent;
import manage.util.page.button.ButtonMeta.SuccessMethod;
import manage.util.page.button.ParamMeta;
import manage.util.page.query.QueryMeta;
import manage.util.page.query.QueryMeta.QueryType;
import manage.util.page.table.ActionTableColMeta;
import manage.util.page.table.ActionTableMeta;

@ActionMeta(name="manageGroupMenuLink")
public class GroupMenuLinkAction extends ManageAction {
	private GroupMenuLink model;
	private AdminGroup adminGroup;
	private MenuInfo menu;
	private Date path;
	private Integer path1;
	private Double path2;
	public Date getPath() {
		return path;
	}
	public void setPath(Date path) {
		this.path = path;
	}
	public Integer getPath1() {
		return path1;
	}
	public void setPath1(Integer path1) {
		this.path1 = path1;
	}
	public Double getPath2() {
		return path2;
	}
	public void setPath2(Double path2) {
		this.path2 = path2;
	}
	public JSONMessage getModuleList(){
		JSONMessage message=new JSONMessage();
		try {
			String defaultMenuOid=null;
			ModuleService moduleService = getService(ModuleService.class);
			AdminLogin admin=getSessionAdmin();
			List<ModuleInfo> moduleList=moduleService.getModuleList4Group(admin);
			JSONMessage modules=new JSONMessage();
			for(ModuleInfo moduleInfo : moduleList){
				JSONMessage module=new JSONMessage();
				module.push("oid", moduleInfo.getOid());
				module.push("name", moduleInfo.getName());
				module.push("icon", moduleInfo.getIcoStyle());
				JSONMessage menus1=new JSONMessage();
				List<MenuInfo> menulist1=moduleService.getMenuList4Group(moduleInfo.getOid(), admin);
				if(menulist1.size()>0){
					for(MenuInfo menuInfo1 : menulist1){
						JSONMessage menu1=new JSONMessage();
						menu1.push("oid", menuInfo1.getOid());
						menu1.push("name", menuInfo1.getName());
						menu1.push("icon", menuInfo1.getIcoStyle());
						List<MenuInfo> menulist2=moduleService.getMenuList4Group(moduleInfo.getOid(),menuInfo1.getOid(), admin);
						if(menulist2.size()>0){
							JSONMessage menu2=new JSONMessage();
							for(MenuInfo menuInfo2 : menulist2){
								if(null==defaultMenuOid)defaultMenuOid=menuInfo2.getOid();
								menu2.push(menuInfo2.getOid(), menuInfo2.getName());
							}
							menu1.push("menus", menu2);
							menus1.push(menuInfo1.getOid(), menu1);
						}
					}
					module.push("menus", menus1);
					modules.push(moduleInfo.getOid(), module);
				}
			}
			message.push("defaultMenuOid", defaultMenuOid);
			message.push("modules", modules);
			message.push("code", 0);
		} catch (Exception e) {
			message.push("code", 1);
			message.push("msg", e.getMessage());
			e.printStackTrace();
		}
		return message;
	}
	public ActionResult gotoMenuPage() throws Exception{
		ActionResult result=new ActionResult("manage/groupMenuLink/menuPage");
		AdminLogin admin=getSessionAdmin();
		if(null==admin){
			throw new MException(this.getClass(),"未登录!");
		}
		String menu_oid=getDao(GroupMenuLinkDao.class).getMenuOid(admin.getOid(),admin.getAdminGroup().getOid(), menu.getOid());
		if(StringUtil.isSpace(menu_oid)){
			throw new MException(this.getClass(),"权限错误!");
		}
		menu.setOid(menu_oid);
		menu=ModelQueryList.getModel(menu,1);
		StringBuffer content=new StringBuffer("进入菜单(").append(menu.getModuleInfo().getName()).append("/");
		content.append(menu.getParentMenu().getName()).append("/");
		content.append(menu.getName()).append(")");
		setLogContent("菜单", content.toString());
		result.setModel(menu);
		if(menu.getUrlPath().indexOf("?")>=0){
			menu.setUrlPath(new StringBuffer(menu.getUrlPath()).append("&menuId=").append(menu.getOid()).toString());
		}else{
			menu.setUrlPath(new StringBuffer(menu.getUrlPath()).append("?menuId=").append(menu.getOid()).toString());
		}
		return result;
	}
	@ActionTableMeta(dataUrl = "action/manageGroupMenuLink/groupMenuLinkData",tableHeight=300,
		modelClass="manage.model.GroupMenuLink",
		cols = { 
			@ActionTableColMeta(field = "module.name", title = "模块名称", width=120,align="center"),
			@ActionTableColMeta(field = "menu.name", title = "菜单名称", width=160),
			@ActionTableColMeta(field = "menu.description", title = "菜单描述", width=300)
		},
		querys = {
			@QueryMeta(field="adminGroup.oid",type=QueryType.HIDDEN,name = "管理员组oid")
		},
		buttons = {
			@ButtonMeta(title="设置菜单权限", event = ButtonEvent.MODAL, modalWidth=800,
				url = "action/manageGroupMenuLink/setGroupMenuPage", 
				queryParams={@ParamMeta(name = "model.adminGroup.oid",field="adminGroup.oid")},
				success=SuccessMethod.MUST_REFRESH
			)
		}
	)
	public JSONMessage groupMenuLinkData(){
		return getListDataResult(null);
	}
	/**
	 * 设置用户菜单权限
	 * @return
	 * @throws Exception
	 */
	public ActionResult setGroupMenuPage() throws Exception{
		ActionResult result=new ActionResult("manage/groupMenuLink/setGroupMenuPage");
		ModuleService moduleService=getService(ModuleService.class);
		result.setList(moduleService.getModuleList());
		result.setMap(new HashMap<String, Object>());
		for(Model module : result.getList()){
			List<MenuInfo> list=moduleService.getMenuList(module.getOid());
			result.getMap().put(module.getOid(), list);
			for(MenuInfo menu : list){
				result.getMap().put(menu.getOid(), moduleService.getMenuList(module.getOid(),menu.getOid()));
			}
		}
		result.getMap().put("groupMenuLink", getDao(GroupMenuLinkDao.class).getGroupMenuLink(model.getAdminGroup().getOid()));
		result.setModel(model);
		return result;
	}
	public JSONMessage addGroupMenuLink(){
		JSONMessage message=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			getService(GroupMenuLinkService.class).addGroupMenuLink(model);
			message.push("code", 0);
			message.push("msg", "添加权限成功!");
		} catch (Exception e) {
			message.push("code", 1);
			message.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}
	public JSONMessage removeGroupMenuLink(){
		JSONMessage message=new JSONMessage();
		try {
			verifyAdminOperPower("manage_system_power");
			getService(GroupMenuLinkService.class).removeGroupMenuLink(model);
			message.push("code", 0);
			message.push("msg", "移除权限成功!");
		} catch (Exception e) {
			message.push("code", 1);
			message.push("msg", e.getMessage());
			if(RuntimeData.getDebug()) e.printStackTrace();
		}
		return message;
	}
	public AdminGroup getAdminGroup() {
		return adminGroup;
	}
	public void setAdminGroup(AdminGroup adminGroup) {
		this.adminGroup = adminGroup;
	}
	public GroupMenuLink getModel() {
		return model;
	}
	public void setModel(GroupMenuLink model) {
		this.model = model;
	}
	public MenuInfo getMenu() {
		return menu;
	}
	public void setMenu(MenuInfo menu) {
		this.menu = menu;
	}
	@Override
	public Class<? extends ManageAction> getActionClass() {
		return this.getClass();
	}
}
