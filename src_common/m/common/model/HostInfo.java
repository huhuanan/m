package m.common.model;

import java.util.Date;

public class HostInfo extends Model {
	private String ip;
	private String ipport;
	private Integer total;//

	private Integer main;//0 or 1(主控)
	private Integer self;//0 or 1(当前主机)
	private Date lastDate;

	private Double totalMemory=0.0;//JVM总内存
	private Double freeMemory=0.0;//JVM分配内存
	private Double maxMemory=0.0;//JVM最大内存
	private Integer loginNum=0;//登录数量
	private Integer sessionNum=0;//session数量
	private Integer dbUseLinkNum=0;//数据库当前连接数
	
	public Integer getDbUseLinkNum() {
		return dbUseLinkNum;
	}
	public Integer getLoginNum() {
		return loginNum;
	}
	public void setLoginNum(Integer loginNum) {
		this.loginNum = loginNum;
	}
	public void setDbUseLinkNum(Integer dbUseLinkNum) {
		this.dbUseLinkNum = dbUseLinkNum;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Date getLastDate() {
		return lastDate;
	}
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	public Integer getMain() {
		return main;
	}
	public void setMain(Integer main) {
		this.main = main;
	}
	public Integer getSelf() {
		return self;
	}
	public Double getTotalMemory() {
		return totalMemory;
	}
	public void setTotalMemory(Double totalMemory) {
		this.totalMemory = totalMemory;
	}
	public Double getFreeMemory() {
		return freeMemory;
	}
	public String getIpport() {
		return ipport;
	}
	public void setIpport(String ipport) {
		this.ipport = ipport;
	}
	public void setFreeMemory(Double freeMemory) {
		this.freeMemory = freeMemory;
	}
	public Double getMaxMemory() {
		return maxMemory;
	}
	public void setMaxMemory(Double maxMemory) {
		this.maxMemory = maxMemory;
	}
	public void setSelf(Integer self) {
		this.self = self;
	}
	public Integer getSessionNum() {
		return sessionNum;
	}
	public void setSessionNum(Integer sessionNum) {
		this.sessionNum = sessionNum;
	}
}
