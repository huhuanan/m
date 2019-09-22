package m.system.netty;

public abstract class NettyEvent {

	/**
	 * 读取或者发送
	 * @param ipport
	 * @param result 读取的内容
	 * @return 发送的内容, null代表不发送
	 */
	public abstract NettyMessage readOrReturn(String ipport,NettyMessage msg);
	/**
	 * 发送回调 readOrReturn也会调用
	 * @param ipport
	 * @param result 发送的内容
	 * @return
	 */
	public void sendCallback(String ipport,NettyMessage msg){
		
	};
	/**
	 * 打开回调事件
	 * @param ipport
	 */
	public void openCallback(String ipport){
		
	};
	/**
	 * 关闭回调事件
	 * @param ipport
	 */
	public void closeCallback(String ipport){
		
	}
	/**
	 * 发生异常
	 * @param ipport
	 * @param cause
	 */
	public void exceptionCallback(String ipport,Throwable cause) {
		
	}
}
