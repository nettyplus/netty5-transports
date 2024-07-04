package io.github.nettyplus.netty5.transports;

import io.netty5.channel.IoHandlerFactory;
import io.netty5.channel.epoll.Epoll;
import io.netty5.channel.epoll.EpollDatagramChannel;
import io.netty5.channel.epoll.EpollIoHandler;
import io.netty5.channel.epoll.EpollServerSocketChannel;
import io.netty5.channel.epoll.EpollSocketChannel;
import io.netty5.channel.kqueue.KQueueDatagramChannel;
import io.netty5.channel.kqueue.KQueueIoHandler;
import io.netty5.channel.kqueue.KQueueServerSocketChannel;
import io.netty5.channel.kqueue.KQueueSocketChannel;
import io.netty5.channel.nio.NioIoHandler;
import io.netty5.channel.socket.DatagramChannel;
import io.netty5.channel.socket.ServerSocketChannel;
import io.netty5.channel.socket.SocketChannel;
import io.netty5.channel.socket.nio.NioDatagramChannel;
import io.netty5.channel.socket.nio.NioServerSocketChannel;
import io.netty5.channel.socket.nio.NioSocketChannel;
import io.netty5.channel.kqueue.KQueue;
import io.netty5.channel.uring.IOUring;
import io.netty5.channel.uring.IOUringIoHandler;
import io.netty5.channel.uring.IOUringServerSocketChannel;
import io.netty5.channel.uring.IOUringSocketChannel;
import io.netty5.channel.uring.IOUringDatagramChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum NettyTransport {
    NIO(true, NioIoHandler::newFactory, NioServerSocketChannel.class, NioSocketChannel.class, NioDatagramChannel.class),
    EPOLL(Epoll.isAvailable(), EpollIoHandler::newFactory, EpollServerSocketChannel.class, EpollSocketChannel.class, EpollDatagramChannel.class),
    IO_URING(IOUring.isAvailable(), IOUringIoHandler::newFactory, IOUringServerSocketChannel.class, IOUringSocketChannel.class, IOUringDatagramChannel.class),
    KQUEUE(KQueue.isAvailable(), KQueueIoHandler::newFactory, KQueueServerSocketChannel.class, KQueueSocketChannel.class, KQueueDatagramChannel.class);

    private static final Collection<NettyTransport> AVAILABLE = Arrays.stream(values())
        .filter(NettyTransport::isAvailable)
        .collect(Collectors.toUnmodifiableList());

    private final boolean available;
    private final Supplier<IoHandlerFactory> ioHandlerFactorySupplier;
    private final Class<? extends ServerSocketChannel> serverSocketChannelClass;
    private final Class<? extends SocketChannel> socketChannelClass;
    private final Class<? extends DatagramChannel> datagramChannelClass;

    NettyTransport(boolean available,
        Supplier<IoHandlerFactory> ioHandlerFactory,
        Class<? extends ServerSocketChannel> serverSocketChannelClass,
        Class<? extends SocketChannel> socketChannelClass,
        Class<? extends DatagramChannel> datagramChannelClass) {
        this.available = available;
        this.ioHandlerFactorySupplier= ioHandlerFactory;
        this.serverSocketChannelClass = serverSocketChannelClass;
        this.socketChannelClass = socketChannelClass;
        this.datagramChannelClass = datagramChannelClass;
    }

    public boolean isAvailable() {
        return available;
    }

    public Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        return this.serverSocketChannelClass;
    }

    public Class<? extends SocketChannel> getSocketChannelClass() {
        return this.socketChannelClass;
    }

    public Class<? extends DatagramChannel> getDatagramChannelClass() {
        return this.datagramChannelClass;
    }

    public IoHandlerFactory createIoHandlerFactory() {
        return this.ioHandlerFactorySupplier.get();
    }

    public static Collection<NettyTransport> availableTransports() {
        return AVAILABLE;
    }
}