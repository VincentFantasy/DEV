Weixin Java Tools 微信公众号/企业号开发Java SDK
=====================================
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.binarywang/weixin-java-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.binarywang/weixin-java-parent)
[![Build Status](https://travis-ci.org/Wechat-Group/weixin-java-tools.svg?branch=develop)](https://travis-ci.org/Wechat-Group/weixin-java-tools)

### 注意：
1. ***本项目Fork自chanjarster/weixin-java-tools，但由于原项目已停止维护，故单独维护和发布，且发布到maven上的groupId也会不同，详细信息见下文。***
1. ***自2.0.0版本以来，公众号的接口调整比较大，主要是为了解决主接口类过于庞大不方便管理的问题，将接口实现代码按模块进行拆分。***
1. 本SDK要求的最低JDK版本是7，为满足少量还在使用JDK6的用户的需求，特意抽出独立的代码分支项目，请参考 https://github.com/binarywang/weixin-java-tools-for-jdk6 ，其他更早的JDK版本则需要自己改造实现；
1. 最新更新：2016-11-30 发布2.4.0正式版！

===========

## 开发交流方式及注意事项：
1. QQ群：343954419（推荐点击按钮入群： [![Join QQ Group](http://pub.idqqimg.com/wpa/images/group.png)](http://shang.qq.com/wpa/qunwpa?idkey=731dc3e7ea31ebe25376cc1a791445468612c63fd0e9e05399b088ec81fd9e15) 或 [![Join QQ Group](http://pub.idqqimg.com/wpa/images/group.png)](http://jq.qq.com/?_wv=1027&k=40lRskK)，如果无反应，可以自行搜索群号进行添加 ）
1. 由于群容量有限，即将爆满，故开启付费入群模式以保证只有真实交流需求的人进入，并为保证群的活跃度，将不定期清理长时间不活跃的同学；
1. 微信群： 因微信群已达到100人限制，故如有想加入微信群的，请入QQ群后联系管理员，提供微信号以便邀请加入；
1. 新手提问前，请先阅读此文章：http://t.cn/RV93MRB
1. 寻求帮助时需贴代码或大长串异常信息的，请利用http://paste.ubuntu.com
1. 有功能需求或由于微信官方接口调整导致的代码问题，可以直接提出issue，便于讨论追踪问题；
1. 详细开发文档请看 [Wiki](https://github.com/wechat-group/weixin-java-tools/wiki)，部分文档可能未能及时更新，如有发现，可以及时上报或者自行修改。
1. 微信公众号官方文档入口地址：http://mp.weixin.qq.com/wiki （注意，从网上搜到的文档有的虽然地址前面跟这个一样，但明显左侧菜单不一致，是旧的文档，注意不要看错文档）。
1. 各个模块的Javadoc可以在线查看：[weixin-java-mp](https://binarywang.github.io/weixin-java-mp-javadoc/)、[weixin-java-common](https://binarywang.github.io/weixin-java-common-javadoc/)、[weixin-java-cp](https://binarywang.github.io/weixin-java-cp-javadoc/)

===========

## 版本说明
1. 本项目定为每两个月发布一次正式版，版本号格式为X.X.0（如2.1.0，2.2.0等），月底发布新版本，遇到重大问题需修复会及时提交新版本，欢迎大家随时提交Pull Request；
1. BUG修复和新特性一般会先发布成小版本作为临时测试版本（如2.0.1-beta，2.0.2-beta等，即尾号不为0，并添加beta字样，以区别于正式版）；
1. 目前最新版本号为 [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.binarywang/weixin-java-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.binarywang/weixin-java-parent) ，也可以通过访问链接 [【公众号】](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.binarywang%22%20AND%20a%3A%22weixin-java-mp%22) 、[【企业号】](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.binarywang%22%20AND%20a%3A%22weixin-java-cp%22)
分别查看所有最新的版本。 

## Maven & Gradle 最新正式版本

* 公众号（订阅号、服务号）：

maven：
```xml
<dependency>
  <groupId>com.github.binarywang</groupId>
  <artifactId>weixin-java-mp</artifactId>
  <version>2.4.0</version>
</dependency>
```
gradle：
```groovy
compile 'com.github.binarywang:weixin-java-mp:2.4.0'
```

* 企业号：

maven：
```xml
<dependency>
  <groupId>com.github.binarywang</groupId>
  <artifactId>weixin-java-cp</artifactId>
  <version>2.4.0</version>
</dependency>
```
gradle：
```groovy
compile 'com.github.binarywang:weixin-java-cp:2.4.0'
```

===========

#### 本项目主要存放在github上，地址为 :
* https://github.com/wechat-group/weixin-java-tools
* ===========但同时会在其他几个网站同步更新，地址分别是:
* https://bitbucket.org/binarywang/weixin-java-tools
* http://git.oschina.net/binary/weixin-java-tools
* https://git.coding.net/binarywang/weixin-java-tools.git

===========
## 可参考的Demo项目
#### 目前都是公众号的，风格不同，欢迎提供更多的demo供新手参考:
1. https://github.com/wechat-group/weixin-mp-demo 
1. https://github.com/wechat-group/weixin-mp-multi-demo (支持多公众号)
1. https://github.com/wechat-group/weixin-java-tools-springmvc
1. https://github.com/wechat-group/weixin-mp-demo-springboot


===========
## 关于代码贡献
1. 非常欢迎和感谢对本项目发起Pull Request的同学，本项目代码风格为使用2个空格代表一个Tab，因此在提交代码时请注意一下，否则很容易在IDE格式化代码后与原代码产生大量diff，这样会给其他人阅读代码带来极大的困扰。
1. 为了便于设置，本项目引入editorconfig插件，请使用eclipse的同学在贡献代码前安装相关插件，IntelliJ IDEA则自带支持，无需额外安装插件。
1. 本项目可以采用两种方式接受代码贡献：
  * 第一种就是基于[Git Flow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)开发流程，因此在发起Pull Request的时候请选择develop分支，详细步骤参考后文。
  * 另外一种贡献代码的方式就是加入SDK Developers开发组，前提是对自己的代码足够自信就可以申请加入，加入之后可以随时直接提交代码，但要注意对所做的修改或新增的代码进行单元测试，保证提交代码没有明显问题，具体加入方式，请咨询QQ群管理员[![点击这里给我发消息](http://wpa.qq.com/pa?p=2:1211415707:51)](http://wpa.qq.com/msgrd?v=3&uin=1211415707&site=qq&menu=yes)。

## PR方式贡献代码步骤
* 在 GitHub 上 `fork` 到自己的仓库，如 `my_user/weixin-java-tools`，然后 `clone` 到本地，并设置用户信息。
```
$ git clone git@github.com:my_user/weixin-java-tools.git
$ cd weixin-java-tools
$ git config user.name "yourname"
$ git config user.email "your email"
```
* 修改代码后提交，并推送到自己的仓库。
```
$ #do some change on the content
$ git commit -am "Fix issue #1: change something"
$ git push
```
* 在 GitHub 网站上提交 Pull Request。
* 定期使用项目仓库内容更新自己仓库内容。
```
$ git remote add upstream https://github.com/wechat-group/weixin-java-tools
$ git fetch upstream
$ git checkout develop
$ git rebase upstream/develop
$ git push -f origin develop
```
