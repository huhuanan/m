package m.system.socket;

public abstract class SocketClientTimerEvent {

	/**
	 * 定时运行
	 */
	public abstract void run(SocketClient client);

	private boolean isStop=false;
	/**
	 * 是否停止  返回true则停止
	 * @return
	 */
	public boolean isStop(){
		return isStop;
	}
	public void setStop(boolean flag){
		this.isStop=flag;
	}
}
