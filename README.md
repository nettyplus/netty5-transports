# netty5-transports

A unified abstraction layer for [Netty 5](https://netty.io/) network transports. This library simplifies the selection and usage of different I/O transport implementations by providing a simple API to detect, configure, and use the optimal transport for your platform.

## Features

- **Automatic transport detection** - Checks platform availability at runtime
- **Unified API** - Single interface for all transport types
- **Multiple transport support** - NIO, epoll, io_uring, and kqueue
- **Easy switching** - Change transports without code modifications
- **Channel class access** - Get appropriate channel classes for each transport

## Supported Transports

| Transport | Platform | Description |
|-----------|----------|-------------|
| **NIO** | All | Standard Java NIO transport (cross-platform, always available) |
| **epoll** | Linux | High-performance Linux-specific transport using epoll |
| **kqueue** | macOS/BSD | High-performance BSD-specific transport using kqueue |

**Note:** Native transports (epoll, kqueue) provide better performance than NIO on their respective platforms.

## Requirements

- Java 11 or higher
- Netty 5.0.0.Alpha5 or compatible version

## Installation

Add the following dependency to your Maven `pom.xml`:

```xml
<dependency>
  <groupId>io.github.nettyplus</groupId>
  <artifactId>netty5-transports</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

You may also need to add the Netty snapshots repository:

```xml
<repositories>
  <repository>
    <id>netty-snapshots</id>
    <name>netty-snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  </repository>
</repositories>
```

## Usage

### Basic Example - Detecting Available Transports

```java
import io.github.nettyplus.netty5.transports.NettyTransport;

public class Example {
  public static void main(String[] args) {
    // Check all transports
    for (NettyTransport transport : NettyTransport.values()) {
      System.out.println(transport.name() + " isAvailable=" + transport.isAvailable());
    }
    
    // Get only available transports
    System.out.println("Available transports: " + NettyTransport.availableTransports());
  }
}
```

### Advanced Example - Using Transports

```java
import io.github.nettyplus.netty5.transports.NettyTransport;
import io.netty5.bootstrap.ServerBootstrap;
import io.netty5.channel.IoHandlerFactory;
import io.netty5.channel.socket.ServerSocketChannel;

public class ServerExample {
  public static void main(String[] args) {
    // Automatically select the best available transport
    NettyTransport transport = NettyTransport.availableTransports()
        .stream()
        .findFirst()
        .orElse(NettyTransport.NIO);
    
    System.out.println("Using transport: " + transport.name());
    
    // Get transport-specific classes
    Class<? extends ServerSocketChannel> channelClass = 
        transport.getServerSocketChannelClass();
    IoHandlerFactory ioHandlerFactory = transport.createIoHandlerFactory();
    
    // Use with Netty ServerBootstrap
    ServerBootstrap bootstrap = new ServerBootstrap()
        .ioHandlerFactory(ioHandlerFactory)
        .channel(channelClass);
    
    // ... configure and start server
  }
}
```

### Accessing Channel Classes

The library provides access to the appropriate channel classes for each transport:

```java
NettyTransport transport = NettyTransport.EPOLL; // or any other transport

// Get channel classes
Class<? extends ServerSocketChannel> serverSocketChannel = 
    transport.getServerSocketChannelClass();
Class<? extends SocketChannel> socketChannel = 
    transport.getSocketChannelClass();
Class<? extends DatagramChannel> datagramChannel = 
    transport.getDatagramChannelClass();
```

## Building

To build the project:

```bash
mvn clean install
```

To skip tests:

```bash
mvn clean install -DskipTests
```

## Testing

Run tests with:

```bash
mvn test
```

**Note:** Some tests are platform-specific and will only run on their respective platforms (Linux, macOS, Windows).

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

## Related Links

- [Netty Project](https://netty.io/)
- [Netty 5 Documentation](https://netty.io/wiki/)
- [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
