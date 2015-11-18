# 部署指南

## 必备软件

* mysql/oracle/pgsql/h2 数据库
* redis 2.8+
* maven 3.1+

## 通过maven进行简易启动(mysql)

确保本地mysql和redis启动正常, 并创建数据库

```
create database nutzbook default character set utf8;
```

默认数据库密码是root, 如果不符,修改custom/db.properties的值

### 在windows控制台


```
chcp 65001
set MAVEN_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8 -Xmx1G -Xms128m"
mvn jetty:run
```

### linux/mac

```
export MAVEN_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8 -Xmx1G -Xms128m"
mvn jetty:run
```

### 常见问题

* 启动时会自动创建表及相关目录,无需手动导入sql初始化
* 如果报数据库连接错误,请检查db.properties
* 如果访问时提示redis连接错误,请检查redis是否已经启动,且redis.properties中的信息是否正确


## 配置文件说明

* 配置文件在src/main/resources
* oauth_consumer.properties -- OAuth配置文件,请到相关的网站申请key
* custom/cdn.properties -- CDN配置文件
* custom/cron.properties -- 计划任务配置文件
* custom/db.properties -- 数据库配置信息
* custom/jpush.properties -- JPush推送服务的配置信息
* custom/email.properties -- 邮件服务的配置信息
* custom/redis.properties -- redis服务器配置信息
* custom/secken.properties -- 洋葱安全登录APP的配置信息,依赖socketio
* custom/shortit.properties -- "短点"的配置信息
* custom/socketio.properties -- SocketIO的服务器端配置信息
* custom/topic.properties -- 论坛数据配置
* custom/website.properties -- 网站信息配置
* custom/zbus.properties -- ZBus消息总线服务的配置信息


## 用docker镜像启动

通过docker-compose启动(推荐)

```
cd docker
docker-compose up
```

单独启动, 需要自行解决mysql/redis的启动哦

```
docker run -it --rm  -e NUTZBOOK_db.url="jdbc:mysql://192.168.1.111:3306/nutzbook" \
	-e NUTZBOOK_redis.host=192.168.1.111 -v /dev/urandom:/dev/random wendal/nutzbook2:latest
```