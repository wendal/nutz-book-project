### Ngrok内网穿透神器

### 使用原则

1. 仅供Nutz社区的用户开发Nutz相关的项目时使用

2. 严禁部署非法应用

3. 严禁部署大流量应用

4. 不允许部署长期运行的项目, 不保证域名地址的不变

5. 访问权限每周更新一次, 初定每周六傍晚

### 下载链接

[配置文件](/ngrok/config/download)

[Ngrok客户端v1.x 不支持2.x客户端](https://ngrok.com/download/1)

http://pan.baidu.com/s/1eQptxvk 客户端网盘地址

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
# Conn                        6
Avg Conn Time                 30758.59ms



HTTP Requests
-------------

GET /nutzbook/rs/logo.png     200 OK
```

本地启动tomcat或其他web应用后, 访问对应的地址即可(替换成自己的地址哦)

```
http://wendal.ngrok.nutz.cn:9080/nutzbook/
```

### 注意事项

1. 可以通过80端口访问,经由nginx转发,最长链接10分钟,最大POST body大小为1mb
2. 本服务的可用性保证是95%
