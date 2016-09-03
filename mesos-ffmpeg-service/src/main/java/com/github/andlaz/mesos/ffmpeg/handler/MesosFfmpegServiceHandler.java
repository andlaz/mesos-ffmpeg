package com.github.andlaz.mesos.ffmpeg.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

import java.net.InetSocketAddress;

public class MesosFfmpegServiceHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {

//		sendError(ctx, NOT_IMPLEMENTED);

		// put transcoding request in a queue for the scheduler

		// wait on transcoding task to launch
		// get slave address and port from scheduler
		InetSocketAddress transcoder = InetSocketAddress.createUnresolved("localhost", 8999);

		// send chunked-encoding response
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
		HttpHeaders.setTransferEncodingChunked(response);
//		response.headers().set(CONTENT_TYPE, "application/octet-stream");
		ctx.write(response);

		final Channel serviceChannel = ctx.channel();

		// establish connection to slave
		Bootstrap b = new Bootstrap();
		b.group(serviceChannel.eventLoop())
			.channel(ctx.channel().getClass())
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new FfmpegStreamHandler(serviceChannel));
				}
			});		
		
		// TODO maybe worry about the outcome of this attempt?
		b.connect(transcoder.getHostName(), transcoder.getPort()).channel().closeFuture().addListener(new ChannelFutureListener() {
	        
			@Override
	        public void operationComplete(ChannelFuture future) {
				serviceChannel.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
	        }
	    });
			

	}

	private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status,
				Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

		// Close the connection as soon as the error message is sent.
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	static void closeOnFlush(Channel ch) {
		if (ch.isActive()) {
			ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}

}
