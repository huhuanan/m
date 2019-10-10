package m.common.netty;

import java.util.List;

import m.common.model.HostInfo;
import m.common.service.HostInfoService;
import m.system.netty.NettyMessage;
import m.system.netty.NettyServer;
import m.system.netty.NettyTimerEvent;

public class HostNettyServerTimer extends NettyTimerEvent<NettyServer<NettyMessage>> {

	@Override
	public void run(NettyServer<NettyMessage> t) {
		HostInfoService.resetCurrentHostOtherInfo();
		NettyServer<NettyMessage> server=HostNettyUtil.getServer();
		if(null!=server) {
			List<HostInfo> ls=HostInfoService.getTimeoutHost();
			for(HostInfo hi : ls) {
				server.closeClient(hi.getIpport());
			}
		}
	}

}
