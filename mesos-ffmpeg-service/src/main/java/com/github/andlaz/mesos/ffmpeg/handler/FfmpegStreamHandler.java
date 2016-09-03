package com.github.andlaz.mesos.ffmpeg.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedStream;

public class FfmpegStreamHandler extends ChannelInboundHandlerAdapter {

	private final Channel serviceChannel;

	public FfmpegStreamHandler(Channel serviceChannel) {
		this.serviceChannel = serviceChannel;
	}


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

  	  // write chunk to service channel
  	  serviceChannel.writeAndFlush(new DefaultHttpContent((ByteBuf)msg));
	
	}
		
//	@Override
//	public void channelReadComplete(ChannelHandlerContext ctx)  {
//		ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
//	}

}
