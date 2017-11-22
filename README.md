
> **DubboCloud项目是基于Dubbo二次开发的云开源项目**

#### 前言

目前市面上Spring boot/Cloud越来越火,不可否认确实很好用,里面集成很多功能套件,包括我自己也是Spring for all核心成员之一,但是在实际工作中,尤其是中小公司目前还是以Dubbo为主,主要原因是:
* 使用Spring boot/Cloud需要一定学习和试错成本
* 一些公司用了很长时间建立了自己相对成熟和稳定的服务化解决方案,所以也就没有足够的动力来进行替换。
* 虽然Spring boot/Cloud更新相对较快,社区非常活跃,文档非常丰富,但是从公司实际生产的角度来说,如果在线上遇到BUG,依然不会那么及时的得到更新。

#### 维护发起目的

有这个想法的前提其实还是基于我们公司目前采用的还是Spring boot+Dubbo的方式,Dubbo在Spring Cloud中只是扮演了服务治理的角度,而Spring Cloud本身包括了很多功能组件可供使用,于是将Spring Cloud的好用的功能组件移值到Dubbo中变成Dubbo Cloud的想法就产生了。




#### 主要维护者

**程超、陈子桥、张军涛**

#### 目前已经实现的功能点

* 修复了Dubbo hessian下面无法RpcContext传递数据的问题
* 增加了基于Dubbo的调用链日志
* 增加了Dubbo mock系统的配置信息
* 增加了Hystrix功能，但目前还没有界面显示，正在开发中
* 增加了基于Dubbo的Spring boot starter


#### 参考学习

参考的Dubbo官网文档是
https://dubbo.gitbooks.io/dubbo-dev-book/build.html