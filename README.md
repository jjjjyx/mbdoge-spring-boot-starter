# mbdoge-spring-boot-starter
适用于 spring boot 2.x 版本的starter ，用于快速创建web项目，内置了大量常用web配置，以及api加密功能

# 2.4.0.90 版本
* 修复
    - ip 工具类屏蔽ip 方法
* 优化
    - WebDefaultSecurityConfigure 能够自定义 AuthenticationProvider
    - bean 的写法与方法名称

# 2.4.0.90 版本
* 修复
    - 一些错误 （忘记了）

# 2.4.0.87 版本
* 修复
    - ip 工具类获取ip 的顺序， 改为先读取 X-Real-IP

# 2.4.0.86 版本
* 新增
    - ip 查询api 支持自定义字段
    - DataLocalTime 工具类
    - 前端传long 转DataLocalTime 转换类

# 2.4.0.85 版本
* 新增
    - Dispatch 的事件回调增加泛型支持

# 2.4.0.82 版本
修复版本的增加方式，改为跟随spring boot 使用的版本
例如在项目使用 2.4.0.82 那么对应的 spring boot 版本建议使用 2.4.0

* 修复
    - 语法格式，语法检查修改
    - 兼容2.4.0 版本，支持cors 的设置 2.4.0 不允许设置 allow-origins = * 现在改为在配置文件中指定allow

# 0.0.8 版本
..

# 0.0.2 版本

## web-api
1. 修改了多语言的配置，阅读了更多 spring boot 姿势，使用了默认的语言配置来进行配置多语言，更加复用框架代码, 避免了重复代码

```properties
# 语言目录，支持多个，不用写classpath:
# 语言目录里必须包含默认语言 即 xxxx.properties
spring.messages.basename=cn/mbdoge/jyx/messages
# 使用默认语言
spring.mvc.locale=zh_CN
# 切换语言
spring.mvc.locale-resolver=accept_header
```

2. 修正了url 查询参数中特殊字符的问题，0.0.1 版本中需要在项目中的config 在自己注册bean 现在不需要了
3. 修改了 MessageSourceAccessor 的命名，由 `webMessageSourceAccessor` -> `messageSourceAccessor`
4. 增加了 language 的测试用例，对 `ControllerHandlerAdviceTest` 测试用例进行了注解补充
5. 删除了 `WebApiProperties` 原因，没有需要的配置项
6. 移动了 `AnalyzeIpGeoException` 的位置，放在了 `cn.mbdoge.jyx.web.util` 包下

### 下一版本 功能

1. 优化ControllerHandlerAdviceTest 增加对 数据库错误的支持
2. api 请求访问日志记录器

## web-security
1. 修复了 `EncodeResponseBodyAdvice#47L` 的检查错误 `TEXT_PLAIN_VALUE -> TEXT_PLAIN`
2. 修复了 `AccessExceptionAdvice` 中定义的异常拦截，被覆盖的问题，调整了该类的order 级别
3. 修改了 `CustomDaoAuthenticationProvider` 使用 MessageSource 作为构造参数，使用 `MessageSourceAccessor` 会获取不到使用者传入的`spring.messages.basename`

# 0.0.1 版本 测试版 单纯的将常规项目中的公共模块进行了整理