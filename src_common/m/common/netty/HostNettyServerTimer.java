package m.common.netty;

import java.util.List;

import m.common.model.HostInfo;
import m.common.service.HostInfoService;
import m.system.netty.NettyServer;
import m.system.netty.NettyTimerEvent;

public class HostNettyServerTimer extends NettyTimerEvent<NettyServer> {

	@Override
	public void run(NettyServer t) {
		HostInfoService.resetCurrentHostOtherInfo();
		NettyServer server=HostNettyUtil.getServer();
		if(null!=server) {
			List<HostInfo> ls=HostInfoService.getTimeoutHost();
			for(HostInfo hi : ls) {
				server.closeClient(hi.getIpport());
			}
		}
	}

}
