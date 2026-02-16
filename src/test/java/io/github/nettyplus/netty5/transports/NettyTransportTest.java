package io.github.nettyplus.netty5.transports;

import io.netty5.channel.IoHandler;
import io.netty5.channel.epoll.Epoll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class NettyTransportTest {
    @Test
    public void nioIsAvailable() {
        assertTrue(NettyTransport.NIO.isAvailable());
        assertThat(NettyTransport.availableTransports())
            .contains(NettyTransport.NIO);
    }

    @Test
    @EnabledOnOs(value = { OS.LINUX } )
    public void epollIsAvailableOnLinux() {
        assertTrue(Epoll.isAvailable());
        assertTrue(NettyTransport.EPOLL.isAvailable());
    }

    @Test
    @EnabledOnOs(value = { OS.LINUX } )
    public void ioUringIsAvailableOnLinux() {
        /* todo
        assertTrue(IOUring.isAvailable());
        assertTrue(NettyTransport.IO_URING.isAvailable());
         */
    }

    @Test
    @EnabledOnOs(value = { OS.LINUX } )
    public void linuxTransports() {
        assertThat(NettyTransport.availableTransports())
            .containsExactlyInAnyOrder(
                NettyTransport.NIO,
                /* todo NettyTransport.IO_URING, */
                NettyTransport.EPOLL);
    }

    @Test
    @EnabledOnOs(value = { OS.WINDOWS } )
    public void windowsTransports() {
        assertThat(NettyTransport.availableTransports())
            .containsExactlyInAnyOrder(NettyTransport.NIO);
    }

    @Test
    @EnabledOnOs(value = { OS.MAC } )
    public void macTransports() {
        assertThat(NettyTransport.availableTransports())
            .containsExactlyInAnyOrder(
                NettyTransport.NIO,
                NettyTransport.KQUEUE);
    }

    @ParameterizedTest
    @EnumSource(NettyTransport.class)
    public void checkTransport(final NettyTransport transport) {
        assertNotNull(transport.getDatagramChannelClass());
        assertNotNull(transport.getServerSocketChannelClass());
        assertNotNull(transport.getSocketChannelClass());
        if (transport.isAvailable()) {
            IoHandler handler = transport.createIoHandlerFactory().newHandler();
            handler.prepareToDestroy();
            handler.destroy();
        }
    }
}