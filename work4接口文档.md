# Work4 接口文档

## 注：提供了直接可以使用的接口文档
[](https://apifox.com/apidoc/shared-0d2df84b-f7ac-4398-bdcf-78027d357195)
## 1.用户相关文档

### 1.1 登录

#### 1.1.1 基本信息

> 请求路径：/user/login
>
> 请求方式：Post
>
> 接口描述：该接口用于用户登录



#### 1.1.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| username | 登录账号 | string | 是       | 5-16位非空字符 |
| password  | 密码     |  string      | 是 | 5-16位非空字符 |

请求参数样例：

```shell
username=zhangsan&password=123456
```



#### 1.1.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常 |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
	{
    "code": 200,
    "msg": "登录成功",
    "data": {
        "user": {
            "id": 1763762848326180866,
            "user_name": "wutiaowu",
            "nick_name": "五条悟",
            "avatar_url": "http://localhost:10001/image/d08a2bdf-2df4-4386-af27-3951ca450356.jpg",
            "created_at": "2024-03-02 11:06:47",
            "updated_at": "2024-03-04 20:09:54",
            "deleted_at": null
        },
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3N2IxNmViOWZlNjQ0MDJkOWZlYTM4YjBhMDAwYTU3NiIsInN1YiI6IjE3NjM3NjI4NDgzMjYxODA4NjYiLCJpc3MiOiJzZyIsImlhdCI6MTcwOTY1MDMwMywiZXhwIjoxNzA5NjUzOTAzfQ.XjumiLN25p_SGs8Fm29tAd4S2NoSacDGiOGhKsH7rQw"
    }
}
}
// 失败
{
    "code": -1,
    "msg": "账号或者密码错误,请重试！"
}
```

### 1.2 注销

#### 1.2.1 基本信息

> 请求路径：/user/logout
>
> 请求方式：Post
>
> 接口描述：该接口用于用户注销



#### 1.1.2 请求参数

请求参数格式：form-data

请求参数说明：

无

#### 1.1.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常 |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 200,
    "msg": "注销成功"
}
// 失败
{
    "code": -1,
    "msg": "账号或密码错误，请重新登录"
}
```
### 1.3 注册

#### 1.3.1 基本信息

> 请求路径：/user/register
>
> 请求方式：Post
>
> 接口描述：该接口用于注册新用户



#### 1.1.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| username | 登录账号 | string | 是       | 5-16位非空字符 |
| password  | 密码     |  string      | 是 | 5-16位非空字符 |

请求参数样例：

```shell
username=zhangsan&password=123456
```



#### 1.1.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常 |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 200,
    "msg": "用户注册成功"
}

// 失败
{
    "code": -4,
    "msg": "用户已存在，请重新输入用户名"
}
```
### 1.4 用户信息

#### 1.4.1 基本信息

> 请求路径：/user/info
>
> 请求方式：Get
>
> 接口描述：该接口用于查询用户信息



#### 1.4.2 请求参数

请求参数格式：Query Params

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| user_id | 用户id | string | 是       | 5-16位非空字符 |


请求参数样例：

```shell
user_id=1765205688616964097
```



#### 1.4.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success",
    "data": {
        "id": 1765205688616964097,
        "user_name": "zhenhuan",
        "nick_name": "NULL",
        "avatar_url": null,
        "created_at": "2024-03-06 10:40:07",
        "updated_at": null,
        "deleted_at": null
    }
}
// 失败
{
    "code": -4,
    "msg": "用户已存在，请重新输入用户名"
}
```
### 1.5 上传头像

#### 1.5.1 基本信息

> 请求路径：/user/avatar/upload
>
> 请求方式：Put
>
> 接口描述：该接口用于用户上传头像



#### 1.5.2 请求参数

请求参数格式：from-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| file | 文件 | File | 是       | jpg，png，jepg皆可 |


请求参数样例：

```
file=20201208112014_cdanm.jpg
```



#### 1.5.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success",
    "data": {
        "id": 1763762848326180866,
        "user_name": "wutiaowu",
        "nick_name": "五条悟",
        "avatar_url": "http://localhost:10001/image/fa6fd684-d61a-45f5-a225-8930528176e3.jpg",
        "created_at": "2024-03-02 11:06:47",
        "updated_at": "2024-03-06 11:03:50",
        "deleted_at": null
    }
}
// 失败
{
    {
    "code": -4,
    "msg": "文件类型错误，请上传符合类型的文件"
    }
}

