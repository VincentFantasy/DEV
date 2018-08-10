## 全能微信Java开发工具包（SDK）
### 支持包括微信支付、开放平台、公众号、企业微信/企业号、小程序等微信功能的后端开发。
---------------------------------
[![Maven Central](https://img.shields.io/maven-central/v/com.github.binarywang/weixin-java-parent.svg)](http://mvnrepository.com/artifact/com.github.binarywang/weixin-java-parent)
[![Build Status](https://travis-ci.org/Wechat-Group/weixin-java-tools.svg?branch=develop)](https://travis-ci.org/Wechat-Group/weixin-java-tools)
[![使用IntelliJ IDEA开发维护](https://img.shields.io/badge/IntelliJ%20IDEA-提供支持-blue.svg)](https://www.jetbrains.com/idea/)

---------------------------------
### 重要信息
1. **微信公众号【WX开发助手】已开通，欢迎[扫码](qrcodes/mp_qrcode.jpg)或者在微信中搜索`weixin-java-tools`或者`WX开发助手`关注，本公众号会及时通知SDK相关更新信息，并不定期分享微信开发相关技术知识。**
1. **最近微信支付爆出的所谓漏洞是官方的老版的微信支付所谓的SDK （就是一个demo）的代码漏洞，使用我们的SDK不存在此问题，如果不放心，检查下自己项目所依赖的xstream版本是否≥1.4.9，前提是使用了weinxin-java-pay提供的回调通知解析代码。**
1. 最新更新：**2018-06-22 发布[【3.1.0正式版】](https://github.com/Wechat-Group/weixin-java-tools/releases)**！
1. [开源中国本项目的首页](https://www.oschina.net/p/weixin-java-tools-new)，欢迎大家积极留言评分 🙂
1. 新手重要提示：本项目仅是一个开发工具包（即SDK），未提供Web实现，建议使用maven或gradle引用本项目即可使用本SDK提供的各种功能，详情可参考[【Demo项目】](demo.md)或本项目中的部分单元测试代码；另外微信开发新手请务必阅读[开发文档Wiki首页](https://github.com/Wechat-Group/weixin-java-tools/wiki)的常见问题部分，可以少走很多弯路，节省不少时间。
1. [出现`Illegal key size`问题的解决办法](https://github.com/Wechat-Group/weixin-java-tools/wiki/%E5%8A%A0%E8%A7%A3%E5%AF%86%E7%9A%84%E5%BC%82%E5%B8%B8%E5%A4%84%E7%90%86%E5%8A%9E%E6%B3%95) （太多人遇到此问题而不知所措，因此特意置顶，希望能引起新手的注意，其他常见问题请查阅 [【开发文档Wiki】](https://github.com/wechat-group/weixin-java-tools/wiki)首页）

--------------------------------
### 其他说明
1. 本项目Fork自chanjarster/weixin-java-tools，但由于原项目已停止维护，故单独维护和发布，且发布到maven上的groupId也会不同，详细信息见下文。
1. SDK详细开发文档请查阅 [【开发文档Wiki】](https://github.com/wechat-group/weixin-java-tools/wiki)，部分文档可能未能及时更新，如有发现，可以及时上报或者自行修改。
1. **阅读源码的同学请注意，本SDK为简化代码编译时加入了lombok支持，如果不了解lombok的话，请先学习下相关知识；**
1. 如有新功能需求，发现BUG，或者由于微信官方接口调整导致的代码问题，可以直接在[【Issues】](https://github.com/Wechat-Group/weixin-java-tools/issues)页提出issue，便于讨论追踪问题；
1. 如果想贡献代码，请阅读[【代码贡献指南】](contribution.md)；
1. **如果本SDK对您有所帮助，欢迎对我们的努力进行肯定，可以扫描[【支付宝付款码】](qrcodes/alipay_qrcode.jpg)或者[【微信支付二维码】](qrcodes/wepay_qrcode.jpg)进行打赏，或者直接前往[【托管于码云的项目首页】](http://gitee.com/binary/weixin-java-tools)，在评论区上方可以找到“捐助”按钮。非常感谢各位打赏和捐助的同学！**
1. 各个模块的Javadoc可以在线查看：[weixin-java-miniapp](http://binary.ac.cn/weixin-java-miniapp-javadoc/)、[weixin-java-pay](http://binary.ac.cn/weixin-java-pay-javadoc/)、[weixin-java-mp](http://binary.ac.cn/weixin-java-mp-javadoc/)、[weixin-java-common](http://binary.ac.cn/weixin-java-common-javadoc/)、[weixin-java-cp](http://binary.ac.cn/weixin-java-cp-javadoc/)、[weixin-java-open](http://binary.ac.cn/weixin-java-open-javadoc/)
1. 本SDK要求的最低JDK版本是1.7，还在使用JDK6的用户请参考[【此项目】]( https://github.com/binarywang/weixin-java-tools-for-jdk6) ，而其他更早的JDK版本则需要自己改造实现。
1. 本SDK项目在以下代码托管网站同步更新:
* 码云：https://gitee.com/binary/weixin-java-tools
* GitHub：https://github.com/wechat-group/weixin-java-tools

---------------------------------
### 技术交流方式
1. QQ群：（**注意：目前为付费群，刚入群会有5分钟禁言，稍等片刻即可正常发言**） [![加入QQ群](https://img.shields.io/badge/QQ群-343954419-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=731dc3e7ea31ebe25376cc1a791445468612c63fd0e9e05399b088ec81fd9e15) 或 [![加入QQ群](https://img.shields.io/badge/QQ群-343954419-blue.svg)](http://jq.qq.com/?_wv=1027&k=40lRskK)，推荐点击按钮入群，当然如果无法成功操作，请自行搜索群号`343954419`进行添加；
1. 由于群容量有限，为了维持运营千人QQ群的所需支付的QQ年费会员费用，故开启付费入群模式，申请者只需支付少量金额即可加入，这样也可以保证只有真实交流需求的人进入，避免闲杂做广告人员的乱入；当然如果确实因为各种原因无法付费入群的，请联系群主说明原因即可入群；
1. 微信群： 因微信群已达到100人限制，故如有想加入微信群的，可以加[【微信二维码】](qrcodes/wechat_qrcode.jpg)此微信号以便邀请加入（请注明“申请加入微信开发群”），或者加入QQ群后联系管理员，提供微信号以便邀请加入；
1. 新手提问前，请先阅读此[【文章】](http://www.dianbo.org/9238/stone/tiwendezhihui.htm)；
1. 寻求帮助时需贴代码或大长串异常信息的，请利用http://paste.ubuntu.com

---------------------------------
### 版本说明
1. 本项目定为大约每两个月发布一次正式版，版本号格式为X.X.0（如2.1.0，2.2.0等），遇到重大问题需修复会及时提交新版本，欢迎大家随时提交Pull Request；
1. BUG修复和新特性一般会先发布成小版本作为临时测试版本（如2.4.5.BETA，2.4.6.BETA等，即尾号不为0，并添加BETA字样，以区别于正式版）；
1. 目前最新版本号为 [![Maven Central](https://img.shields.io/maven-central/v/com.github.binarywang/weixin-java-parent.svg)](http://mvnrepository.com/artifact/com.github.binarywang/weixin-java-parent) ，也可以通过访问链接 [【微信支付】](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.binarywang%22%20AND%20a%3A%22weixin-java-pay%22) 、[【微信小程序】](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.binarywang%22%20AND%20a%3A%22weixin-java-miniapp%22) 、[【公众号】](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.binarywang%22%20AND%20a%3A%22weixin-java-mp%22) 、[【企业微信】](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.binarywang%22%20AND%20a%3A%22weixin-java-cp%22)、[【开放平台】](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.binarywang%22%20AND%20a%3A%22weixin-java-open%22)
分别查看所有最新的版本。 

---------------------------------
## Maven引用
注意：最新版本（包括测试版）为 [![Maven Central](https://img.shields.io/maven-central/v/com.github.binarywang/weixin-java-parent.svg)](http://mvnrepository.com/artifact/com.github.binarywang/weixin-java-parent)，以下为最新正式版。

```xml
<dependency>
  <groupId>com.github.binarywang</groupId>
  <artifactId>（不同模块参考下文）</artifactId>
  <version>3.1.0</version>
</dependency>
```
* 各模块的`artifactId`：
  - 微信小程序：`weixin-java-miniapp`   
  - 微信支付：`weixin-java-pay`
  - 微信开放平台：`weixin-java-open`   
  - 公众号：`weixin-java-mp`    
  - 企业号/企业微信：`weixin-java-cp`
