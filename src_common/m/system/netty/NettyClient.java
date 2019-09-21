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
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

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
	public void open() throws InterruptedException{
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>(){
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast(new LengthFieldPrepender(4));
				pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
				pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
				pipeline.addLast(new SimpleChannelInboundHandler<String>(){
					//读取
					protected void channelRead0(ChannelHandlerContext ctx,String msg) throws Exception {
					    String ipport=ctx.channel().localAddress().toString();
				        String result=event.readOrReturn(ipport, msg);
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
					}
				});
			}
        });
        try {
            ChannelFuture future = bootstrap.connect(ip, port).sync();
			System.out.println("客户端启动:" + future.channel().localAddress());
			future.channel().closeFuture().sync();
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
	}
	public void close(){
		if(null!=channel){
			channel.close();
		}
	}
	/**
	 * 向服务端发送消息
	 * @param msg
	 * @return
	 */
	public boolean send(String msg){
		boolean b=false;
		if(null!=channel){
			channel.channel().writeAndFlush(msg);
			event.sendCallback(channel.channel().localAddress().toString(), msg);
			b=true;
		}
		return b;
	}
}
