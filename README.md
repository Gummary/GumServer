# 简介

不同模式下的服务端:

- blocking
- nonblocking
- asynchronous
- mutipplexing

服务器实现的功能是，在接收到客户端发送的数据后，使用MD5进行加密后返回给客户端。

客户端与服务端之间使用的协议如下：

|  type  | length  | content         |
|--------|---------|-----------------|
| 1 byte | 4 bytes | variable length |

# TODO

- [] 客户端读取写入SocketChannel的过程比较类似，构造一个自己的Socket Channel封装这些逻辑

# requirements

jdk-17