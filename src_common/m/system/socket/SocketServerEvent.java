package m.system.socket;


public interface SocketServerEvent {
	/**
	 * 读取并返回
	 * @param ipport
	 * @param bytes
	 * @return
	 */
	public byte[] readOrReturn(String ipport,byte[] bytes);
	/**
	 * 读取后的回调事件
	 * @param ipport
	 */
	public void readCallback(String ipport);
	/**
	 * 打开回调事件
	 * @param ipport
	 */
	public void openCallback(String ipport);
	/**
	 * 关闭回调事件
	 * @param ipport
	 */
	public void closeCallback(String ipport);
	
}
