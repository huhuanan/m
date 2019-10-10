package m.common.model;

import java.util.Date;

public class HostInfo extends Model {
	private String ip;
	private String ipport;
	private Integer total;//

	private Date createDate=new Date();
	private Date lastDate;

	private Double totalMemory=0.0;//JVM总内存
	private Double freeMemory=0.0;//JVM分配内存
	private Double maxMemory=0.0;//JVM最大内存
	private Integer dbUseLinkNum=0;//数据库当前连接数
	private Integer dbMaxLinkNum=0;//数据库连接数峰值
	
	public Integer getDbMaxLinkNum() {
		return dbMaxLinkNum;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public void setDbMaxLinkNum(Integer dbMaxLinkNum) {
		this.dbMaxLinkNum = dbMaxLinkNum;
	}
	public Integer getDbUseLinkNum() {
		return dbUseLinkNum;
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
}
