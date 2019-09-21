package m.system.netty;

public class TestClient {
	public static void main(String[] a) throws Exception{
		final NettyClient client=new NettyClient(new NettyEvent(){
			public String readOrReturn(String ipport, String msg) {
				System.out.println(ipport+":"+msg);
				return null;
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
			
		},"192.168.137.1",12345);
		new Thread(){
			public void run(){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				client.send("12345678");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				client.close();
			}
		}.start();
		client.setTimerTask(new NettyTimerEvent<NettyClient>(){
			int i=0;
			@Override
			public void run(NettyClient t) {
				if(i==2){
					stop();
				}
				i++;
				t.send("22222");
			}
		}, 1000);
		client.open();
	}
}
