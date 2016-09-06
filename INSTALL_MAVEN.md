# 通过maven进行简易启动(mysql)

默认数据库密码是root, 如果不符,修改custom/db.properties的值

### 在windows控制台

```
chcp 65001
set MAVEN_OPTS="-Dlog4j2.disable.jmx=true -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Dehcache.disk.store.dir=/data -Xmx1G -Xms128m"
mvn jetty:run
```

### linux/mac

```
export MAVEN_OPTS="-Dlog4j2.disable.jmx=true -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Dehcache.disk.store.dir=/data -Xmx1G -Xms128m"
mvn jetty:run
```