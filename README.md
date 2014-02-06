About 
======

RssToMobiService (rtms) is a service (you can treat it as a tool) helps you to fetch RSS and generate mobi file for your kindle device.


------------

Dependency
--------

- python env (pip)
- redis
- maven
- one of Java's IDEs such as Eclipse, IntelliJ idea (optional)

> **NOTE**
>
> - python env (pip): two script will need python's env (fullTxt and kindlestrip)
> - redis: used to store image's json object and cache processed entry desc
> - maven: it is necessary that jar will be built with it
> - Java's IDE: can help you edit some config item and code, it depends on yourself.

### How to use
> assume that you have installed those dependencies.

* enter your-local-repository

```
cd RssToMobiService/src/main/resources/
```

* modify some config file. ***mail.properties*** and ***feedlinks.txt*** must be modified generally. Check it one by one!

* run dispatch script

```
(sudo) sh dispatch.sh
```

* return to project's root dir and build && generate jar file

```
mvn assembly:assembly
```

* init python script's dependency packages

```
(sudo) pip install feedparser
(sudo) pip install readability-lxml
```
* start redis server (command see ***resources/redisMaintain.sh***)

* goto target dir && execute jar

```
java -jar RssToMobiService-1.0-SNAPSHOT.jar
```

> **NOTE**
>
> - you must goto amazon's website to set your kindle's **receive-email**
> - you should change the **feedlinks.txt**'s rss url
> - these are apse some config items about mobi file you should pay attention to (**rtms.properites**)

### What's more
- you can make it as a deamon daily service.
- optimize entry fetch/parse and image download 

### Q&A
- [my blog about this repository](http://blog.csdn.net/yanghua_kobe/article/details/18950969)
- [my email](mailto://yanghua1127@gmail.com)
- [my weibo](http://weibo.com/yanghua1127)














