package m.common.model;

import java.util.Date;

public interface LogModel {

	public String getRealname();
	public void setRealname(String realname);
	public String getUsername();
	public void setUsername(String username);
	public String getUserType();
	public void setUserType(String userType);
	public String getOperType();
	public void setOperType(String operType);
	public String getOperUrl();
	public void setOperUrl(String operUrl);
	public String getOperData();
	public void setOperData(String operData);
	public String getDescription();
	public void setDescription(String description);
	public Date getCreateDate();
	public void setCreateDate(Date createDate);
	public String getOperResult();
	public void setOperResult(String operResult);
	public String getResultException();
	public void setResultException(String resultException);
	public String getOperIp();
	public void setOperIp(String operIp);
}
