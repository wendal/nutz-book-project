# nutz-book-project
nutz-book 衍生出的project, 与nutzbook同步更新

## 主项目地址

https://github.com/wendal/nutz-book

http://nutzbook.wendal.net

[部署指南](INSTALL.md)

## 当前已经实现或正在实现的功能

* 基本的增删改查,MVC各种用法及文件上传
* Dao关联关系(One/Many/ManyMany)
* 邮件发送及基于3DES的无数据库验证机制
* Quartz计划任务集成
* Shiro集成及权限管理
* Ehcache及DaoCache集成
* redis 集成(配置及拦截器应用)
* socketio 集成及demo聊天工具
* jetbrick模板集成
* beetl模板集成及应用
* 二维码生成及跨屏登陆
* 声明式系统日志
* 基于socialauth的Oauth登陆(客户端,非服务器端实现)
* 集成极光推送(jpush)
* U2F绑定与登陆
* zbus集成(RPC及MQ)

## 关于目录结构变化

nutzbook中的代码是eclipse结构的, 本项目最新的代码已经改为maven格式, 映射关系如下

src -- src/main/java
conf -- src/main/resources
WebContent -- src/main/webapp

eclipse依然可以直接导入本项目

## 依赖的jar包下载

http://nutzbook.wendal.net/jars/

## 在线演示地址

论坛系统(即Nutz社区的官网) https://nutz.cn

管理后台  https://nutz.cn/home

配置: 阿里云, 2G RAM, 缓存, https, 七牛云存储CDN, jpush及socketio均启用