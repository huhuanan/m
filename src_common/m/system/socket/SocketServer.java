package m.system.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import m.system.RuntimeData;
import m.system.db.TransactionManager;
import m.system.exception.MException;

public class SocketServer {
	private SocketServerEvent event;
	private int port;
	private ServerSocket serverImpl;
	private Map<String,SocketServerLine> lineMap;
	private SocketServerTimerEvent timeEvent;
	private long timeLong;
	private SocketServerTimerTask task;
	private ServerTask serverTask;
	
	private SocketServer(SocketServerEvent event, int port){
		this.event=event;
		this.port=port;
		lineMap=new HashMap<String,SocketServerLine>();
	}
	public static SocketServer create(SocketServerEvent event,int port){
		return new SocketServer(event,port);
	}
	/**
	 * 停止接入和定时器
	 */
	public void close(){
		serverTask.stop();
	}
	/**
	 * 添加一个定时器方法
	 * @param event
	 * @param time
	 */
	public void setTimerTask(SocketServerTimerEvent event,long time){
		timeEvent=event;
		timeLong=time;
	}
	public void open(){
		if(null!=serverTask){
			serverTask.stop();
		}
		task = new SocketServerTimerTask(this,timeEvent,timeLong);
		serverTask=new ServerTask(this);
		new Thread(serverTask).start();
	}
	public void removeLine(String ipport) {
		lineMap.remove(ipport);	
	}
	/**
	 * 发送指定
	 * @param ipport
	 * @param bytes
	 * @throws Exception
	 */
	public void send(String ipport,byte[] bytes) throws Exception {
		SocketServerLine line=lineMap.get(ipport);
		if(null==line) throw new MException(this.getClass(),"未连接");
		if(line.isStop()) throw new MException(this.getClass(),"已断开 ");
		line.send(bytes);
	}
	/**
	 * 发送全部
	 * @param bytes
	 */
	public void sendAll(byte[] bytes) {
		for(String key : lineMap.keySet()) {
			try {
				send(key,bytes);
			}catch(Exception e) {
				System.out.println(new StringBuffer(key).append(e.getMessage()).toString());
			}
		}
	}
	
	static class ServerTask implements Runnable {
		private SocketServer server;
		private boolean isStop=false;
		public ServerTask(SocketServer server){
			this.server=server;
		}
		public void run() {
			try {
				isStop=false;
				server.serverImpl = new ServerSocket(server.port);
				if(RuntimeData.getDebug()) System.out.println("服务启动!");
				if(null!=server.task){
					new Thread(server.task).start();
				}
				while (!isStop) {
					Socket socket = server.serverImpl.accept();
					String ipport = new StringBuffer(socket.getInetAddress().getHostAddress()).append(":").append(socket.getPort()).toString();
					SocketServerLine t = new SocketServerLine(ipport,socket,server.event,server);
					server.lineMap.put(ipport, t);
					new Thread(t).start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void stop(){
			try {
				server.serverImpl.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(null!=server.task) server.task.stop();
			isStop=true;
		}
	}
	/**
	 * 用来处理Socket请求的
	 */
	static class SocketServerLine implements Runnable {
		private boolean isStop=false;
		private Socket socket;
		private SocketServer server;
		private String ipport;
		private SocketServerEvent event;
		private DataInputStream reader;
		private DataOutputStream writer;
		public SocketServerLine(String ipport,Socket socket,SocketServerEvent event,SocketServer server) {
			this.ipport = ipport;
			this.socket = socket;
			this.event = event;
			this.server = server;
		}
		public void run() {
			try {
				if(RuntimeData.getDebug()) System.out.print(ipport);
				if(RuntimeData.getDebug()) System.out.println("接入");
				TransactionManager.initConnection();
				event.openCallback(ipport);
				TransactionManager.closeConnection();
				handleSocket(ipport,event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public boolean isStop(){
			return isStop;
		}
		public void stop(){
			isStop=true;
		}
		/** 
		 * 跟客户端Socket进行通信 
		 * @throws Exception 
		 */
		private void handleSocket(String ipport, SocketServerEvent event) {
			try {
				reader = new DataInputStream(socket.getInputStream());
				writer = new DataOutputStream(socket.getOutputStream());
				byte[] bytes = new byte[102400];
				int len;
				//writer.write(new byte[]{0x01});
				while ((len = reader.read(bytes))!=-1&&!isStop) {
					byte[] nc = Arrays.copyOf(bytes, len);
					if(null!=event){
						try {
							TransactionManager.initConnection();
							nc=event.readOrReturn(ipport,nc);
							if(null!=nc){
								writer.write(nc);
								writer.flush();
							}
							event.readCallback(ipport);
							TransactionManager.closeConnection();
						} catch (MException e1) {}
					}
				}
				writer.close();
				reader.close();
				socket.close();
			} catch (IOException e) {
				if(RuntimeData.getDebug()) System.out.println("Socket通信异常");
				e.printStackTrace();
			}
			isStop=true;
			if(null!=event){
				event.closeCallback(ipport);
				server.removeLine(ipport);
			}
		}
		/**
		 * 发送
		 * @param ipport
		 * @param bytes
		 * @throws Exception
		 */
		public void send(byte[] bytes) throws Exception {
			writer = new DataOutputStream(socket.getOutputStream());
			writer.write(bytes);
			writer.flush();
			Thread.sleep(300);//睡0.3秒,防止发送过快
		}
	}
	static class SocketServerTimerTask implements Runnable {
		private SocketServer server;
		private SocketServerTimerEvent event;
		private long time;
		private boolean isStop=false;
		public SocketServerTimerTask(SocketServer server,SocketServerTimerEvent event,long time){
			this.server=server;
			this.event=event;
			this.time=time;
		}
		public void run() {
			if(null!=event){
				while(!event.isStop()){
					if(this.isStop) break;
					try {
						TransactionManager.initConnection();
						event.run(server);
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
	public Map<String, SocketServerLine> getLineMap() {
		return lineMap;
	}
}