package manage.model;


import java.util.Date;

import m.common.model.FieldMeta;
import m.common.model.LinkTableMeta;
import m.common.model.TableMeta;
import m.common.model.UserModel;
import m.common.model.type.FieldType;
@TableMeta(name="os_admin_login",description="管理员登录表")
public class AdminLogin extends StatusModel implements UserModel {

	@LinkTableMeta(name="admin_group_oid",table=AdminGroup.class,notnull=true,description="用户组")
	private AdminGroup adminGroup;
	@LinkTableMeta(name="head_image_oid",table=ImageInfo.class,description="头像")
	private ImageInfo headImage;

	@FieldMeta(name="token",type=FieldType.STRING,length=100,description="token")
	private String token;
	@FieldMeta(name="realname",type=FieldType.STRING,length=20,notnull=true,description="真实姓名")
	private String realname;
	@FieldMeta(name="username",type=FieldType.STRING,length=20,notnull=true,description="登录帐号")
	private String username;
	@FieldMeta(name="password",type=FieldType.STRING,length=50,notnull=true,description="登录密码")
	private String password;
	@FieldMeta(name="create_date",type=FieldType.DATE,description="创建时间")
	private Date createDate;
	@FieldMeta(name="last_login_time",type=FieldType.DATE,description="最后登录时间")
	private Date lastLoginTime;
	@FieldMeta(name="last_login_ip",type=FieldType.STRING,length=20,description="最后登录IP")
	private String lastLoginIp;
	@FieldMeta(name="login_count",type=FieldType.INT,description="登陆次数")
	private Integer loginCount;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public AdminGroup getAdminGroup() {
		return adminGroup;
	}
	public void setAdminGroup(AdminGroup adminGroup) {
		this.adminGroup = adminGroup;
	}
	public Integer getLoginCount() {
		return loginCount;
	}
	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}
	public ImageInfo getHeadImage() {
		return headImage;
	}
	public void setHeadImage(ImageInfo headImage) {
		this.headImage = headImage;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getUserType() {
		return "管理员";
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
