# nutz-book-project
nutz-book 衍生出的project, 与nutzbook同步更新

## 主项目地址

https://github.com/wendal/nutz-book

http://nutzbook.wendal.net

## 依赖的jar包下载

http://nutzbook.wendal.net/jars/

## 当前已经实现或正在实现的功能

* 基本的增删改查,MVC各种用法及文件上传
* Dao关联关系(One/Many/ManyMany)
* 邮件发送及基于3DES的无数据库验证机制
* Quartz计划任务集成
* Shiro集成及权限管理
* Ehcache及DaoCache集成
* snakerflow工作流集成(未全部完成)
* 自定义表单(未全部完成)
* redis 集成(配置及拦截器应用)
* sockerio 集成及demo聊天工具
* jetbrick模板集成
* beetl模板集成及应用
* 二维码生成及跨屏登陆
* 声明式系统日志
* 基于socialauth的Oauth登陆(客户端,非服务器端实现)

## 在线演示地址

https://nutz.cn/user/login

若不可访问,请报issue

演示机配置: 阿里云, 1G RAM

## war包下载使用说明

地址 http://nutzbook.wendal.net/wars/

下载后请更名为nutzbook.war 就是把版本号去掉

数据库配置文件在 db.properties

默认已添加mysql/oracle/pgsql驱动, 其他数据库请自行添加驱动

oracle用户留意一下  db.validationQuery=select 1 from dual

# 测试邮件的账号密码

由于有人恶意发email, 书中的邮箱密码已修改
请在conf/custom/email.properties配置自己的邮箱信息

# Mavan用户

运行,需要先启动一个mysql和redis哦

```
mvn jetty:run
```

# 用docker镜像启动

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