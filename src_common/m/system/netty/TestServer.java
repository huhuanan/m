package m.system.netty;

public class TestServer {
	public static void main(String[] a) throws Exception{
		final NettyServer server=new NettyServer(new NettyEvent(){
			public String readOrReturn(String ipport, String msg) {
				System.out.println(ipport+":"+msg);
				return "ok";
			}

			@Override
			public void closeCallback(String ipport) {
				System.out.println(ipport+"关闭");
			}

			@Override
			public void openCallback(String ipport) {
				System.out.println(ipport+"打开");
			}

			@Override
			public void sendCallback(String ipport, String msg) {
				System.out.println(ipport+":"+msg);
			}
		},12345);
		NettyTimerEvent<NettyServer> task=new NettyTimerEvent<NettyServer>(){

			int i=0;
			@Override
			public void run(NettyServer t) {
				if(i==2){
					stop();
				}
				i++;
				t.sendAll("11111111");
			}
			
		};
		server.setTimerTask(task, 2000);
		server.open();
	}

}
