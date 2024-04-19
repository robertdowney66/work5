# work4
## 项目接口文档

[接口文档](https://apifox.com/apidoc/shared-0d2df84b-f7ac-4398-bdcf-78027d357195)

## 预留账号密码

testman
1234

## 项目结构图
```
└─src
    ├─main
    │  ├─java
    │  │  └─com
    │  │      └─yuyu
    │  │          ├─config       // 配置类
    │  │          ├─controller   // 控制层
    │  │          ├─dao          // 数据层
    │  │          ├─exception    // 异常处理类
    │  │          ├─filter       // 过滤器
    │  │          ├─handler      // 处理security的异常
    │  │          │  └─impl      
    │  │          ├─pojo         // 实体类
    │  │          │  ├─DO        // 数据访问层
    │  │          │  └─DTO       // 数据传输层对象
    │  │          ├─scheduled    // 用于时间控制的类
    │  │          ├─service      // 业务层
    │  │          │  └─impl      // 接口
    │  │          └─utils        // 工具类
    │  └─resources
    │      ├─mapper
    │      ├─META-INF
    │      └─static
    │          ├─image
    │          └─video
    └─test
        └─java
            └─com
                └─yuyu

```
## 技术栈
使用了springboot+mybatisplus+mybatis+springsecurity+mysql+redis+docker实现了视频网站的接口
## 项目结构介绍
### mysql层面
1. 编辑了user 用户表，like 点赞表，video 视频表，comment 评论表 四张基本表
2. 编辑了user_fan 一张中间表，用于统计粉丝
3. 编辑了menu,role,role_menu，三张用于用户权限认证的表

### java层面
#### 1. pojo层
##### DO
   - User
   - Comment
   - Like
   - UserFan
   - Video
   - Menu
##### DTO
   - UserDTO 用于传输User的部分信息
##### 其他
   - LoginUser 用于用户登录与权限使用
   - result
   用于返回前端的信息封装类
#### 2. dao层
    对应上述pojo层中DO中的实体类的DAO
#### 3. service层
- UserService 为用户模块相关的业务层
- VideoService 为视频模块相关的业务层
- SocialService 为社交模块相关的业务层
- InteractService 为互动模块相关的业务层

#### 4. controller层
用于四个对应模块的展示层

#### 5. utils
- FastJsonRedisSerializer redis使用FastJson序列化所需的工具类

- JacksonObjectMapper 用于将json时间格式化
- JwtUtil 用于jwt相关的操作的工具类
- RedisCache 用于redis相关操作的工具类
- ServiceUitls 用于service层所需操作的工具类
- UploadUtil 用于上传视频的工具类
- WebUtils 用于web相关操作的工具类
  
#### 6.scheduled（随便取了一个名字，也不知道取啥名字）
- TimeScheduled 用于定时操作

#### 7.handler
里面就是继承了security中的俩个handler，用于拦截登录和权限的出现的异常

#### 8.exception
定义了SystemException和BussinessException俩种异常，封装自定义编码的code类，和一个全局异常捕获

#### 9.filter
自定义一个JwtAuthenticationTOkenFilter拦截器用于jwt的登录验证

### resouces
就是yml文件，还有menu与user对应的mapper.xml

## 项目功能
就是接口文档中所示的那些接口，这里不做赘述

## 项目存有问题
1. 异步处理尚未学习，所以视频图片等上传效率低
2. 锁的知识涉猎比较少，目前就只知道乐观，悲观锁，所以这次项目没有使用锁，估计多线程也会出现问题
3. 表的性能优化方面，确实也涉猎较少，所以呈现出来的表也都比较基础
4. 限流所需的技术也并未学习到,所以并未很好完成
5. 对于定时更新数据，应该有更好的方式，但是目前只会这种使用注解的最简单方式

## bonus方面
1. 完成了阿里云的云存储
2. 完成了权限功能，实现封禁和解封功能
## 项目启动
项目已经部署在docker上，只需要访问 http://47.113.188.125:10001 加上对应的接口路径，就可以访问了



