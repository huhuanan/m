package m.common.netty;

import m.system.netty.NettyClient;
import m.system.netty.NettyMessage;
import m.system.netty.NettyServer;

public class HostNettyUtil {
	private static boolean init=false;
	public static void done() {
		if(!init) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			done();
		}
	}
	private static boolean isClient=true;
	private static boolean isServer=true;
	public static void closeAll() {
		closeClient(false);
		closeServer(false);
	}
	
	private static NettyServer<NettyMessage> server;
	private static NettyClient<NettyMessage> client;
	/**
	 * 开启服务端
	 * @param port
	 */
	public static void openServer(int port) {
		server=new NettyServer<NettyMessage>(new HostNettyServerEvent(),port);
		server.setTimerTask(new HostNettyServerTimer(), 10000);
		new Thread() {
			public void run() {
				try {
					server.open();
				} catch (Exception e) {
					System.out.println("主机服务启动失败:"+e.getMessage());
				}
			}
		}.start();
	}
	/**
	 * 关闭服务端
	 */
	public static void closeServer(boolean isServer) {
		if((!isServer)&&null!=server) {
			init=true;
			System.out.println("关闭 服务端");
			HostNettyUtil.isServer=isServer;
		}
		if(null!=server) {
			server.close();
			server=null;
		}
	}
	/**
	 * 获取服务
	 * @return
	 */
	public static NettyServer<NettyMessage> getServer() {
		return server;
	}
	private static String clientIp;
	private static int clientPort;
	/**
	 * 开启客户端
	 * @param ip
	 * @param port
	 */
	public static void openClient(String ip,int port) {
		clientIp=ip;
		clientPort=port;
		client=new NettyClient<NettyMessage>(new HostNettyClientEvent(),ip,port);
		client.setTimerTask(new HostNettyClientTimer(), 10000);
		new Thread() {
			public void run() {
				try {
					client.open();
				} catch (Exception e) {
					System.out.println("主机客户端启动失败:"+e.getMessage());
					HostNettyUtil.reopenClient();
				}
			}
		}.start();
	}
	/**
	 * 关闭客户端
	 */
	public static void closeClient(boolean isClient) {
		if((!isClient)&&null!=client) {
			init=true;
			System.out.println("关闭 客户端");
			HostNettyUtil.isClient=isClient;
		}
		if(null!=client) {
			client.close();
			client=null;
		}
	}
	/**
	 * 重启客户端
	 */
	public static void reopenClient() {
		closeClient(isClient);
		if(isClient) {
			try {
				Thread.sleep(5000); //延迟五秒启动
			} catch (InterruptedException e) { }
			System.out.println("主机客户端重启");
			openClient(clientIp, clientPort);
		}
	}
	/**
	 * 获取客户端
	 * @return
	 */
	public static NettyClient<NettyMessage> getClient() {
		return client;
	}
	/**
	 * 获取ip部分
	 * @param ipport
	 * @return
	 */
	public static String getIp(String ipport) {
		String ip=ipport;
		if(ip.indexOf("/")==0) ip=ip.substring(1);
		return ip.split(":")[0];
	}
}
