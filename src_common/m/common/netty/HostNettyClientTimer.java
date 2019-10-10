package m.common.netty;

import m.common.service.HostInfoService;
import m.system.netty.NettyClient;
import m.system.netty.NettyMessage;
import m.system.netty.NettyTimerEvent;

public class HostNettyClientTimer extends NettyTimerEvent<NettyClient<NettyMessage>> {

	public void run(NettyClient<NettyMessage> t) {
		HostInfoService.resetCurrentHostOtherInfo();
		NettyMessage msg=new NettyMessage();
		msg.push("host_host", HostInfoService.getCurrentHost());
		t.send(msg);
	}

}
