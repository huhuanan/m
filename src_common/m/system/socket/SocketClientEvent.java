package m.system.socket;


public interface SocketClientEvent {
	/**
	 * 向服务器发送
	 * @param ipport
	 * @param bytes
	 * @return
	 */
	public void sendCallback(String ip,int port,byte[] result);
	/**
	 * 打开回调事件
	 * @param ipport
	 */
	public void openCallback(String ip,int port);
	/**
	 * 关闭回调事件
	 * @param ipport
	 */
	public void closeCallback(String ip,int port);
	
}
