package m.common.netty;

import java.util.Map;

import m.common.model.HostInfo;
import m.common.service.HostInfoService;
import m.system.cache.CacheUtil;
import m.system.netty.NettyEvent;
import m.system.netty.NettyMessage;

public class HostNettyClientEvent extends NettyEvent {
	public NettyMessage readOrReturn(String ipport, NettyMessage msg) {
		System.out.println("client readOrReturn:"+msg);
		Boolean main=msg.get(Boolean.class,"host_main");
		if(null!=main&&main) {
			System.out.println("主机服务器关闭当前客户端");
			String ip=HostNettyUtil.getIp(ipport);
			HostInfoService.setMainHost(ip);
			HostNettyUtil.setDone();
			HostNettyUtil.isClient=false;
			HostNettyUtil.closeClient();
		}else {
			Map<String, HostInfo> hostMap=msg.get(Map.class, "host_hostMap");
			if(null!=hostMap) {
				System.out.println("主机客户端关闭当前服务端");
				String ip=HostNettyUtil.getIp(ipport);
				HostInfoService.setHostMap(ip,hostMap);
				HostNettyUtil.setDone();
				HostNettyUtil.isServer=false;
				HostNettyUtil.closeServer();
			}
		}
		//缓存处理
		CacheUtil.doNettySynchCache(msg);
		return null;
	}
	public void exceptionCallback(String ipport, Throwable cause) {
		if(HostNettyUtil.isClient) HostNettyUtil.reopenClient();//发生异常重启客户端
	}
	public void closeCallback(String ipport) {
		if(HostNettyUtil.isClient) HostNettyUtil.reopenClient();
	}

//	public void sendCallback(String ipport, NettyMessage msg) {
//		// TODO Auto-generated method stub
//		super.sendCallback(ipport, msg);
//	}
//
//	public void openCallback(String ipport) {
//		// TODO Auto-generated method stub
//		super.openCallback(ipport);
//	}



}
