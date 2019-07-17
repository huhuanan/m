package m.system.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import m.common.socket.HostSocketUtil;
import m.system.RuntimeData;
import m.system.db.TransactionManager;
import m.system.exception.MException;

public class SocketClient {
	private SocketClientEvent event;
	private String ip;
	private int port;
	private Socket socket;
	private DataInputStream reader;
	private DataOutputStream writer;
	private SocketClientTimerTask task;
	private SocketClientTimerEvent timeEvent;
	private long timeLong;
	private SocketClient(SocketClientEvent event,String ip,int port){
		this.event=event;
		this.ip=ip;
		this.port=port;
	}
	public static SocketClient create(SocketClientEvent event,String ip,int port){
		return new SocketClient(event,ip,port);
	}
	public void close(){
		if(null!=task) task.stop();
		try {
			if(null!=reader){
				reader.close();
				reader=null;
			}
		} catch (Exception e) {}
		try {
			if(null!=writer){
				writer.close();
				writer=null;
			}
		} catch (Exception e) {}
		try {
			if(null!=socket){
				socket.close();
				socket=null;
			}
		} catch (Exception e) {}
		event.closeCallback(ip, port);
	}
	private int sendNextTime=100;
	public byte[] send(byte[] bs) throws IOException, MException{
		if(null!=socket){
			writer.write(bs);
			writer.flush();
			
			byte[] bytes = new byte[1024];
			byte[] result=new byte[0];
			int len;
			while((len=reader.read(bytes,0,bytes.length))>=0){
				if(len==bytes.length){
					result=ArrayUtils.addAll(result, bytes);
				}else{
					result=ArrayUtils.addAll(result, Arrays.copyOfRange(bytes,0,len));
					break;
				}
				if(reader.available()<=0) break;
			}
			event.sendCallback(ip, port, result);
			return result;
		}else{
			sendNextTime+=50;
			if(sendNextTime>1000) throw new MException(this.getClass(),"未成功链接的发送超时");
			try {
				Thread.sleep(sendNextTime);
			} catch (InterruptedException e) { }
			return send(bs);
		}
	}
	/**
	 * 添加一个定时器方法
	 * @param event
	 * @param time
	 */
	public void setTimerTask(SocketClientTimerEvent event,long time){
		timeEvent=event;
		timeLong=time;
	}
	public void open() {
		if(null==this.socket){
			this.task = new SocketClientTimerTask(this,timeEvent,timeLong);
			new Thread(new ClientTask(this)).start();
		}
	}
	
	/**
	 * 用来启动客户端
	 */
	static class ClientTask implements Runnable {
		private SocketClient client;
		public ClientTask(SocketClient client) {
			this.client = client;
		}
		public void run() {
			try {
				if(RuntimeData.getDebug()) System.out.println("客户端启动!");
				client.socket=new Socket(client.ip,client.port);
				client.reader = new DataInputStream(client.socket.getInputStream());
				client.writer = new DataOutputStream(client.socket.getOutputStream());
				client.event.openCallback(client.ip, client.port);
				if(null!=client.task){
					new Thread(client.task).start();
				}
			} catch (IOException e) {
				HostSocketUtil.closeServer();//服务器端关闭
				System.out.println(new StringBuffer(client.ip).append(":").append(client.port).append("连接失败,稍后重试...").toString());
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {}
				run();
			}
		}
	}
	class SocketClientTimerTask implements Runnable {
		private SocketClient client;
		private SocketClientTimerEvent event;
		private long time;
		private boolean isStop=false;
		public SocketClientTimerTask(SocketClient client,SocketClientTimerEvent event,long time){
			this.client=client;
			this.event=event;
			this.time=time;
		}
		public void run() {
			if(null!=event){
				while(!event.isStop()){
					if(this.isStop) break;
					try {
						TransactionManager.initConnection();
						event.run(client);
						TransactionManager.closeConnection();
					} catch (MException e1) {}
					try { Thread.sleep(time); } catch (InterruptedException e) {}
				}
			}
		}
		public void stop(){
			this.isStop=true;
		}
	}
}
