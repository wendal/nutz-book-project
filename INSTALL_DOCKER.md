# 通过docker启动

## 通过docker-compose启动(推荐)

```
cd docker
docker-compose up
```

第一次启动可能报mysql无法连接,原因是数据库第一次启动比web慢一些,关掉重开一次即可

### docker-compose.yml说明

其中的web节点下可以配置环境变量

* NUTZBOOK_XXX custom下的配置文件属性, 所有参数均可配置
* OAUTH_XXX    oauth配置参数,主要是qq及github登陆信息
* NGROK_XXX    识ngrok配置信息,填入auth_token即可实现外网访问,详情请到论坛查看 

## 单独启动, 需要自行确保mysql/redis的启动哦

```
docker run -it --rm  -e NUTZBOOK_db.url="jdbc:mysql://192.168.1.111:3306/nutzbook" \
	-e NUTZBOOK_redis.host=192.168.1.111 -v /dev/urandom:/dev/random wendal/nutzbook:latest
```

环境变量可参看*docker-compose.yml说明*