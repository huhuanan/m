package manage.model;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;

@TableMeta(name="os_admin_group_power",description="用户组权限表")
public class AdminGroupPower extends Model {

	@FieldMeta(name="name",type=FieldType.STRING,length=50,description="名称")
	private String name;
	@LinkTableMeta(name="admin_group_oid",table=AdminGroup.class,description="管理员组")
	private AdminGroup adminGroup;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public AdminGroup getAdminGroup() {
		return adminGroup;
	}
	public void setAdminGroup(AdminGroup adminGroup) {
		this.adminGroup = adminGroup;
	}
}
