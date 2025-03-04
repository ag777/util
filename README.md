# Java工具库

这是一个基于jdk21的个人用Java工具库(从jdk8的库修改而来，残留大量旧的语法)。

## 功能特性

- JSON处理工具 (基于Gson)
- 数据库操作辅助工具
- HTTP请求工具 (基于OkHttp)
- 日期时间处理工具 (基于Joda-Time)

## 系统要求

- Java 21 或更高版本
- Maven 3.x

## 安装使用

由于本项目是私有工具库，需要通过以下步骤使用：

1. 克隆项目到本地
```bash
git clone [项目地址]
```

2. 在项目根目录执行安装命令
```bash
mvn clean install
```

3. 在其他项目中引入依赖
```xml
<dependency>
    <groupId>github.ag777</groupId>
    <artifactId>util</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>[本工具库jar包的绝对路径]</systemPath>
</dependency>
```

注意：需要将`[本工具库jar包的绝对路径]`替换为实际的jar包路径。
或者导入本地maven仓库使用

## 主要工具类

### 数据库相关
- `DbHelper`: 数据库操作的辅助工具类，简化数据库操作流程
- `BaseDbConnectionUtils`: 数据库连接工具类
- `DBUpdateHelper`: 数据库更新辅助工具

### JSON处理
- `GsonUtils`: JSON序列化和反序列化工具类
- `JsonObjectUtils`: JsonObject操作工具类

### 文件操作
- `FileUtils`: 文件操作工具类

### HTTP请求
- `HttpUtils`: HTTP请求基础工具类
- `HttpHelper`: HTTP请求辅助工具类
- `HttpApiUtils`: HTTP API调用工具类

### 基础工具类
- `DateUtils`: 日期时间处理工具类
- `StringUtils`: 字符串处理工具类
- `ObjectUtils`: 对象操作工具类
- `RandomUtils`: 随机数生成工具类
- `RegexUtils`: 正则表达式工具类
- `VersionUtils`: 版本号处理工具类
- `IOUtils`: IO操作工具类

### 集合工具类
- `ArrayUtils`: 数组操作工具类
- `ListUtils`: List集合操作工具类
- `MapUtils`: Map集合操作工具类
- `SetUtils`: Set集合操作工具类
- `CollectionAndMapUtils`: 集合与Map通用操作工具类

### 其他工具类
- `ExceptionUtils`: 异常处理工具类
- `ReflectionUtils`: 反射操作工具类