```
### 1.6 修改昵称

#### 1.6.1 基本信息

> 请求路径：/user/nickname
>
> 请求方式：Put
>
> 接口描述：该接口用于用户修改昵称

#### 1.6.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| nickname | 用户昵称 | string | 是       | 5-16位非空字符 |


请求参数样例：

```shell
nikename=五条悟
```



#### 1.6.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success",
    "data": {
        "id": 1763762848326180866,
        "user_name": "wutiaowu",
        "nick_name": "五条悟",
        "avatar_url": "http://localhost:10001/image/fa6fd684-d61a-45f5-a225-8930528176e3.jpg",
        "created_at": "2024-03-02 11:06:47",
        "updated_at": "2024-03-06 11:08:06",
        "deleted_at": null
    }
}
// 失败
{
    "code": -4,
    "msg": "用户已存在，请重新输入用户名"
}

```
### 1.7 封禁用户

#### 1.7.1 基本信息

> 请求路径：/user/block
>
> 请求方式：Post
>
> 接口描述：该接口用于用户封禁

#### 1.7.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| user_id | 用户id | string | 是       | 5-16位非空字符 |


请求参数样例：

```shell
user_id = 1765372028988334082
```



#### 1.7.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success"
    
}
// 失败
{
    "code": -3,
    "msg": "系统繁忙，请稍后再试"
}

```
### 1.8 解封昵称

#### 1.8.1 基本信息

> 请求路径：/user/unblock
>
> 请求方式：Post
>
> 接口描述：该接口用于解封用户

#### 1.8.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| user_id | 用户id | string | 是       | 5-16位非空字符 |


请求参数样例：

```shell
user_id = 1765372028988334082
```



#### 1.8.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success",
}
// 失败
{
    "code": -4,
    "msg": "该用户未被封禁"
}

```
## 2.视频相关文档
### 2.1 上传视频

#### 2.1.1 基本信息

> 请求路径：/video/publish
>
> 请求方式：Post
>
> 接口描述：该接口用于上传视频



#### 2.1.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| data | 视频内容 | file | 是       | 任意视频格式 |
| title | 视频标题 | string | 是       | 128个字数内 |
| description | 视频简介 | string | 是       | 128字数内 |
请求参数样例：

```shell
data=2023-09-15 23-49-40.mkv
title=五条悟变成俩段
description=no
```

#### 2.1.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 200,
    "msg": "视频上传成功"
}
// 失败
{
    "code": -4,
    "msg": "用户已存在，请重新输入用户名"
}
```
### 2.2 点击视频
#### 2.2.1 基本信息

> 请求路径：/video/click
>
> 请求方式：Post
>
> 接口描述：该接口用于点击视频



#### 2.2.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| video_id | 视频id | Long | 是       | 1到12个非空数字 |


```shell
video_id=22
```

#### 2.2.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success"
}
// 失败
{
    "code": -3,
    "msg": "系统繁忙，请联系管理员！"
}
```
### 2.3 热度排名

#### 2.3.1 基本信息

> 请求路径：/video/popular
>
> 请求方式：Get
>
> 接口描述：该接口用于获取热度排名



#### 2.3.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| page_size | 分页大小 | Integer | 否       | 20以内数字 |
| page_num | 分页开始页 | Integer | 否       | 1000以内数字 |


```shell
page_num=0&page_size=5
```

