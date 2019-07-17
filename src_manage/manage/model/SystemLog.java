package manage.model;

import java.util.Date;

import m.common.model.FieldMeta;
import m.common.model.LogModel;
import m.common.model.Model;
import m.common.model.TableMeta;
import m.common.model.type.FieldType;
@TableMeta(name="os_system_log",description="系统日志表")
public class SystemLog extends Model implements LogModel{

	@FieldMeta(name="realname",type=FieldType.STRING,length=20,description="真实姓名")
	private String realname;
	@FieldMeta(name="username",type=FieldType.STRING,length=20,description="登录帐号")
	private String username;
	@FieldMeta(name="user_type",type=FieldType.STRING,length=20,description="用户类型")
	private String userType;
	@FieldMeta(name="oper_type",type=FieldType.STRING,length=20,description="操作类型")
	private String operType;
	@FieldMeta(name="oper_url",type=FieldType.STRING,length=200,description="操作链接")
	private String operUrl;
	@FieldMeta(name="oper_data",type=FieldType.STRING,length=4000,description="操作链接")
	private String operData;
	@FieldMeta(name="description",type=FieldType.STRING,length=500,description="描述")
	private String description;
	@FieldMeta(name="create_date",type=FieldType.DATE,description="创建时间")
	private Date createDate;
	@FieldMeta(name="oper_result",type=FieldType.STRING,length=20,description="操作结果")
	private String operResult;
	@FieldMeta(name="result_exception",type=FieldType.STRING,length=1000,description="结果异常")
	private String resultException;
	@FieldMeta(name="oper_ip",type=FieldType.STRING,length=20,description="操作ip")
	private String operIp;
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getOperType() {
		return operType;
	}
	public void setOperType(String operType) {
		this.operType = operType;
	}
	public String getOperUrl() {
		return operUrl;
	}
	public void setOperUrl(String operUrl) {
		this.operUrl = operUrl;
	}
	public String getOperData() {
		return operData;
	}
	public void setOperData(String operData) {
		this.operData = operData;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getOperResult() {
		return operResult;
	}
	public void setOperResult(String operResult) {
		this.operResult = operResult;
	}
	public String getResultException() {
		return resultException;
	}
	public void setResultException(String resultException) {
		this.resultException = resultException;
	}
	public String getOperIp() {
		return operIp;
	}
	public void setOperIp(String operIp) {
		this.operIp = operIp;
	}
}
