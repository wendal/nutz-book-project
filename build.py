#!/usr/bin/python
# -*- coding: utf-8 -*-
import os.path, sys, subprocess
import shutil, zipfile, urllib

if os.path.exists("dst"):
	shutil.rmtree("dst")
os.makedirs("dst/plugins")

# 首先,全部模块安装一次
subprocess.check_call("mvn clean install -Dmaven.test.skip=true", shell=True)

# 逐个插件模块进行编译
for fname in os.listdir(os.getcwd()):
	if not fname.startswith("nutzcn-") :
		continue
	if fname.startswith("nutzcn-webapp") or fname.startswith("nutzcn-core") or fname.startswith("nutzcn-adminlte"):
		continue
	subprocess.call("mvn package assembly:single -Dmaven.test.skip=true", shell=True, cwd=fname)
	
for root, dirs, files in os.walk(os.getcwd()):
		for name in files:
			if name.endswith("jar-with-dependencies.jar") :
				fsource = os.path.join(root, name)
				shutil.copyfile(fsource, "dst/plugins/" + name[0:-26] + ".jar")
			if name.startswith("nutzcn-webapp") and name.endswith(".war") :
				fsource = os.path.join(root, name)
				shutil.copyfile(fsource, "dst/ROOT.war")
				
# 下载tomcat
if not os.path.exists("apache-tomcat-8.5.12-windows-x64.zip"):
	print "download tomcat , please wait"
	urllib.urlretrieve ("http://mirror.bit.edu.cn/apache/tomcat/tomcat-8/v8.5.12/bin/apache-tomcat-8.5.12-windows-x64.zip", "apache-tomcat-8.5.12-windows-x64.zip")
	print "download tomcat complete"
	
# 释放tomcat
with zipfile.ZipFile("apache-tomcat-8.5.12-windows-x64.zip","r") as zip_ref:
	zip_ref.extractall("dst/")
	
# 干掉webapps下的文件
for fname in os.listdir(os.getcwd() + "/dst/apache-tomcat-8.5.12/webapps"):
	shutil.rmtree(os.getcwd() + "/dst/apache-tomcat-8.5.12/webapps/" + fname)
	
# 释放ROOT.war
with zipfile.ZipFile("dst/ROOT.war","r") as zip_ref:
	zip_ref.extractall("dst/apache-tomcat-8.5.12/webapps/ROOT/")
os.remove("dst/ROOT.war")
	
# 设置必要的java属性
with open("dst/apache-tomcat-8.5.12/bin/setenv.bat", "w") as f :
	f.write("set JAVA_OPTS=-Dfile.encoding=UTF-8 -Dlog4j2.disable.jmx=true")

# 拷贝redis
os.makedirs("dst/redis")
shutil.copyfile("C:\\Program Files\\Redis\\redis-server.exe", "dst/redis/redis-server.exe")
shutil.copyfile("C:\\Program Files\\Redis\\redis-cli.exe", "dst/redis/redis-cli.exe")
shutil.copyfile("C:\\Program Files\\Redis\\redis.windows.conf", "dst/redis/redis.windows.conf")

# 拷贝数据库配置文件
os.makedirs("dst/apache-tomcat-8.5.12/webapps/ROOT/WEB-INF/classes/custom")
shutil.copyfile("nutzcn-core/src/main/resources/custom/db.properties", "dst/apache-tomcat-8.5.12/webapps/ROOT/WEB-INF/classes/custom/db.properties")

if os.path.exists("notepad++.exe") :
    shutil.copyfile("notepad++.exe", "dst/notepad++.exe")
    with open(u"dst/修改数据库配置.bat", "w") as f :
	    f.write('''cd %~dp0
notepad++.exe %~dp0\\apache-tomcat-8.5.12\\webapps\\ROOT\\WEB-INF\\classes\\custom\\db.properties''')

with open(u"dst/启动tomcat.bat", "w") as f :
	f.write('''cd %~dp0
cd	apache-tomcat-8.5.12\\
bin\\startup.bat''')
with open(u"dst/关闭tomcat.bat", "w") as f :
	f.write('''cd %~dp0
cd	apache-tomcat-8.5.12\\
bin\\shutdown.bat''')
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
3. 数据库账号密码位于 apache-tomcat-8.5.12\webapps\ROOT\WEB-INF\classes\custom\db.properties

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