#### 2.3.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success",
    "data": {
        "items": [
            {
                "id": 22,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/b9d71118-feb9-4bb7-99ce-b9c0a3d2dd6f.mkv",
                "cover_url": null,
                "title": "五条悟变成俩段",
                "description": "no",
                "visit_count": 1,
                "like_count": 6,
                "comment_count": 6,
                "created_at": "2024-03-05 19:25:47",
                "updated_at": "2024-03-06 13:40:00",
                "deleted_at": null
            },
            {
                "id": 21,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/4937354d-c89a-4b8b-821a-c456b7b8bf6d.mkv",
                "cover_url": null,
                "title": "五条悟暴打宿傩",
                "description": "都可以看",
                "visit_count": 1,
                "like_count": 3,
                "comment_count": 3,
                "created_at": "2024-03-05 19:25:36",
                "updated_at": "2024-03-06 13:40:00",
                "deleted_at": null
            },
            {
                "id": 20,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/b33664ab-5fe9-44b4-856f-1a6adf7512e8.mkv",
                "cover_url": null,
                "title": "五条悟被宿傩暴打",
                "description": "都别看",
                "visit_count": 1,
                "like_count": 1,
                "comment_count": 1,
                "created_at": "2024-03-05 19:25:17",
                "updated_at": "2024-03-06 13:40:00",
                "deleted_at": null
            },
            {
                "id": 23,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/4d40d1e2-7129-41e1-93cd-13b634b04a28.mkv",
                "cover_url": null,
                "title": "五条悟变成俩段",
                "description": "no",
                "visit_count": 0,
                "like_count": 0,
                "comment_count": 0,
                "created_at": "2024-03-06 12:40:51",
                "updated_at": "2024-03-06 13:40:00",
                "deleted_at": null
            }
        ]
    }
}
// 失败
{
    "code": -3,
    "msg": "系统繁忙，请联系管理员！"
}
```
### 2.4 发布列表

#### 2.4.1 基本信息

> 请求路径：/video/list
>
> 请求方式：Get
>
> 接口描述：该接口用于查询用户发布列表

#### 2.4.2 请求参数

请求参数格式：Query Params

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| user_id | 用户id | String | 是       | 5到12个非空数字 |
| page_size | 分页大小 | Integer | 否       | 20以内数字 |
| page_num | 分页开始页 | Integer | 否       | 1000以内数字 |

```shell
user_id=1763762848326180866&page_num=0&page_size=5
```

#### 2.4.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success",
    "data": {
        "total": 4,
        "items": [
            {
                "id": 20,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/b33664ab-5fe9-44b4-856f-1a6adf7512e8.mkv",
                "cover_url": null,
                "title": "五条悟被宿傩暴打",
                "description": "都别看",
                "visit_count": 1,
                "like_count": 1,
                "comment_count": 1,
                "created_at": "2024-03-05 19:25:17",
                "updated_at": "2024-03-06 15:20:01",
                "deleted_at": null
            },
            {
                "id": 21,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/4937354d-c89a-4b8b-821a-c456b7b8bf6d.mkv",
                "cover_url": null,
                "title": "五条悟暴打宿傩",
                "description": "都可以看",
                "visit_count": 1,
                "like_count": 3,
                "comment_count": 3,
                "created_at": "2024-03-05 19:25:36",
                "updated_at": "2024-03-06 15:20:01",
                "deleted_at": null
            },
            {
                "id": 22,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/b9d71118-feb9-4bb7-99ce-b9c0a3d2dd6f.mkv",
                "cover_url": null,
                "title": "五条悟变成俩段",
                "description": "no",
                "visit_count": 1,
                "like_count": 6,
                "comment_count": 6,
                "created_at": "2024-03-05 19:25:47",
                "updated_at": "2024-03-06 15:20:01",
                "deleted_at": null
            },
            {
                "id": 23,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/4d40d1e2-7129-41e1-93cd-13b634b04a28.mkv",
                "cover_url": null,
                "title": "五条悟变成俩段",
                "description": "no",
                "visit_count": 0,
                "like_count": 0,
                "comment_count": 0,
                "created_at": "2024-03-06 12:40:51",
                "updated_at": "2024-03-06 15:20:01",
                "deleted_at": null
            }
        ]
    }
}
// 失败
{
    "code": -3,
    "msg": "系统繁忙，请联系管理员！"
}
```
### 2.5 搜索视频

#### 2.5.1 基本信息

> 请求路径：/video/search
>
> 请求方式：Post
>
> 接口描述：该接口用于搜索视频



#### 2.5.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| keywords | 关键字 | String | 否      | 二十个字数内 |
| page_size | 分页大小 | Integer | 否       | 20以内数字 |
| page_num | 分页开始页 | Integer | 否       | 1000以内数字 |
| username | 用户姓名 | String | 否       | 二十个字数内 |
| from_data | 开始日期 | String | 否       | 11位timestamp |
| to_date| 截止日期 | String | 否       | 11位timestamp |

请求参数样例：

