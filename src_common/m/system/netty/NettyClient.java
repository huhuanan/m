package m.system.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient<T extends Object> extends NettyObject<NettyClient<T>> {
	private String ip;
	private int port;
	private NettyEvent<T> event;
	private ChannelHandlerContext channel;
	
	public NettyClient(NettyEvent<T> event,String ip,int port){
		this.ip=ip;
		this.port=port;
		this.event=event;
	}
	/**
	 * 启动客户端 在调用线程启动
	 * @throws InterruptedException
	 */
	public void open() throws Exception{
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>(){
			protected void initChannel(SocketChannel ch) throws Exception {
				event.initClientChannel(ch);
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new SimpleChannelInboundHandler<T>(){
					//读取
					protected void channelRead0(ChannelHandlerContext ctx,T msg) throws Exception {
					    String ipport=ctx.channel().localAddress().toString();
					    T result=event.readOrReturn(ipport, msg);
				        if(null!=result){
				        	ctx.channel().writeAndFlush(result);
				        	event.sendCallback(ipport, result);
				        }
					}
					//通道开启了
				    public void channelActive(ChannelHandlerContext ctx) throws Exception {
					    super.channelActive(ctx);
					    String ipport=ctx.channel().localAddress().toString();
					    channel=ctx;
					    event.openCallback(ipport);
				    }
				    //通道关闭了
				    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
					    super.channelInactive(ctx);
					    String ipport=ctx.channel().localAddress().toString();
					    channel=null;
					    event.closeCallback(ipport);
				    }
					public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception {
						super.exceptionCaught(ctx, cause);
					    String ipport=ctx.channel().localAddress().toString();
						event.exceptionCallback(ipport, cause);
					}
				});
			}
        });
        try {
            ChannelFuture future = bootstrap.connect(ip, port).sync();
			System.out.println("客户端启动:" + future.channel().localAddress());
			runTimerTask();//启动定时任务
			future.channel().closeFuture().sync();
        }catch(Exception e){
        	throw e;
        }finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
	}
	public void close(){
		if(null!=channel){
			try {
				channel.close().sync();
			} catch (InterruptedException e) { }
		}
		this.clearTimerTask();
	}
	/**
	 * 向服务端发送消息
	 * @param msg
	 * @return
	 * @throws InterruptedException 
	 */
	public boolean send(T msg) {
		boolean b=false;
		if(null!=channel){
			try {
				channel.channel().writeAndFlush(msg).sync();
			} catch (InterruptedException e) { }
			event.sendCallback(channel.channel().localAddress().toString(), msg);
			b=true;
		}
		return b;
	}
}
