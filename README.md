# nutz-book-project
nutz-book 衍生出的project, 与nutzbook同步更新

## 主项目地址

https://github.com/wendal/nutz-book

http://nutzbook.wendal.net

## 依赖的jar包下载

http://nutzbook.wendal.net/jars/

## 当前已经实现或正在实现的功能

* 基本的增删改查
* Dao关联关系
* 邮件发送
* Quartz计划任务
* Shiro集成及权限管理
* Ehcache及DaoCache集成
* snakerflow工作流集成(未全部完成)
* 自定义表单(未全部完成)

## 在线演示地址

http://115.28.27.32:8090/nutzbook/

若不可访问,请报issue

演示机配置: 树莓派, 广州长宽

## war包下载使用说明

地址 http://nutzbook.wendal.net/wars/

下载后请更名为nutzbook.war 就是把版本号去掉

数据库配置文件在 db.properties

默认已添加mysql/oracle/pgsql驱动, 其他数据库请自行添加驱动

oracle用户留意一下  db.validationQuery=select 1 from dual

# 测试邮件的账号密码

由于有人恶意发email, 书中的邮箱密码已修改
请配置为自己的邮箱信息

# Mavan用户

运行,需要先启动一个redis哦

```
mvn jetty:run
```