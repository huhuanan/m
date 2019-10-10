package m.common.netty;

import java.util.Map;

import m.common.model.HostInfo;
import m.common.service.HostInfoService;
import m.system.cache.CacheUtil;
import m.system.netty.NettyEvent;
import m.system.netty.NettyMessage;

public class HostNettyClientEvent extends NettyEvent<NettyMessage> {
	public NettyMessage readOrReturn(String ipport, NettyMessage msg) {
		//System.out.println("client readOrReturn:"+msg);
		Boolean main=msg.get(Boolean.class,"host_main");
		if(null!=main&&main) {
			String ip=HostNettyUtil.getIp(ipport);
			HostInfoService.setMainHost(ip);
			HostNettyUtil.closeClient(false);
		}else {
			Map<String, HostInfo> hostMap=msg.get(Map.class, "host_hostMap");
			if(null!=hostMap) {
				String ip=HostNettyUtil.getIp(ipport);
				HostInfoService.setHostMap(ip,hostMap);
				HostNettyUtil.closeServer(false);
			}
		}
		//缓存处理
		CacheUtil.readNettyClientMessage(ipport, msg);
		return null;
	}
	public void exceptionCallback(String ipport, Throwable cause) {
		HostNettyUtil.reopenClient();//发生异常重启客户端
	}
	public void closeCallback(String ipport) {
		HostNettyUtil.reopenClient();
	}

	public void sendCallback(String ipport, NettyMessage msg) {
		// TODO Auto-generated method stub
		super.sendCallback(ipport, msg);
		//System.out.println(ipport+msg);
	}
//
//	public void openCallback(String ipport) {
//		// TODO Auto-generated method stub
//		super.openCallback(ipport);
//	}



}
