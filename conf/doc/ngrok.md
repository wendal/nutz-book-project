### Ngrok内网穿透神器

### 使用原则

仅供Nutz社区的各位Nutzer开发时使用,严禁部署非法应用, 严禁大流量应用

### 下载链接

[配置文件](/ngrok/config/download)

[Ngrok客户端](https://ngrok.com/download/1)

### 使用方法

下载配置文件(ngrok.yml)及客户端后, 启动之, 以转发到8080端口为例

```
ngrok -config ngrok.yml 8080
```

等待出现如下信息, 请注意,子域名与用户名是绑定的,不允许自定义

```
ngrok

Tunnel Status                 online
Version                       1.7/1.7
Forwarding                    http://wendal.ngrok.nutz.cn:9080 -> 127.0.0.1:8080
Forwarding                    https://wendal.ngrok.nutz.cn:9080 -> 127.0.0.1:8080
Web Interface                 127.0.0.1:4040
\# Conn                        6
Avg Conn Time                 30758.59ms



HTTP Requests
-------------

GET /nutzbook/rs/logo.png     200 OK
```

本地启动tomcat或其他web应用后, 访问对应的地址即可(替换成自己的地址哦)

```
http://wendal.ngrok.nutz.cn:9080/nutzbook/
```