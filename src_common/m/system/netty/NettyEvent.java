package m.system.netty;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public abstract class NettyEvent<T extends Object> {

	/**
	 * 读取或者发送
	 * @param ipport
	 * @param result 读取的内容
	 * @return 发送的内容, null代表不发送
	 */
	public abstract T readOrReturn(String ipport,T msg);
	/**
	 * 发送回调 readOrReturn也会调用
	 * @param ipport
	 * @param result 发送的内容
	 * @return
	 */
	public void sendCallback(String ipport,T msg){
		
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
	
	public void initClientChannel(SocketChannel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
		pipeline.addLast(new ObjectEncoder());
	}
	public void initServerChannel(SocketChannel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
		pipeline.addLast(new ObjectEncoder());
	}
}