```shell
keywords=五&page_num=0&page_size=5&username=wutiaowu&from_date=1009361505023&to_date=1809361505023
```

#### 2.5.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success",
    "data": {
        "total": 4,
        "items": [
            {
                "id": 20,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/b33664ab-5fe9-44b4-856f-1a6adf7512e8.mkv",
                "cover_url": null,
                "title": "五条悟被宿傩暴打",
                "description": "都别看",
                "visit_count": 1,
                "like_count": 1,
                "comment_count": 1,
                "created_at": "2024-03-05 19:25:17",
                "updated_at": "2024-03-06 15:30:00",
                "deleted_at": null
            },
            {
                "id": 21,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/4937354d-c89a-4b8b-821a-c456b7b8bf6d.mkv",
                "cover_url": null,
                "title": "五条悟暴打宿傩",
                "description": "都可以看",
                "visit_count": 1,
                "like_count": 3,
                "comment_count": 3,
                "created_at": "2024-03-05 19:25:36",
                "updated_at": "2024-03-06 15:30:00",
                "deleted_at": null
            },
            {
                "id": 22,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/b9d71118-feb9-4bb7-99ce-b9c0a3d2dd6f.mkv",
                "cover_url": null,
                "title": "五条悟变成俩段",
                "description": "no",
                "visit_count": 1,
                "like_count": 6,
                "comment_count": 6,
                "created_at": "2024-03-05 19:25:47",
                "updated_at": "2024-03-06 15:30:00",
                "deleted_at": null
            },
            {
                "id": 23,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/4d40d1e2-7129-41e1-93cd-13b634b04a28.mkv",
                "cover_url": null,
                "title": "五条悟变成俩段",
                "description": "no",
                "visit_count": 0,
                "like_count": 0,
                "comment_count": 0,
                "created_at": "2024-03-06 12:40:51",
                "updated_at": "2024-03-06 15:30:00",
                "deleted_at": null
            }
        ]
    }
}
// 失败
{
    "code": -3,
    "msg": "系统繁忙，请联系管理员！"
}
```
## 3.互动相关文档
### 3.1 评论

#### 3.1.1 基本信息

> 请求路径：/comment/publish
>
> 请求方式：Post
>
> 接口描述：该接口用于评论操作



#### 3.1.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| video_id | 视频id | String | 否      | 零到十二个数字 |
| comment_id | 评论id | String | 否       | 零到十二个数字 |
| content| 内容 | String | 是      | 128个字以内 |


请求参数样例：

```shell
video_id=22&content=笑死
```

#### 3.1.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success"
}
// 失败
{
    "code": -3,
    "msg": "系统繁忙，请联系管理员！"
}
```
### 3.2 评论列表

#### 3.2.1 基本信息

> 请求路径：/comment/list
>
> 请求方式：Get
>
> 接口描述：该接口用于获取评论列表



#### 3.2.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| video_id | 视频id | String | 否      | 零到十二个数字 |
| comment_id | 评论id | String | 否       | 零到十二个数字 |
| page_size | 分页大小 | Integer | 否       | 20以内数字 |
| page_num | 分页开始页 | Integer | 否       | 1000以内数字 |



请求参数样例：

```shell
video_id=22&page_size=5&page_num=0
```

#### 3.2.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success",
    "data": {
        "items": [
            {
                "comment_id": 38,
                "user_id": 1763762848326180866,
                "video_id": 20,
                "parent_id": 0,
                "content": "我还是觉得宿傩帅一点",
                "like_count": null,
                "child_count": null,
                "created_at": "2024-03-05 19:26:58",
                "updated_at": null,
                "deleted_at": null
            }
        ]
    }
}
// 失败
{
    "code": -3,
    "msg": "系统繁忙，请联系管理员！"
}
```
### 3.3 删除评论

#### 3.3.1 基本信息

> 请求路径：/comment/delete
>
> 请求方式：Delete
>
> 接口描述：该接口用于删除评论



#### 3.3.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| video_id | 视频id | String | 否      | 零到十二个数字 |
| comment_id | 评论id | String | 否       | 零到十二个数字 |


请求参数样例：

```shell
video_id=22
```

#### 3.3.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success"
}
// 失败
{
    "code": -3,
    "msg": "系统繁忙，请联系管理员！"
}
```
### 3.4 点赞

#### 3.4.1 基本信息

