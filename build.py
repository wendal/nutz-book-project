#!/usr/bin/python
# -*- coding: utf-8 -*-
import os.path, sys, subprocess
import shutil, zipfile, urllib

if os.path.exists("dst"):
    shutil.rmtree("dst")
os.makedirs("dst/plugins")

# 首先,全部模块安装一次
subprocess.check_call("mvn package install -Dmaven.test.skip=true", shell=True)

# 逐个插件模块进行编译
for fname in os.listdir(os.getcwd()):
    if not fname.startswith("nutzcn-") :
        continue
    if fname.startswith("nutzcn-webapp") or fname.startswith("nutzcn-core") or fname.startswith("nutzcn-adminlte"):
        continue
    subprocess.call("mvn package assembly:single -Dmaven.test.skip=true -U", shell=True, cwd=fname)
    
for root, dirs, files in os.walk(os.getcwd()):
        for name in files:
            if name.startswith("nutzcn-starter-") and name.endswith("jar-with-dependencies.jar"):
                fsource = os.path.join(root, name)
                shutil.copyfile(fsource, "dst/starter.jar")
            elif name.endswith("shaded.jar") :
                fsource = os.path.join(root, name)
                shutil.copyfile(fsource, "dst/plugins/" + name[0:-11] + ".jar")
            elif name.startswith("nutzcn-webapp") and name.endswith(".war") :
                fsource = os.path.join(root, name)
                shutil.copyfile(fsource, "dst/ROOT.war")
				
# 创建Runnable War
subprocess.check_call("java -jar starter.jar -inject ROOT.war -output nutzcn.jar", shell=True, cwd="dst/")
#subprocess.check_call("pack200 -r -G nutzcn.jar", shell=True, cwd="dst/")
os.remove("dst/ROOT.war")
os.remove("dst/starter.jar")

# 拷贝redis
os.makedirs("dst/redis")
shutil.copyfile("C:\\Program Files\\Redis\\redis-server.exe", "dst/redis/redis-server.exe")
shutil.copyfile("C:\\Program Files\\Redis\\redis-cli.exe", "dst/redis/redis-cli.exe")
shutil.copyfile("C:\\Program Files\\Redis\\redis.windows.conf", "dst/redis/redis.windows.conf")

# 拷贝数据库配置文件
os.makedirs("dst/custom")
shutil.copyfile("nutzcn-core/src/main/resources/custom/db.properties", "dst/custom/db.properties")

with open(u"dst/启动.bat", "w") as f :
    f.write('''cd %~dp0
java -Dfile.encoding=UTF-8 -jar nutzcn.jar ''')
with open(u"dst/启动redis.bat", "w") as f :
    f.write('''cd %~dp0
cd redis
start redis-server.exe redis.windows.conf''')

# 写一下说明文件
with open(u"dst/读我.txt", "w") as f :
    f.write('''
# 启动须知

1. 必须使用JDK8
2. 本项目依赖Redis, 可以双击"启动redis.bat", 启动一个redis, 若已经安装redis,请无视
3. 数据库账号密码位于 custom\db.properties

# 后台管理系统

1. 地址 http://127.0.0.1:8080/adminlte
2. 账号密码  admin/123456

# 插件的使用方法

1. 插件可以在plugins目录找到
2. 登录后台管理系统后, 系统设置, 插件管理, 上传, 然后启用之
3. 建议上传yvr插件, 启用完成后, 可以访问 http://127.0.0.1:8080/yvr 社区系统

项目地址: http://git.oschina.net/wendal/nutz-book-project

反馈和意见,请访问 https://nutz.cn
    ''')
