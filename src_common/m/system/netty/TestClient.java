package m.system.netty;

public class TestClient {

	public static void main(String[] args) throws Exception {
		NettyClient<NettyMessage> client=new NettyClient<NettyMessage>(new NettyEvent<NettyMessage>() {
			public NettyMessage readOrReturn(String ipport, NettyMessage msg) {
				return null;
			}
			public void sendCallback(String ipport, NettyMessage msg) {
				System.out.println(ipport+":"+msg);
			}
			public void openCallback(String ipport) {
				System.out.println(ipport+"链接");
			}
			public void closeCallback(String ipport) {
				System.out.println(ipport+"关闭");
			}
			public void exceptionCallback(String ipport, Throwable cause) {
				System.out.println(ipport+"异常");
			}
		},"192.168.85.1",8181);
		client.setTimerTask(new NettyTimerEvent<NettyClient<NettyMessage>>() {
			int i=0;
			@Override
			public void run(NettyClient<NettyMessage> t) {
				if(i==10) {
					t.close();
					return;
				}
				i++;
				NettyMessage msg=new NettyMessage();
				msg.push("msg", "123");
				t.send(msg);
			}
			
		}, 2000);
		client.open();
	}

}
