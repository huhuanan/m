package manage.model;

import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
@TableMeta(name="os_group_menu_link",description="管理员组菜单关系表")
public class GroupMenuLink extends Model {

	@LinkTableMeta(name="admin_group_oid",table=AdminGroup.class,description="管理员组")
	private AdminGroup adminGroup;
	@LinkTableMeta(name="module_oid",table=ModuleInfo.class,description="栏目")
	private ModuleInfo module;
	@LinkTableMeta(name="menu_oid",table=MenuInfo.class,description="菜单")
	private MenuInfo menu;
	public ModuleInfo getModule() {
		return module;
	}
	public void setModule(ModuleInfo module) {
		this.module = module;
	}
	public MenuInfo getMenu() {
		return menu;
	}
	public void setMenu(MenuInfo menu) {
		this.menu = menu;
	}
	public AdminGroup getAdminGroup() {
		return adminGroup;
	}
	public void setAdminGroup(AdminGroup adminGroup) {
		this.adminGroup = adminGroup;
	}
}
