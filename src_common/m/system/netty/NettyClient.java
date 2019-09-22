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
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyClient extends NettyObject<NettyClient> {
	private String ip;
	private int port;
	private NettyEvent event;
	private ChannelHandlerContext channel;
	
	public NettyClient(NettyEvent event,String ip,int port){
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
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
				pipeline.addLast(new ObjectEncoder());
				pipeline.addLast(new SimpleChannelInboundHandler<NettyMessage>(){
					//读取
					protected void channelRead0(ChannelHandlerContext ctx,NettyMessage msg) throws Exception {
					    String ipport=ctx.channel().localAddress().toString();
					    NettyMessage result=event.readOrReturn(ipport, msg);
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
			channel.close();
		}
		this.clearTimerTask();
	}
	/**
	 * 向服务端发送消息
	 * @param msg
	 * @return
	 */
	public boolean send(NettyMessage msg){
		boolean b=false;
		if(null!=channel){
			channel.channel().writeAndFlush(msg);
			event.sendCallback(channel.channel().localAddress().toString(), msg);
			b=true;
		}
		return b;
	}
}
