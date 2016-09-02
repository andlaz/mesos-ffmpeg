package com.github.andlaz.mesos.ffmpeg;

import com.github.andlaz.mesos.ffmpeg.handler.MesosFfmpegServiceHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class MesosFfmpegService {

	public static class Builder {

		MesosFfmpegService service = new MesosFfmpegService();

		public Builder withServicePort(int port) {
			service.port = port;
			return this;
		}

		public MesosFfmpegService build() {
			return service;
		}

	}

	public static void main(String[] args) throws InterruptedException {

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		// TODO configuration
		MesosFfmpegService service = new MesosFfmpegService.Builder().build();

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						service.pipelineForChannel(ch);
					}
				}).childOption(ChannelOption.SO_KEEPALIVE, true);

		bootstrap.bind(service.getPort()).sync().channel().closeFuture().sync();

	}

	private int port = 8181;

	private MesosFfmpegService() {

	}
	
	public int getPort() {
		return port;
	}

	public ChannelPipeline pipelineForChannel(SocketChannel channel) {

		return channel.pipeline().addLast(new HttpServerCodec()).addLast(new HttpObjectAggregator(65536))
				.addLast(new ChunkedWriteHandler()).addLast(new MesosFfmpegServiceHandler());

	}

}
