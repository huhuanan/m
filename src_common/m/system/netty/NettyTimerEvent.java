package m.system.netty;


public abstract class NettyTimerEvent<T extends NettyObject<?>> {

	/**
	 * 定时运行
	 */
	public abstract void run(T t);

	private boolean isStop=false;
	/**
	 * 是否停止  返回true则停止
	 * @return
	 */
	public boolean isStop(){
		return isStop;
	}
	/**
	 * 停止后不可再起
	 */
	public void stop(){
		this.isStop=true;
	}
}
