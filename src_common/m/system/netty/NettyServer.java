package m.system.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

public class NettyServer extends NettyObject<NettyServer> {
	private int port;
	private NettyEvent event;
	private ChannelFuture future;
	private Map<String,ChannelHandlerContext> channelMap;
	
	public NettyServer(NettyEvent event,int port){
		this.port=port;
		this.event=event;
		channelMap=new HashMap<String, ChannelHandlerContext>();
	}
	/**
	 * 开启服务 在调用线程开启 
	 * @throws InterruptedException 
	 * @throws Exception
	 */
	public void open() throws InterruptedException{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(group, bossGroup).channel(NioServerSocketChannel.class).localAddress(this.port)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
					pipeline.addLast(new LengthFieldPrepender(4));
					pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
					pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
					pipeline.addLast(new SimpleChannelInboundHandler<String>(){
						//读取
						protected void channelRead0(ChannelHandlerContext ctx,String msg) throws Exception {
						    String ipport=ctx.channel().remoteAddress().toString();
					        String result=event.readOrReturn(ipport, msg);
					        if(null!=result){
					        	ctx.channel().writeAndFlush(result);
					        	event.sendCallback(ipport, result);
					        }
						}
						//通道开启了
					    public void channelActive(ChannelHandlerContext ctx) throws Exception {
						    super.channelActive(ctx);
						    String ipport=ctx.channel().remoteAddress().toString();
						    channelMap.put(ipport, ctx);
						    event.openCallback(ipport);
					    }
					    //通道关闭了
					    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
						    super.channelInactive(ctx);
						    String ipport=ctx.channel().remoteAddress().toString();
						    channelMap.remove(ipport);
						    event.closeCallback(ipport);
					    }
						public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception {
							super.exceptionCaught(ctx, cause);
						}
					});
				}
			});
			future = bootstrap.bind().sync(); // 服务器异步创建绑定
			System.out.println("服务启动:" + future.channel().localAddress());
			future.channel().closeFuture().sync(); // 关闭服务器通道
		}finally{
			group.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	/**
	 * 关闭服务
	 */
	public void close(){
		for(ChannelHandlerContext ctx : channelMap.values()){
			ctx.close();
		}
		if(null!=future){
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
	/**
	 * 发送给指定ipport
	 * @param ipport
	 * @param msg
	 * @return
	 */
	public boolean send(String ipport,String msg){
		boolean b=false;
		ChannelHandlerContext ctx=channelMap.get(ipport);
		if(null!=ctx){
			ctx.channel().writeAndFlush(msg);
			b=true;
		}
		event.sendCallback(ipport, msg);
		return b;
	}
	/**
	 * 发送给所有连接
	 * @param msg
	 */
	public void sendAll(String msg){
		for(String ipport : channelMap.keySet()){
			channelMap.get(ipport).channel().writeAndFlush(msg);
			event.sendCallback(ipport, msg);
		}
	}
}