> 请求路径：/like/action
>
> 请求方式：Post
>
> 接口描述：该接口用于点赞操作



#### 3.4.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| video_id | 视频id | String | 否      | 零到十二个数字 |
| comment_id | 评论id | String | 否       | 零到十二个数字 |
| action_type | 类型，点赞或者取消点赞 | Integer | 是      | 1为点赞2为取消 |


请求参数样例：

```shell
video_id=22&action_type=1
```

#### 3.4.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success"
}
// 失败
{   
    "code": -4,
    "msg": "点赞操作非法,请检查"
}
```
### 3.5 点赞列表

#### 3.5.1 基本信息

> 请求路径：/like/list
>
> 请求方式：Get
>
> 接口描述：该接口用于搜索视频



#### 3.5.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| user_id | 用户id | String | 否      | 零到十二个数字 |
| page_size | 分页大小 | Integer | 否       | 20以内数字 |
| page_num | 分页开始页 | Integer | 否       | 1000以内数字 |


请求参数样例：

```shell
user_id=1763234093514002434&page_num=0&page_size=5&
```

#### 3.5.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success",
    "data": {
        "items": [
            {
                "id": 20,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/b33664ab-5fe9-44b4-856f-1a6adf7512e8.mkv",
                "cover_url": null,
                "title": "五条悟被宿傩暴打",
                "description": "都别看",
                "visit_count": 1,
                "like_count": 1,
                "comment_count": 1,
                "created_at": "2024-03-05 19:25:17",
                "updated_at": "2024-03-06 19:50:00",
                "deleted_at": null
            },
            {
                "id": 21,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/4937354d-c89a-4b8b-821a-c456b7b8bf6d.mkv",
                "cover_url": null,
                "title": "五条悟暴打宿傩",
                "description": "都可以看",
                "visit_count": 1,
                "like_count": 3,
                "comment_count": 3,
                "created_at": "2024-03-05 19:25:36",
                "updated_at": "2024-03-06 19:50:00",
                "deleted_at": null
            },
            {
                "id": 22,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/b9d71118-feb9-4bb7-99ce-b9c0a3d2dd6f.mkv",
                "cover_url": null,
                "title": "五条悟变成俩段",
                "description": "no",
                "visit_count": 1,
                "like_count": 6,
                "comment_count": 6,
                "created_at": "2024-03-05 19:25:47",
                "updated_at": "2024-03-06 19:50:00",
                "deleted_at": null
            },
            {
                "id": 22,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/b9d71118-feb9-4bb7-99ce-b9c0a3d2dd6f.mkv",
                "cover_url": null,
                "title": "五条悟变成俩段",
                "description": "no",
                "visit_count": 1,
                "like_count": 6,
                "comment_count": 6,
                "created_at": "2024-03-05 19:25:47",
                "updated_at": "2024-03-06 19:50:00",
                "deleted_at": null
            },
            {
                "id": 22,
                "user_id": 1763762848326180866,
                "video_url": "http://localhost:10001/video/b9d71118-feb9-4bb7-99ce-b9c0a3d2dd6f.mkv",
                "cover_url": null,
                "title": "五条悟变成俩段",
                "description": "no",
                "visit_count": 1,
                "like_count": 6,
                "comment_count": 6,
                "created_at": "2024-03-05 19:25:47",
                "updated_at": "2024-03-06 19:50:00",
                "deleted_at": null
            }
        ]
    }
}
// 失败
{
    "code": -4,
    "msg": "用户不存在"
}
```
## 4.社交相关文档
### 4.1 关注

#### 4.1.1 基本信息

> 请求路径：/relation/action
>
> 请求方式：Post
>
> 接口描述：该接口用于关注操作



#### 4.1.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| to_user_id| 光注对象id | String | 是      | 零到十二个数字 |
| action_type | 操作类型,1代表点赞，0代表取消 | Integer | 是       | 
0或者1 |


请求参数样例：

```shell
to_user_id=1763762848326180866&action_type=0
```

