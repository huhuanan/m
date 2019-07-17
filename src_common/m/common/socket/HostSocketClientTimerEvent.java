package m.common.socket;

import org.apache.commons.lang.ArrayUtils;

import m.common.service.HostInfoService;
import m.system.RuntimeData;
import m.system.SystemInit;
import m.system.exception.MException;
import m.system.socket.SocketClient;
import m.system.socket.SocketClientTimerEvent;
import m.system.util.ByteUtil;

public class HostSocketClientTimerEvent extends SocketClientTimerEvent {
	private boolean isInit=false;
	@Override
	public void run(SocketClient client) {
		//System.out.println("-----client_timer");
		HostInfoService service=null;
		try {
			service=RuntimeData.getService(HostInfoService.class);
			service.reset();
			byte[] data = new byte[]{(byte)0x88};
			data=ArrayUtils.addAll(data, ByteUtil.toBytes(service.getCurrentTotal(), 4));
			data=ArrayUtils.addAll(data, service.getBytesByHostOtherInfo());
			//客户端只发送当前主机的total
			data=ArrayUtils.addAll(data, new byte[]{0x00});
			HostSocketUtil.fillVerifyData(data);
			byte[] result=client.send(data);
			if(result.length==0){//关闭客户端并关闭定时器
				throw new MException(this.getClass(),"错误的主控服务器");
				//this.setStop(true);
			}else if(result.length==1&&result[0]==1){//主控服务器，关闭客户端
				System.out.println("主控服务器客户端关闭");
				HostSocketUtil.closeClient();//客户端关闭
			}else{//和主控服务器正常通信
				//System.out.println(ByteUtil.toHexString(result, " "));
				service.setHostByBytes(result);
				HostSocketUtil.closeServer();//服务器端关闭
			}
			if(!isInit){
				SystemInit.initModelTable();
				SystemInit.initClassRun();
				SystemInit.taskClassRun();
				isInit=true;
			}
		} catch (Exception e) {
			client.close();
			System.out.println(e.getMessage()+"(重新连接...)");
			try {
				RuntimeData.getService(HostInfoService.class).clearOtherHost();
			} catch (Exception e2) {}
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e1) {}
			client.open();
		}
	}

}
