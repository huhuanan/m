package m.common.netty;

import m.system.netty.NettyClient;
import m.system.netty.NettyServer;
import m.system.util.StringUtil;

public class HostNettyUtil {
	private static boolean init=false;
	protected static void setDone() {
		init=true;
	}
	protected static boolean isClient=true;
	protected static boolean isServer=true;
	public static void done() {
		if(!init) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			done();
		}
	}
	
	private static NettyServer server;
	private static NettyClient client;
	/**
	 * 开启服务端
	 * @param port
	 */
	public static void openServer(int port) {
		server=new NettyServer(new HostNettyServerEvent(),port);
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
	public static void closeServer() {
		if(null!=server) {
			server.close();
			server=null;
		}
	}
	/**
	 * 获取服务
	 * @return
	 */
	public static NettyServer getServer() {
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
		client=new NettyClient(new HostNettyClientEvent(),ip,port);
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
	public static void closeClient() {
		if(null!=client) {
			client.close();
			client=null;
		}
	}
	/**
	 * 重启客户端
	 */
	public static void reopenClient() {
		closeClient();
		if(!StringUtil.isSpace(clientIp)) {
			try {
				Thread.sleep(5000); //延迟五秒启动
			} catch (InterruptedException e) { }
			System.out.println("主机客户端重启");
			openClient(clientIp, clientPort);
		}
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
