package m.common.netty;

import java.util.Date;

import m.common.model.HostInfo;
import m.common.service.HostInfoService;
import m.system.RuntimeData;
import m.system.cache.CacheUtil;
import m.system.netty.NettyEvent;
import m.system.netty.NettyMessage;
import m.system.netty.NettyServer;

public class HostNettyServerEvent extends NettyEvent<NettyMessage> {

	@Override
	public NettyMessage readOrReturn(String ipport, NettyMessage msg) {
		//System.out.println("server readOrReturn:"+msg);
		HostInfo host=msg.get(HostInfo.class,"host_host");
		if(null!=host) {
			String ip=HostNettyUtil.getIp(ipport);
			host.setIp(ip);
			host.setIpport(ipport);
			host.setLastDate(new Date());
			HostInfoService.setHostInfo(ip, host);
			if(ip.equals(RuntimeData.getServerIp())) {
				NettyMessage result=new NettyMessage();
				result.push("host_main", true);
				return result;
			}else {
				return hostMapMessage();
			}
		}
		//缓存处理
		CacheUtil.readNettyServerMessage(ipport, msg);
		return null;
	}
	public void closeCallback(String ipport) {
		//掉线主机清除，并通知所有主机
		String ip=HostNettyUtil.getIp(ipport);
		if(!ip.equals(RuntimeData.getServerIp())) {
			HostInfoService.removeHost(ip);
			NettyServer<NettyMessage> server=HostNettyUtil.getServer();
			if(null!=server) {
				server.sendAll(hostMapMessage());
			}
		}
	}
	private NettyMessage hostMapMessage() {
		NettyMessage result=new NettyMessage();
		result.push("host_hostMap", HostInfoService.getHostMap());
		return result;
	}

	public void sendCallback(String ipport, NettyMessage msg) {
		// TODO Auto-generated method stub
		super.sendCallback(ipport, msg);
		//System.out.println(ipport+msg);
	}

//	public void openCallback(String ipport) {
//		// TODO Auto-generated method stub
//		super.openCallback(ipport);
//	}

	
}