#### 4.1.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success"
}
// 失败
{
    "code": -3,
    "msg": "系统繁忙，请联系管理员！"
}
```
### 4.2 粉丝列表

#### 4.2.1 基本信息

> 请求路径：/follower/list
>
> 请求方式：Get
>
> 接口描述：该接口用于获取粉丝列表



#### 4.2.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| user_id| 查找对象id | String | 是      | 零到十二个数字 |
| page_size | 分页大小 | Integer | 否       | 20以内数字 |
| page_num | 分页开始页 | Integer | 否       | 1000以内数字 |



请求参数样例：

```shell
user_id=1763762848326180866&page_num=0&page_size=5
```

#### 4.2.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success",
    "data": {
        "total": 1,
        "items": [
            {
                "id": 1763762848326180866,
                "user_name": "wutiaowu",
                "avatar_url": "http://localhost:10001/image/fa6fd684-d61a-45f5-a225-8930528176e3.jpg"
            }
        ]
    }
}
// 失败
{
    "code": -4,
    "msg": "未找到该用户"
}
```
### 4.3 关注列表

#### 4.3.1 基本信息

> 请求路径：/following/list
>
> 请求方式：Get
>
> 接口描述：该接口用于搜索视频



#### 4.3.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| user_id| 查询对象id | String | 是      | 零到十二个数字 |
| page_size | 分页大小 | Integer | 否       | 20以内数字 |
| page_num | 分页开始页 | Integer | 否       | 1000以内数字 |



请求参数样例：

```shell
user_id=1763762848326180866&page_num=0&page_size=5
```

#### 4.3.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
// 成功
{
    "code": 10000,
    "msg": "success",
    "data": {
        "total": 4,
        "items": [
            {
                "id": 1763234093514002434,
                "user_name": "yangmi",
                "avatar_url": "http://localhost:10001/image/1e2ce2e4-ac0a-48b7-95c2-42c906718935.png"
            },
            {
                "id": 1763750692448800770,
                "user_name": "silang",
                "avatar_url": "http://localhost:10001/image/4074ef16-98a0-413f-aef2-d1b387c0562c.jpg"
            },
            {
                "id": 1763762848326180866,
                "user_name": "wutiaowu",
                "avatar_url": "http://localhost:10001/image/fa6fd684-d61a-45f5-a225-8930528176e3.jpg"
            },
            {
                "id": 1765205688616964097,
                "user_name": "zhenhuan",
                "avatar_url": null
            }
        ]
    }
}
// 失败
{
    "code": -4,
    "msg": "未找到该用户"
}
```
### 4.4 关注

#### 4.4.1 基本信息

> 请求路径：/friends/list
>
> 请求方式：Get
>
> 接口描述：该接口用于获得好友列表



#### 4.4.2 请求参数

请求参数格式：form-data

请求参数说明：

| 参数名称  | 说明     | 类型   | 是否必要 | 备注           |
| --------- | -------- | ------ | -------- | -------------- |
| page_size | 分页大小 | Integer | 否       | 20以内数字 |
| page_num | 分页开始页 | Integer | 否       | 1000以内数字 |


请求参数样例：

```shell
page_num=0&page_size=5
```

#### 4.4.3 响应数据

响应数据类型：application/json

响应参数说明：

| 名称    | 类型   | 是否必须 | 默认值 | 备注                   | 其他信息 |
| ------- | ------ | -------- | ------ | ---------------------- | -------- |
| code    | number | 必须     |        | 响应码，10000-成功，-1-登录信息异常 -2-权限异常 -3-未知异常 -4 业务异常  |          |
| message | string | 非必须   |        | 提示信息               |          |
| data    | object | 非必须   |        | 返回的数据             |          |

响应数据样例：

```json
{
    "code": 10000,
    "msg": "success",
    "data": {
        "total": 3,
        "items": [
            {
                "id": 1763234093514002434,
                "user_name": "yangmi",
                "avatar_url": "http://localhost:10001/image/1e2ce2e4-ac0a-48b7-95c2-42c906718935.png"
            },
            {
                "id": 1763750692448800770,
                "user_name": "silang",
                "avatar_url": "http://localhost:10001/image/4074ef16-98a0-413f-aef2-d1b387c0562c.jpg"
            },
            {
                "id": 1763762848326180866,
                "user_name": "wutiaowu",
                "avatar_url": "http://localhost:10001/image/fa6fd684-d61a-45f5-a225-8930528176e3.jpg"
            }
        ]
    }
}
// 失败
{
    "code": -3,
    "msg": "系统繁忙，请联系管理员！"
}
```
