package com.github.andlaz.mesos.ffmpeg;

import com.github.andlaz.mesos.ffmpeg.handler.MesosFfmpegServiceHandler;
import com.github.andlaz.mesos.ffmpeg.scheduler.FFMPEGScheduler;
import com.github.andlaz.mesos.ffmpeg.service.ConfigService;
import io.netty.bootstrap.ServerBootstrap;
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
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos.FrameworkInfo;
import org.apache.mesos.Protos.Status;
import org.apache.mesos.Scheduler;

/**
 * Bootstraps the Netty http pipeline
 * @author andras
 *
 */
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
		bootstrap
			.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					service.pipelineForChannel(ch);
				}
			}).childOption(ChannelOption.SO_KEEPALIVE, true);

		bootstrap.bind(service.getPort()).sync().channel().closeFuture().sync();

	}

	private int port = 8181;


	private void mesosFrameworkInitialization() {

		// initializate framework

		FrameworkInfo.Builder frameworkBuilder = FrameworkInfo.newBuilder()
				.setUser("") // Have Mesos fill in the current user.
				.setName("ffmpeg");

		frameworkBuilder.setCheckpoint(true);

		frameworkBuilder.setPrincipal("ffmpeg-ressource-framework");
		Scheduler scheduler =  new FFMPEGScheduler();

		MesosSchedulerDriver driver = new MesosSchedulerDriver(
				scheduler, frameworkBuilder.build(), ConfigService.getMesosMaster());

		int status = driver.run() == Status.DRIVER_STOPPED ? 0 : 1;

		// Ensure that the driver process terminates.
		driver.stop();

		System.exit(status);
	}

	private MesosFfmpegService() {

	}
	
	public int getPort() {
		return port;
	}

	public ChannelPipeline pipelineForChannel(SocketChannel channel) {

		return channel.pipeline()
				.addLast(new HttpServerCodec())
				.addLast(new HttpObjectAggregator(65536))
				.addLast(new ChunkedWriteHandler())
				.addLast(new MesosFfmpegServiceHandler());

	}

}
