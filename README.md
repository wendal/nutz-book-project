# N平台

Nutz主库地址 https://github.com/nutzam/nutz 来个star呗

[![Build Status](https://travis-ci.org/wendal/nutz-book-project.png?branch=v3.x)](https://travis-ci.org/wendal/nutz-book-project)
[license](https://img.shields.io/github/license/mashape/apistatus.svg?maxAge=2592000)]()

## 依赖的环境

* mysql/oracle/pgsql/h2 数据库
* redis 3.0+
* maven 3.3+
* Jdk 8+

**本项目必须依赖Redis数据库** http://redis.io

Redis-Windows 下载地址  https://github.com/MSOpenTech/redis/releases 或者 https://nutz.cn/nutzdw/

后台管理账户密码 admin/123456

* 在线演示地址 https://nutz.cn 
* 管理后台(需要管理员权限才能操作)  https://nutz.cn/adminlte

本项目是由模块化+插件化开发, 请查阅[hotplug开发指南](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-hotplug)

## 模块简介

* nutzcn-core 核心模块,其他模块均依赖它
* nutzcn-webapp web项目模块, 用于启动
* nutzcn-adminlte 后台管理界面
* nutzcn-yvr 论坛模块

## 如何开发

默认情况下, webapp模块仅依赖了core和adminlte,所以启动后只有后台可访问.

在eclipes下, 单击nutzcn-webapp模块, 然后按 Ctrl+Alt+P, 按需加载的模块

若新建模块, 务必按hotplug插件规范的要求添加必要的文件

## 手机客户端

Android官方客户端 

  * 下载地址 http://app.xiaomi.com/detail/419235
  * 源码地址 https://github.com/wendal/NutzCN-Material-Design

iOS客户端

  * 下载地址 https://itunes.apple.com/us/app/nutz-she-qu/id1082195150?l=zh&ls=1&mt=8
  * 源码地址 https://github.com/TuWei1992/NutzCommunity

## License

MIT License或Apache License 双授权协议. -- 事实上我懒得选.

极为宽松,请大胆商用,大胆闭源,本库的代码完全免费. 任选MIT/Apache协议执行即可.