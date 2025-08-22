## 项目介绍

- 项目名称：athena-security-iam
- 项目描述：IAM服务

## 项目结构

```lua
─ athena-security-iam
├─db -- 数据库脚本
├─athena-security-iam-boot -- 启动模块
│  └─src
│     └─main
│        ├─java
│        │  └─com.gls.athena.security.iam.boot
│        │     ├─config -- 配置
│        │     └─web
│        │        ├─controller -- 控制器
│        │        ├─converter -- 转换器
│        │        ├─entity -- 实体
│        │        ├─mapper -- mapper
│        │        └─service -- 服务
│        └─resources
│           └─mapper -- mapper文件
└─athena-security-iam-sdk -- sdk模块
   └─src
      └─main
         ├─java
         │  └─com.gls.athena.security.iam.sdk
         │     ├─feign -- feign
         │     └─vo -- vo
         └─resources
            └─META-INF
               └─spring -- spring配置文件
```