# AutoMapper

AutoMapper 是一个 **MyBatis 编译期 SQL 生成器**。你只需要在 Mapper 接口上声明方法并加 `@AutoMapper`，框架会在编译期生成或增量更新对应 XML。

- 纯编译期生效，运行时无额外开销。
- 与手写 XML 共存：同 `id` 的语句会跳过，不会覆盖。
- 支持 SQL92、MySQL、PostgreSQL、SQLite 方言（MySQL 支持更多派生语法）。

![idea.gif](./doc/idea.gif)

## 目录

- [1. 模块说明](#1-模块说明)
- [2. 5 分钟快速开始](#2-5-分钟快速开始)
- [3. 方法命名语法](#3-方法命名语法)
- [4. 注解说明](#4-注解说明)
- [5. 全局配置](#5-全局配置)
- [6. 方言支持与差异](#6-方言支持与差异)
- [7. MySQL 扩展](#7-mysql-扩展)
- [8. 与手写 XML 共存](#8-与手写-xml-共存)
- [9. Example 模块运行](#9-example-模块运行)
- [10. 常见问题与排查](#10-常见问题与排查)

## 1. 模块说明

- `auto-mapper-annotation`：对外注解（`@AutoMapper`、`@FieldName` 等）。
- `auto-mapper-processor`：注解处理器核心，编译期解析方法并生成 SQL。
- `auto-mapper-mybatis`：最小化的 MyBatis 注解/类型定义，降低编译期耦合。
- `auto-mapper-example`：Spring Boot + MyBatis 的完整接入示例。

## 2. 5 分钟快速开始

### 2.1 添加依赖

Maven:

```xml
<dependency>
    <groupId>fun.fengwk.auto-mapper</groupId>
    <artifactId>auto-mapper-processor</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

Gradle (Java):

```groovy
dependencies {
    compileOnly "fun.fengwk.auto-mapper:auto-mapper-processor:1.0.0"
    annotationProcessor "fun.fengwk.auto-mapper:auto-mapper-processor:1.0.0"
}
```

### 2.2 定义 DO

```java
public class UserDO {

    @UseGeneratedKeys
    private Long id;
    private String name;
    private Integer age;

    // getter/setter
}
```

### 2.3 定义 Mapper

```java
@AutoMapper
public interface UserMapper {

    int insert(UserDO userDO);

    UserDO findById(long id);

    List<UserDO> findByNameStartingWith(@Param("name") String name);

    int updateByIdSelective(UserDO userDO);
}
```

### 2.4 编译触发生成

```bash
env JAVA_HOME=$JAVA_HOME_8 mvn clean compile
```

### 2.5 选择方言（可选）

如果不配置，`dbType` 默认是 `MYSQL`。

```java
@AutoMapper(dbType = DBType.SQLITE)
public interface UserMapper {
}
```

也可以在 `auto-mapper.config` 里全局设置：

```properties
fun.fengwk.automapper.annotation.AutoMapper.dbType=SQLITE
```

生成产物位于 `target/classes`（`CLASS_OUTPUT`），例如：

`target/classes/your/package/UserMapper.xml`

示例（节选）：

```xml
<!--auto mapper generate-->
<select id="findById" parameterType="long" resultType="your.package.UserDO">
    select id, name, age
    from user
    where id=#{id}
</select>
```

## 3. 方法命名语法

### 3.1 动作词与别名

| 主动作 | 可用前缀 |
| --- | --- |
| 新增 | `insert` / `add` |
| 删除 | `delete` / `remove` |
| 更新 | `update` |
| 查询 | `find` / `get` / `list` / `select` |
| 计数 | `count` |
| 分页 | `page` / `limit` |

### 3.2 方法模式

| 分类 | 方法模式 | 说明 |
| --- | --- | --- |
| 新增 | `insert[All][Selective]` | 单对象或集合对象入参 |
| 删除 | `deleteAll` / `deleteBy...` | `deleteAll` 无入参 |
| 更新 | `updateBy...[Selective]` | 必须有 `By` 条件 |
| 查询 | `findAll[OrderBy...]` / `findBy...[OrderBy...]` | 返回值需为 JavaBean 或其集合 |
| 计数 | `countAll` / `countBy...` | 返回值必须为 `int/long/Integer/Long` |
| 分页 | `pageAll[OrderBy...]` / `pageBy...[OrderBy...]` / `limitAll[OrderBy...]` / `limitBy...[OrderBy...]` | 必须有 `limit`，可选 `offset` |

### 3.3 条件关键字 (`By` 后)

| 关键字 | 示例 | SQL 语义 |
| --- | --- | --- |
| `And` / `Or` | `findByAAndB` | 逻辑连接 |
| `Is` / `Equals` | `findById` / `findByIdIs` | `=` |
| `LessThan` / `LessThanEquals` | `findByAgeLessThan` | `<` / `<=` |
| `GreaterThan` / `GreaterThanEquals` | `findByAgeGreaterThan` | `>` / `>=` |
| `After` / `Before` | `findByIdAfter` | `>` / `<` |
| `IsNull` / `IsNotNull` / `NotNull` | `findByNameIsNull` | `is null` / `is not null` |
| `Like` / `NotLike` | `findByNameLike` | `like` / `not like` |
| `StartingWith` / `EndingWith` / `Containing` | `findByNameStartingWith` | 前缀 / 后缀 / 包含匹配 |
| `Not` | `findByNameNot` | `!=` |
| `In` / `NotIn` | `findByIdIn` | `in (...)` / `not in (...)` |

### 3.4 排序关键字 (`OrderBy` 后)

- `Asc`（默认，可省略）
- `Desc`
- 多字段排序使用 `And` 连接，例如：`findAllOrderBySortDescAndIdAsc`

### 3.5 参数绑定规则（很重要）

- 多参数方法建议显式使用 `@Param`，避免参数名推断导致的映射错误。
- 分页方法中参数名必须能解析出 `limit`，可选 `offset`。
- `@DynamicOrderBy` 最多只允许一个参数。
- `@IncludeField` 与 `@ExcludeField` 不能同时作用于同一个方法。

## 4. 注解说明

| 注解 | 作用位置 | 作用 |
| --- | --- | --- |
| `@AutoMapper` | 接口 | 开启自动映射，可配置 dbType/命名风格/表名策略 |
| `@FieldName` | 字段、参数、getter | 指定数据库字段名 |
| `@UseGeneratedKeys` | 字段、getter | 标记自增主键（同时具备 `@Id` 语义） |
| `@Id` | 字段、getter | 标记主键 |
| `@Selective` | 字段、参数、方法 | 作为可选条件（null 时跳过） |
| `@IncludeField` | 方法 | insert/update 仅包含指定字段 |
| `@ExcludeField` | 字段、方法 | insert/update 排除指定字段 |
| `@TypeHandler` | 字段、getter | 指定 MyBatis TypeHandler；查询时自动生成 resultMap |
| `@DynamicOrderBy` | 参数 | 动态拼接 `order by ${...}` |
| `@UpdateIncrement` | 字段、getter | 更新时生成 `field=field+N` |
| `@OnDuplicateKeyUpdateIgnore` | 字段、getter | `insertOnDuplicateKeyUpdate` 时忽略该字段 |
| `@MethodExpr` | 方法 | 自定义方法表达式，替代方法名参与解析 |

## 5. 全局配置

在 `src/main/resources/auto-mapper.config` 中配置全局默认值：

```properties
fun.fengwk.automapper.annotation.AutoMapper.dbType=MYSQL
fun.fengwk.automapper.annotation.AutoMapper.mapperSuffix=Mapper
fun.fengwk.automapper.annotation.AutoMapper.tableNamingStyle=LOWER_UNDER_SCORE_CASE
fun.fengwk.automapper.annotation.AutoMapper.fieldNamingStyle=LOWER_UNDER_SCORE_CASE
fun.fengwk.automapper.annotation.AutoMapper.tableNamePrefix=
fun.fengwk.automapper.annotation.AutoMapper.tableNameSuffix=
```

优先级：

`注解显式配置 > auto-mapper.config > 注解默认值`

## 6. 方言支持与差异

### 6.1 支持的方言

| dbType | 支持状态 | 说明 |
| --- | --- | --- |
| `MYSQL` | 已支持 | 默认值，支持 MySQL 派生方法语法 |
| `POSTGRESQL` | 已支持 | 针对 PG 覆盖了 like 关键字与 `insertAllSelective` |
| `SQLITE` | 已支持 | 针对 SQLite 覆盖了 like 关键字与 `insertAllSelective` |
| `SQL92` | 已支持 | 通用实现，适配标准 SQL 场景 |

### 6.2 关键差异

| 能力点 | MYSQL | POSTGRESQL | SQLITE | SQL92 |
| --- | --- | --- | --- | --- |
| 标识符引用符 | `` `name` `` | `"name"` | `"name"` | `"name"` |
| `StartingWith/EndingWith/Containing` | `concat(...)` + `#{}` | `||` + `#{}` | `||` + `#{}` | `${}` 模板拼接 |
| 分页语法 | `limit #{limit} offset #{offset}` | `limit #{limit} offset #{offset}` | `limit #{limit} offset #{offset}` | `limit #{limit} offset #{offset}` |
| `insertAllSelective` | `foreach` 多语句（`;` 分隔） | 单条 `insert ... values (...), (...)`，空值用 `default` | 单条 `insert ... values (...), (...)`，空值用 `default` | `foreach` 多语句（`;` 分隔） |
| MySQL 派生前缀（`insertIgnore` / `replace` / `insertOnDuplicateKeyUpdate` / `findLockInShareMode` / `findForUpdate`） | 支持 | 不支持 | 不支持 | 不支持 |

### 6.3 选型建议

- MySQL 业务优先用 `MYSQL`，可使用方言扩展能力。
- PostgreSQL 业务优先用 `POSTGRESQL`。
- SQLite 业务优先用 `SQLITE`。
- 只有在你明确需要“最通用”的输出时，再考虑 `SQL92`。

## 7. MySQL 扩展

当 `dbType=MYSQL` 时可使用以下派生语法：

- `insertIgnore...` -> `insert ignore into`
- `replace...` -> `replace into`
- `insertOnDuplicateKeyUpdate...` -> `insert ... on duplicate key update ...`
- `findLockInShareMode...` -> `select ... lock in share mode`
- `findForUpdate...` -> `select ... for update`

另外：

- MySQL 下 `StartingWith/EndingWith/Containing` 会使用 `concat(...)` 拼接。
- SQL92 下这些关键字使用字符串模板拼接（`${...}`），请谨慎处理入参，避免注入风险。
- 分页语法已统一为 `limit #{limit} offset #{offset}`（有 offset 时）与 `limit #{limit}`（无 offset 时），MySQL、PostgreSQL、SQLite 共用同一套分页生成逻辑。

## 8. 与手写 XML 共存

推荐做法：

1. 在 `resources` 下预先创建 Mapper XML 外壳（包含正确 `namespace`）。
2. 编译时 AutoMapper 会在该 XML 中追加 `<!--auto mapper generate-->` 片段。
3. 如果某个 `id` 已存在手写 SQL，自动生成会跳过该方法。

这允许你：

- 关键 SQL 手写优化。
- 常规 CRUD 自动生成。

## 9. Example 模块运行

### 9.1 初始化数据库

执行 `example/src/main/resources/init.sql`。

### 9.2 修改连接配置

编辑 `example/src/main/resources/application.yml`（默认是本地 MySQL `test` 库）。

### 9.3 启动

```bash
env JAVA_HOME=$JAVA_HOME_8 mvn -pl example -am clean package -DskipTests
env JAVA_HOME=$JAVA_HOME_8 mvn -pl example spring-boot:run
```

### 9.4 体验接口

```bash
curl "http://localhost:8080/insert?name=NewExample&sort=10"
curl "http://localhost:8080/findById?id=1"
curl "http://localhost:8080/pageAll?offset=0&limit=3"
```

## 10. 常见问题与排查

### Q1: 为什么没有生成 XML？

- 检查是否引入 `auto-mapper-processor`。
- 检查编译是否真的执行（IDE 需要开启 annotation processing）。
- 检查 Mapper 是否是接口且带 `@AutoMapper`。

### Q2: 报错 `Can not found name entry ...`？

- 多参数场景请为条件参数加 `@Param("...")`，并确保命名与表达式一致。

### Q3: 分页方法报 `should have limit params`？

- 参数中必须有 `limit`（可再加 `offset`），类型需为 `int/long/Integer/Long`。

### Q4: `insertAllSelective` 为什么自增主键只返回一个？

- 在 `MYSQL/SQL92` 的 `insertAllSelective`（多语句）场景中，通常只能拿到第一条自增键，这是 JDBC `getGeneratedKeys` 的常见限制。

### Q5: SQLite 应该用哪个 dbType？

- 请显式使用 `dbType=SQLITE`，不要依赖 `SQL92` 兜底。

### Q6: PostgreSQL / SQLite 的 `insertAllSelective` 有什么不同？

- 两者都会生成单条 `insert ... values (...), (...)`，并在字段值为空时使用 `default`，比多语句模式更稳定。

### Q7: 动态排序有风险吗？

- `@DynamicOrderBy` 使用 `${}` 直接拼接，请务必在业务层做白名单校验。

---

如果你只想快速落地，建议直接参考 `example` 模块中的这几个文件：

- `example/src/main/java/fun/fengwk/automapper/example/mapper/ExampleMapper.java`
- `example/src/main/java/fun/fengwk/automapper/example/model/ExampleDO.java`
- `example/src/main/resources/fun/fengwk/automapper/example/mapper/ExampleMapper.xml`
