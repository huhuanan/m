package manage.model;

import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
@TableMeta(name="os_admin_group_link",description="管理员组关系表")
public class AdminGroupLink extends Model {

	@LinkTableMeta(name="admin_group_oid",table=AdminGroup.class,description="管理员组")
	private AdminGroup adminGroup;
	@LinkTableMeta(name="admin_oid",table=AdminLogin.class,description="管理员")
	private AdminLogin admin;
	public AdminGroup getAdminGroup() {
		return adminGroup;
	}
	public void setAdminGroup(AdminGroup adminGroup) {
		this.adminGroup = adminGroup;
	}
	public AdminLogin getAdmin() {
		return admin;
	}
	public void setAdmin(AdminLogin admin) {
		this.admin = admin;
	}
	
}
