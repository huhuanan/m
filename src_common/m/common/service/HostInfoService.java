package m.common.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;

import m.common.model.HostInfo;
import m.system.RuntimeData;
import m.system.db.DBConnection;
import m.system.listener.SessionListener;
import m.system.util.ByteUtil;
import m.system.util.NumberUtil;
import m.system.util.StringUtil;

public class HostInfoService extends Service {
	private int currentTotal=1;
	private int currentOid=0;
	private HostInfo currentHost;
	public HostInfo getCurrentHost(){
		return currentHost;
	}
	
	private static Map<String,HostInfo> hostMap=new LinkedHashMap<String,HostInfo>();
	public void clearList(){
		hostMap=new LinkedHashMap<String,HostInfo>();
	}
	public List<HostInfo> getList(){
		reset();
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
	public void reset(){
		double mb = 1024 * 1024 * 1.0;
		int totalMemory = NumberUtil.toInt(Runtime.getRuntime().totalMemory() / mb *100);
		int freeMemory = NumberUtil.toInt(Runtime.getRuntime().freeMemory() / mb *100);
		int maxMemory = NumberUtil.toInt(Runtime.getRuntime().maxMemory() / mb *100);
		int sessionNum=SessionListener.getSessionNum();
		int dbUseLinkNum = DBConnection.getUseLinkNum();
		setHostOtherInfo(totalMemory, freeMemory, maxMemory, sessionNum,dbUseLinkNum);
	}
	public synchronized void setHostInfo(String ip,Date lastDate,int total,int oid,int main,int self){
		HostInfo hi=hostMap.get(ip);
		if(null==hi){
			hi=new HostInfo();
			hi.setOid(String.valueOf(oid));
			hi.setIp(ip);
			hi.setLastDate(lastDate);
			hi.setTotal(total);
			hi.setMain(main);
			hi.setSelf(self);
			hostMap.put(ip, hi);
		}else{
			hi.setTotal(total);
			hi.setLastDate(lastDate);
		}
		if(null==currentHost&&self==1){
			currentHost=hostMap.get(ip);
			RuntimeData.setHostInfo(currentHost);
			currentOid=Integer.parseInt(currentHost.getOid());
		}
	}
	public void setHostOtherInfo(int totalMemory,int freeMemory,int maxMemory,int sessionNum,int dbUseLinkNum){
		if(null!=currentHost){
			setHostOtherInfo(currentHost.getIp(), totalMemory, freeMemory, maxMemory,sessionNum,dbUseLinkNum);
		}
	}
	/**
	 * 其它信息
	 * @param ip
	 * @return
	 */
	public void setHostOtherInfo(String ip,int totalMemory,int freeMemory,int maxMemory,int sessionNum,int dbUseLinkNum){
		HostInfo hi=hostMap.get(ip);
		if(null!=hi){
			hi.setTotalMemory(totalMemory/100.0);
			hi.setFreeMemory(freeMemory/100.0);
			hi.setMaxMemory(maxMemory/100.0);
			hi.setSessionNum(sessionNum);
			hi.setDbUseLinkNum(dbUseLinkNum);
		}
	}
	/**
	 * 其它信息
	 * @param ip
	 * @return
	 */
	public byte[] getBytesByHostOtherInfo(){
		if(null!=currentHost){
			return getBytesByHostOtherInfo(currentHost.getIp());
		}else{
			return ByteUtil.toBytes(0, 20);
		}
	}
	/**
	 * 其它信息
	 * @param ip
	 * @return
	 */
	public byte[] getBytesByHostOtherInfo(String ip){
		byte[] nb=new byte[0];
		HostInfo hi=hostMap.get(ip);
		if(null!=hi){
			nb=ArrayUtils.addAll(nb,ByteUtil.toBytes(NumberUtil.toInt(hi.getTotalMemory()*100), 4));
			nb=ArrayUtils.addAll(nb,ByteUtil.toBytes(NumberUtil.toInt(hi.getFreeMemory()*100), 4));
			nb=ArrayUtils.addAll(nb,ByteUtil.toBytes(NumberUtil.toInt(hi.getMaxMemory()*100), 4));
			nb=ArrayUtils.addAll(nb,ByteUtil.toBytes(NumberUtil.toInt(hi.getSessionNum()), 4));
			nb=ArrayUtils.addAll(nb,ByteUtil.toBytes(hi.getDbUseLinkNum(), 2));
		}else{
			nb=ArrayUtils.addAll(nb,ByteUtil.toBytes(0, 20));
		}
		return nb;
	}
	/**
	 * 将主机信息转换成byte数组
	 * @return
	 */
	public byte[] toBytesByHosts(String requestIp){
		byte[] nb=new byte[0];
		for(HostInfo host : getList()){
			byte[] hb=new byte[4];
			String[] ss=host.getIp().split("\\.");
			for(int i=0,len=ss.length;i<len;i++){
				hb[i]=ByteUtil.toBytes(Integer.parseInt(ss[i]), 1)[0];
			}
			nb=ArrayUtils.addAll(nb,hb);
			nb=ArrayUtils.addAll(nb,ByteUtil.toBytes(host.getTotal(), 4));
			nb=ArrayUtils.addAll(nb,ByteUtil.toBytes(Integer.parseInt(host.getOid()), 1));
			nb=ArrayUtils.addAll(nb,ByteUtil.toBytes(host.getMain(),1));
			nb=ArrayUtils.addAll(nb,ByteUtil.toBytes(host.getIp().equals(requestIp)?1:0,1));
			nb=ArrayUtils.addAll(nb,getBytesByHostOtherInfo(host.getIp()));
		}
		return nb;
	}
	/**
	 * 将byte数组填充主机信息
	 * @param nb
	 */
	public void setHostByBytes(byte[] nb){
		clearList();
		String ip;
		int totalMemory =0;
		int freeMemory =0;
		int maxMemory =0;
		int sessionNum=0;
		int dbUseLinkNum =0;
		for(int i=0,len=nb.length;i<len;i+=29){
			ip=ByteUtil.toIntJoin(Arrays.copyOfRange(nb, i,i+4), ".");
			setHostInfo(ip,null, 
					ByteUtil.toInt(Arrays.copyOfRange(nb, i+4, i+8)), ByteUtil.toInt(Arrays.copyOfRange(nb, i+8, i+9)), 
					ByteUtil.toInt(Arrays.copyOfRange(nb, i+9, i+10)), ByteUtil.toInt(Arrays.copyOfRange(nb, i+10, i+11)));
			totalMemory = ByteUtil.toInt(Arrays.copyOfRange(nb, i+11, i+15));
			freeMemory = ByteUtil.toInt(Arrays.copyOfRange(nb, i+15, i+19));
			maxMemory = ByteUtil.toInt(Arrays.copyOfRange(nb, i+19, i+23));
			sessionNum=ByteUtil.toInt(Arrays.copyOfRange(nb, i+23, i+27));
			dbUseLinkNum = ByteUtil.toInt(Arrays.copyOfRange(nb, i+27, i+29));
			setHostOtherInfo(ip,totalMemory,freeMemory,maxMemory,sessionNum,dbUseLinkNum);
		}
	}
	public void clearTimeoutHost(){
		HostInfo hi;
		long time=new Date().getTime();
		for(String ip : hostMap.keySet()){
			hi=hostMap.get(ip);
			if(hi.getSelf()==1) continue;
			if(hi.getLastDate().getTime()<time-20*1000){
				hostMap.remove(ip);
			}
		}
	}
	public void clearOtherHost(){
		HostInfo hi;
		for(String ip : hostMap.keySet()){
			hi=hostMap.get(ip);
			if(hi.getSelf()!=1) hostMap.remove(ip);
		}
	}
	public int getCurrentTotal() {
		return currentTotal;
	}
	public void setCurrentTotal(int currentTotal) {
		this.currentTotal = currentTotal;
	}
	public void addCurrentTotal(int currentTotal) {
		this.currentTotal += currentTotal;
	}
	public int getCurrentOid() {
		return currentOid;
	}
}
