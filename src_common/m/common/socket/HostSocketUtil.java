package m.common.socket;

import java.util.Arrays;

import m.system.socket.SocketClient;
import m.system.socket.SocketServer;

public class HostSocketUtil {
	private static SocketServer server;
	private static SocketClient client;
	
	public static void openServer(int port){
		SocketServer ss=SocketServer.create(new HostSocketServerEvent(), port);
		ss.setTimerTask(new HostSocketServerTimerEvent(), 10000);
		ss.open();
		server=ss;
	}
	public static void closeServer(){
		if(null!=server){
			server.close();
			server=null;
		}
	}
	public static void openClient(String ip,int port){
		SocketClient cc=SocketClient.create(new HostSocketClientEvent(), ip, port);
		cc.setTimerTask(new HostSocketClientTimerEvent(), 10000);
		cc.open();
		client=cc;
	}
	public static void closeClient(){
		if(null!=client){
			client.close();
			client=null;
		}
	}
	/**
	 * 验证数据包是否正确
	 * @param bs
	 * @return
	 */
	public static boolean verifyData(byte[] bs){
		byte[] data=Arrays.copyOfRange(bs,0,bs.length-1);
		byte x=0x00;
		for(int i=0,len=data.length;i<len;i++){
			x^=data[i];
		}
		if(x==bs[bs.length-1]){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 填充校验位
	 * @param bs
	 * @return
	 */
	public static void fillVerifyData(byte[] bs){
		byte[] data=Arrays.copyOfRange(bs,0,bs.length-1);
		byte x=0x00;
		for(int i=0,len=data.length;i<len;i++){
			x^=data[i];
		}
		bs[bs.length-1]=x;
	}

}
