package m.common.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import m.common.model.HostInfo;
import m.system.RuntimeData;
import m.system.SystemSessionTask;
import m.system.db.DBConnection;
import m.system.util.NumberUtil;
import m.system.util.StringUtil;

public class HostInfoService extends Service {
	private static int currentTotal=1;
	private static int currentOid=0;
	private static HostInfo currentHost=new HostInfo();
	public static HostInfo getCurrentHost(){
		return currentHost;
	}
	public static void setCurrentHost(HostInfo host) {
		currentHost=host;
		RuntimeData.setHostInfo(currentHost);
		currentOid=Integer.parseInt(host.getOid());
	}
	
	private static Map<String,HostInfo> hostMap=new LinkedHashMap<String,HostInfo>();
	public void clearList(){
		hostMap=new LinkedHashMap<String,HostInfo>();
	}
	public List<HostInfo> getList(){
		resetCurrentHostOtherInfo();
		return new ArrayList<HostInfo>(hostMap.values());
	}
	private static long lastLong=0l;
	private static String[] ips=new String[] {};
	private static Random random=new Random();
	/**
	 * 获取当前主机列表的随机ip
	 * @return
	 */
	public static String getRandomIP(String other) {
		if(System.currentTimeMillis()-lastLong>5000) {
			lastLong=System.currentTimeMillis();
			List<String> ls=new ArrayList<String>();
			for(HostInfo hi : hostMap.values()) {
				if(!hi.getIp().equals(".")) ls.add("http://"+hi.getIp()+"/");
			}
			if(!StringUtil.isSpace(other)) ls.add(other);
			ips=ls.toArray(new String[] {});
		}
		if(ips.length>0) return ips[random.nextInt(ips.length)];
		else return "";
	}
	public static void resetCurrentHostOtherInfo(){
		if(null!=currentHost){
			double mb = 1024 * 1024 * 1.0;
			int totalMemory = NumberUtil.toInt(Runtime.getRuntime().totalMemory() / mb *100);
			int freeMemory = NumberUtil.toInt(Runtime.getRuntime().freeMemory() / mb *100);
			int maxMemory = NumberUtil.toInt(Runtime.getRuntime().maxMemory() / mb *100);
			int sessionNum=SystemSessionTask.getSessionNum();
			int loginNum=SystemSessionTask.getLoginNum();
			int dbUseLinkNum = DBConnection.getUseLinkNum();
			HostInfo hi=hostMap.get(currentHost.getIp());
			if(null!=hi){
				hi.setTotalMemory(totalMemory/100.0);
				hi.setFreeMemory(freeMemory/100.0);
				hi.setMaxMemory(maxMemory/100.0);
				hi.setSessionNum(sessionNum);
				hi.setLoginNum(loginNum);
				hi.setDbUseLinkNum(dbUseLinkNum);
			}
		}
	}
	private static Map<String,Integer> hostOidMap=new HashMap<String, Integer>();
	private static synchronized int getHostOid(String ip) {
		if(null==hostOidMap.get(ip)) {
			int oid=hostOidMap.size();
			hostOidMap.put(ip, oid);
		}
		return hostOidMap.get(ip);
	}
	/**
	 * 主控初始化自己
	 * @param ip
	 */
	public static void setMainHost(String ip) {
		HostInfo host=new HostInfo();
		host.setOid(String.valueOf(currentOid));
		host.setIp(ip);
		host.setTotal(0);
		host.setMain(1);
		host.setSelf(1);
		setHostInfo(ip, host);
		setCurrentHost(host);
	}
	/**
	 * 添加主机信息 服务端调用
	 * @param ip
	 * @param host
	 */
	public static void setHostInfo(String ip,HostInfo host) {
		host.setOid(String.valueOf(getHostOid(ip)));
		if(ip.indexOf(RuntimeData.getServerIp())>=0) {
			host.setMain(1);
			host.setSelf(1);
		}else {
			host.setMain(0);
			host.setSelf(0);
		}
		hostMap.put(ip, host);
	}
	/**
	 * 返回主机map 服务端调用
	 * @return
	 */
	public static Map<String, HostInfo> getHostMap() {
		return hostMap;
	}
	/**
	 * 清除超时主机  服务端调用
	 */
	public static List<HostInfo> getTimeoutHost(){
		List<HostInfo> list=new ArrayList<HostInfo>();
		long time=new Date().getTime();
		for(HostInfo hi : hostMap.values()){
			if(hi.getSelf()==1) continue;
			if(hi.getLastDate().getTime()<time-20*1000){
				list.add(hi);
			}
		}
		return list;
	}
	/**
	 * 移除指定ip主机信息 服务器调用
	 * @param ip
	 */
	public static void removeHost(String ip) {
		hostMap.remove(ip);
	}
	/**
	 * 设置主机map 客户端调用
	 * @param ipport
	 * @param hostMap
	 */
	public static void setHostMap(String ip,Map<String, HostInfo> hostMap) {
		for(HostInfo host : hostMap.values()) {
			if(host.getIp().equals(ip)) {
				host.setSelf(1);
				setCurrentHost(host);
			}
			if(host.getMain()==1) host.setSelf(0);
		}
		HostInfoService.hostMap = hostMap;
		resetCurrentHostOtherInfo();
	}
	
	
	public static int getCurrentTotal() {
		return currentTotal;
	}
	public static void setCurrentTotal(int currentTotal) {
		HostInfoService.currentTotal = currentTotal;
	}
	public static void addCurrentTotal(int currentTotal) {
		HostInfoService.currentTotal += currentTotal;
	}
	public static int getCurrentOid() {
		return currentOid;
	}
}
