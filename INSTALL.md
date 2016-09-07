# 部署指南

## 必备软件

* mysql/oracle/pgsql/h2 数据库
* redis 2.8+
* maven 3.1+
* jdk 8+

PS: 通过docker部署的话无需上述软件

## 部署方式

* [通过maven进行简易启动(mysql)](INSTALL_MAVEN.md)
* [通过maven生成war](INSTALL_WAR.md)
* [通过docker启动](INSTALL_DOCKER.md)
* [裸服务器部署详解](INSTALL_NEW.md)

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
* custom/jpush.properties -- JPush推送/小米服务的配置信息
* custom/email.properties -- 邮件服务的配置信息
* custom/redis.properties -- redis服务器配置信息
* custom/shortit.properties -- "短点"的配置信息
* custom/topic.properties -- 论坛数据配置
* custom/website.properties -- 网站信息配置

## 自定义的基本步骤

1. 修改website.properties的网站信息
2. 修改logo文件,位于src/main/resources/webapp/rs/logo/logo.png, logo2.png
3. 配置邮箱信息email.properties
4. 如需第三方登陆,例如github/qq,配置oauth_consumer.properties,否则请在website.properties关闭之

