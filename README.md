ghost
=====

# 基于Mybatis 的分表、分库引擎；
* 支持一个库里分多张表；
* 支持单库事务；
* 与spring3、mybatis3无缝集成，不需要修改以前任何代码；
* 其他分表分库基本功能

## 例子
* 根据业务配置rule.xml
   参考https://github.com/yuxwmail/ghost/blob/master/src/test/resources/rules.xml
* 根据业务需要建立、修改数据库
* 修改spring配置，增加多数据源配置，配置Ghost相关信息；
   参考https://github.com/yuxwmail/ghost/blob/master/src/test/resources/applicationContext-ghost.xml


离职代码参考
https://github.com/yuxwmail/ghost/blob/master/src/test/java/org/knot/ghost/core/Main.java