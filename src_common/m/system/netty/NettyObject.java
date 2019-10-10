package m.system.netty;

public abstract class NettyObject<T extends NettyObject<?>> {
	private NettyTimerLine<T> timerLine;
	private Thread timerThread;
	public void clearTimerTask(){
		if(null!=timerLine){
			timerLine.stop();
			timerLine=null;
		}
		if(null!=timerThread) {
			timerThread.interrupt();
			timerThread=null;
		}
	}
	@SuppressWarnings("unchecked")
	public void setTimerTask(NettyTimerEvent<T> event,long time){
		clearTimerTask();
		if(null!=event){
			timerLine=new NettyTimerLine<T>((T) this,event,time);
		}
	}
	protected void runTimerTask() {
		if(null!=timerLine){
			timerThread=new Thread(timerLine);
			timerThread.start();
		}
	}
	class NettyTimerLine<E extends NettyObject<?>> implements Runnable {
		private E server;
		private NettyTimerEvent<E> event;
		private long time;
		private boolean isStop=false;
		public NettyTimerLine(E server,NettyTimerEvent<E> event,long time){
			this.server=server;
			this.event=event;
			this.time=time;
		}
		public void run() {
			if(null!=event){
				while(!event.isStop()){
					if(this.isStop) break;
					event.run(server);
					try {
						Thread.sleep(time);
					} catch (InterruptedException e) {} 
				}
			}
		}
		/**
		 * 停止后不可再起
		 */
		public void stop(){
			this.isStop=true;
		}
	}
}
