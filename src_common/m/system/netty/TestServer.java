package m.system.netty;

public class TestServer {

	public static void main(String[] args) throws Exception {
		NettyServer<NettyMessage> server=new NettyServer<NettyMessage>(new NettyEvent<NettyMessage>() {
			public NettyMessage readOrReturn(String ipport, NettyMessage msg) {
				System.out.println(ipport+":"+msg);
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
		},8181);
		server.setTimerTask(new NettyTimerEvent<NettyServer<NettyMessage>>() {
			int i=0;
			@Override
			public void run(NettyServer<NettyMessage> t) {
				if(i==5) {
					t.close();
					return;
				}
				i++;
			}
			
		}, 2000);
		server.open();
	}

}
