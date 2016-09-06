# 新服务部署指南

本文以Centos 6.7 x64 mini安装版为例

## 安装必要的包


```
# 更新全部内置包
yum update
# 安装必要的开发工具
yum groupinstall "Development tools"
yum install -y pcre-devel openssl-devel vim perl-devel libcurl-devel
```

## 挂载分区

一般来说, 大多数vps都分2个盘, vda 和 vdb, 前者系统盘,后者数据盘

如果没分区,用fdisk分区后, 执行

```
mkfs.ext4 /dev/vdb1
```

```
vim /etc/fstab

#添加一行
/dev/vdb1          /opt            ext4    defaults         0 0
```

并挂载

```
mount -a
```

检查结果

```
mount

#输出

/dev/vda2 on / type ext4 (rw)
proc on /proc type proc (rw)
sysfs on /sys type sysfs (rw)
devpts on /dev/pts type devpts (rw,gid=5,mode=620)
tmpfs on /dev/shm type tmpfs (rw)
/dev/vdb1 on /opt type ext4 (rw)
none on /proc/sys/fs/binfmt_misc type binfmt_misc (rw)
```

关联/data到/opt

```
rm -fr /data
ln -s /opt /data
```

## 安装git(可选,为了下载github上的最新源码)


```
cd /tmp
wget https://www.kernel.org/pub/software/scm/git/git-2.7.0.tar.xz
tar xf git-2.7.0.tar.xz
cd git-2.7.0
./configure --prefix=/usr
make -j8
make install
```

## 安装nodejs和apidocs(可选,为了生成api文档)

```
curl --silent --location https://rpm.nodesource.com/setup | bash -
yum install -y nodejs
npm install apidoc -g
```

## 安装redis

```
cd /tmp
wget http://download.redis.io/releases/redis-3.0.6.tar.gz
tar xf redis-3.0.6.tar.gz
cd redis-3.0.6
make
make install
cp redis.conf /etc/
```

修改配置,因为只有一个分区,全部塞到/opt好了

```
vim /etc/redis.conf

#把dir ./
#改成 

dir /opt/

#然后加上
bind 127.0.0.1
```

启动一下

```
redis-server /etc/redis.conf &
```

## 安装jdk

```
cd /opt/
wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u102-b14/jdk-8u102-linux-x64.tar.gz"
tar xzf jdk-8u102-linux-x64.tar.gz
ln -s /opt/jdk1.8.0_102 /opt/jdk
rm jdk-8u102-linux-x64.tar.gz
```

## 安装maven

```
cd /opt
curl -0 http://mirrors.noc.im/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz | tar -zx
ln -s /opt/apache-maven-3.3.9 /opt/maven
```

## 安装mysql数据库

```
rpm -Uvh https://mirror.webtatic.com/yum/el6/latest.rpm
yum remove mysql-libs
yum install mysql55w mysql55w-server
```

修改默认编码

```
vim /etc/my.cnf
```

确保有类似的代码

```
[client]
port            = 3306
socket          = /var/lib/mysql/mysql.sock
default-character-set=utf8

[mysqld]
collation-server = utf8_unicode_ci
init-connect='SET NAMES utf8'
character-set-server = utf8
binlog_format=row

```

启动之

```
service mysqld start
```

连接一下,默认无密码

```
mysql -uroot
```

执行 status看编码信息

```
Server characterset:    utf8
Db     characterset:    utf8
Client characterset:    utf8
Conn.  characterset:    utf8
```

修改密码,退回命令行,执行

```
mysqladmin -u root password root
```




## 安装openresty(nginx,可选)

```
cd /tmp
curl -0 https://openresty.org/download/ngx_openresty-1.9.7.1.tar.gz | tar -zx
cd ngx_openresty-1.9.7.1
./configure --prefix=/opt/openresty
gmake -j8
gmake install

```

## 环境变量

```
vim /etc/profile.d/nutzcn.sh
```

加入下列

```
export JAVA_HOME=/opt/jdk
export MAVEN_HOME=/opt/maven
export PATH=$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin:/opt/bin:/opt/openresty/nginx/sbin
```

然后重启机器

## 下载源码

```
cd /opt
git clone https://github.com/wendal/nutz-book-project.git
mv nutz-book-project nutzcn
cd nutzcn
```

开始编译

```
mvn -Dmaven.test.skip=true package
```

## 建数据库

```
mysql -uroot -p

create database nutzbook default character set utf8;
```

## 启动项目

```
cd /opt/nutzcn
mvn -Dmaven.test.skip=true -Dlog4j2.disable.jmx=true clean jetty:run
```