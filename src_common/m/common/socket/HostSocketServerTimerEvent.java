package m.common.socket;

import m.common.service.HostInfoService;
import m.system.RuntimeData;
import m.system.exception.MException;
import m.system.socket.SocketServer;
import m.system.socket.SocketServerTimerEvent;

public class HostSocketServerTimerEvent extends SocketServerTimerEvent {

	@Override
	public void run(SocketServer server) {
		//System.out.println("===server_run");
		try {
			HostInfoService service=RuntimeData.getService(HostInfoService.class);
			service.reset();
			service.clearTimeoutHost();
		} catch (MException e) {
			e.printStackTrace();
		}
	}

}
