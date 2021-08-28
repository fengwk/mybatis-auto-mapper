# AutoMapper

AutoMapper是一款Mybatis SQL生成框架提供了类似JPA的接口方法转SQL能力，仅需依赖一个编译期Jar包就能避免大量简单且类似SQL的重复编写工作。

Mybatis在互联网企业被广泛使用，但使用过程中开发者却不得不机械地编写一些简单的模板SQL，例如`insert into table (field1, field2, field3...) values (#{var1}, #{var2}, #{var3}...)`或者`select field1 as var1, field2 as var2, field3 as var3 from table where ...`，这些SQL简单却使用频繁，因此不得不在每一个实体（表）的xml文件中进行类似的工作，如果实体中具有众多字段那这样的重复劳动就显得格外枯燥乏味。AutoMapper就是专门为解决这种重复工作而设计的，它能应对80%的SQL编写工作（tips：出于性能考虑互联网企业的大部分SQL都只会对单表进行简单操作）。

# 开始使用

首先需要在项目的编译阶段依赖AutoMapper，如果您正在使用maven管理项目，那么添加如下依赖即可：

```xml
<dependency>
    <groupId>fun.fengwk.auto-mapper</groupId>
    <artifactId>auto-mapper-processor</artifactId>
    <scope>provided</scope>
    <version>0.0.9</version>
</dependency>
```

常见的Mybatis使用方式是编写一个`*Mapper.java`文件在其中定义接口方法，然后编写一个`*Mapper.xml`文件实现接口方法的SQL语句。例如example模块中（tips：example模块中展示了如何在spring-boot项目里使用AutoMapper）就有`ExampleMapper.java`文件和`ExampleMapper.xml`文件，但只要在`ExampleMapper`类上标记`@AutoMapper`注解，AutoMapper就可以在编译期间帮助您生成相应符合Mybatis规范的Mybatis SQL片段语句到相同包路径下的xml文件中。

```java
@AutoMapper
public interface ExampleMapper {
    
    ExampleDO findById(long id);
    
}
```

例如定义了如上的`ExampleMapper`类，那么框架会自动生成如下的Mybatis SQL片段并插入与`ExampleMapper`类相同包路径下的名称为`ExampleMapper.xml`文件中，如果不存在`ExampleMapper.xml`文件那么AutoMapper将会自动生成一个。

```xml
<!--auto mapper generate-->
<select id="findById" parameterType="long" resultType="fun.fengwk.automapper.example.model.ExampleDO">
    select id, name, sort
    from example
    where id=#{id}
</select>
```

为了实现SQL生成，必须遵循一定规则来定义Mapper接口的方法，如果您曾使用过JPA，那么这些规则会非常容易理解，因为这些规则几乎完全遵循了JPA的约定。

下标站时刻展示了当前支持的所有模式（如果您了解状态机可以直接查看`doc/MapperMethodStateMachine.drawio`）：

| 描述 | 模式                | 约束                                                         |
| ---- | ------------------- | ------------------------------------------------------------ |
| 新增 | insert              | 单个JavaBean入参                                             |
|      | insertAll           | 可迭代的JavaBean入参                                         |
| 删除 | deleteAll           | 无入参                                                       |
|      | deleteBy...         | 多入参数时必须使用@Param注解绑定By后参数与入参关系           |
| 修改 | updateBy...         | 多入参数时必须使用@Param注解绑定By后参数与入参关系           |
| 查询 | findAll             | 无入参                                                       |
|      | findAllOrderBy...   | 无入参                                                       |
|      | findBy...           | 多入参数时必须使用@Param注解绑定By后参数与入参关系           |
|      | findBy...OrderBy... | 多入参数时必须使用@Param注解绑定By后参数与入参关系           |
| 计数 | countAll            | 无入参                                                       |
|      | countBy...          | 多入参数时必须使用@Param注解绑定By后参数与入参关系           |
| 分页 | pageAll             | 入参必须拥有limit，可选offset                            |
|      | pageAllOrderBy...   | 入参必须拥有limit，可选offset，必须使用@Param注解绑定By后参数与入参关系 |
|      | pageBy...           | 入参必须拥有limit，可选offset，必须使用@Param注解绑定By后参数与入参关系 |
|      | pageBy...OrderBy... | 入参必须拥有limit，可选offset，必须使用@Param注解绑定By后参数与入参关系 |

下表格展示了当前支持的所有关键字：

| 关键字             | 方法名                                 | SQL                                          |
| ------------------ | -------------------------------------- | -------------------------------------------- |
| And                | findByLastnameAndFirstname             | ... where x.lastname = ? and x.firstname = ? |
| Or                 | findByLastnameOrFirstname              | ... where x.lastname = ? or x.firstname = ?  |
| Is，Equals         | findById，findByIdIs，findByIdEquals   | ... where x.id = ?                           |
| LessThan           | findByAgeLessThan                      | ... where x.age < ?                          |
| LessThanEquals     | findByAgeLessThanEquals                | ... where x.age <= ?                         |
| GreaterThan        | findByAgeGreaterThan                   | ... where x.age > ?                          |
| GreaterThanEquals  | findByAgeGreaterThanEquals             | ... where x.age >= ?                         |
| After              | findByIdAfter                          | ... where x.id > ?                           |
| Before             | findByIdBefore                         | ... where x.id < ?                           |
| IsNull             | findByNameIsNull                       | ... where x.name is null                     |
| IsNotNull，NotNull | findByNameIsNotNull，findByNameNotNull | ... where x.name is not null                 |
| Like               | findByNameLike                         | ... where x.name like ?                      |
| NotLike            | findByNameNotLike                      | ... where x.name not like ?                  |
| StartingWith       | findByNameStartingWith                 | ... where x.name not like '?%'               |
| EndingWith         | findByNameEndingWith                   | ... where x.name not like '%?'               |
| Containing         | findByNameContaining                   | ... where x.name not like '%?%'              |
| OrderBy            | findByIdOrderByIdDesc                  | ... where x.id = 1 order by x.id desc        |
| Not                | findByNameNot                          | ... where x.name != ?                        |
| In                 | findByIdIn(Collection)                 | ... where x.id in (...)                      |
| NotIn              | findByIdNotIn(Collection)              | ... where x.id not in (...)                  |

注意：由于要防止SQL注入问题，当前的StartingWith、EndingWith、Containing关键字均使用的Mysql函数concat，这也意味着这3个关键字仅支持Mysql数据源。

支持自定义字段名：可以在入参或字段上添加`@FieldName`注解支持自定义数据库字段名称。

支持useGeneratedKeys：可以在字段上添加`@UseGeneratedKeys`注解支持Mybatis的useGeneratedKeys功能。

全局配置：尽管我们可以在`@AutoMapper`注解中修改当前类的配置，但如果需要使用全局配置，可以在resource根目录下定义`auto-mapper.config`文件作为全局配置，如果使用了全局配置将忽略`@AutoMapper`中的定义：

```properties
fun.fengwk.automapper.annotation.AutoMapper.dbType=MYSQL
fun.fengwk.automapper.annotation.AutoMapper.mapperSuffix=Mapper
fun.fengwk.automapper.annotation.AutoMapper.tableNamingStyle=LOWER_UNDER_SCORE_CASE
fun.fengwk.automapper.annotation.AutoMapper.fieldNamingStyle=LOWER_UNDER_SCORE_CASE
```

编译：由于AutoMapper作用与编译期，要生成SQL片段必须重新编译项目，使用IDEA的Build -> Rebuild project或者执行`mvn clean install`命令均可执行编译。

# 应用示例

下面将会展示一些具体的示例（这些示例均来自于example模块，可以结合具体代码理解）帮助您了解如何使用规则定义接口方法。

## 示例一

方法

```java
int insert(ExampleDO exampleDO);
```

SQL片段

```xml
<!--auto mapper generate-->
<insert id="insert" keyProperty="id" parameterType="fun.fengwk.automapper.example.model.ExampleDO" useGeneratedKeys="true">
    insert into example (name, sort) values
    (#{name}, #{sort})
</insert>
```

说明

其中name和sort是通过读取ExampleDO中字段获取的，并且Java字段名称会通过@AutoMapper中定义中fieldNamingStyle转换为相应的数据库字段格式，如果没有办法通过常规的规则转换，也可以通过@FieldName注解直接指定数据库字段名称，如果使用了自增主键，使用@UseGeneratedKeys注解可以对相应字段开启Mybatis的useGeneratedKeys属性并且在insert语句中忽略该字段的插入。

## 示例二

方法

```java
int deleteById(long id);
```

SQL片段

```xml
<!--auto mapper generate-->
<delete id="deleteById" parameterType="long">
    delete from example
    where id=#{id}
</delete>
```

说明

AutoMapper根据By后条件生成where语句，因为只有一个参数不必添加`@Param`注解来说明参数名称。

## 示例三

方法

```java
int updateById(ExampleDO exampleDO);
```

SQL片段

```xml
<!--auto mapper generate-->
<update id="updateById" parameterType="fun.fengwk.automapper.example.model.ExampleDO">
    update example set id=#{id}, name=#{name}, sort=#{sort}
    where id=#{id}
</update>
```

说明

AutoMapper通过读取入参ExampleDO类的字段生成set语句，并且根据By后条件生成where语句。

## 示例四

方法

```java
ExampleDO findById(long id);
```

SQL片段

```xml
<!--auto mapper generate-->
<select id="findById" parameterType="long" resultType="fun.fengwk.automapper.example.model.ExampleDO">
    select id, name, sort
    from example
    where id=#{id}
</select>
```

说明

AutoMapper通过读取返回值ExampleDO类中的字段生成select语句，并且根据By后条件生成where语句，因为只有一个参数不必添加`@Param`注解来说明参数名称。

## 示例五

方法

```java
List<ExampleDO> findByNameStartingWith(String name);
```

SQL片段

```xml
<!--auto mapper generate-->
<select id="findByNameStartingWith" parameterType="java.lang.String" resultType="fun.fengwk.automapper.example.model.ExampleDO">
    select id, name, sort
    from example
    where name like concat(#{name}, '%')
</select>
```

说明

AutoMapper通过读取返回值泛型ExampleDO类中的字段生成select语句，根据By后条件生成where语句，因为只有一个参数不必添加`@Param`注解来说明参数名称。

## 示例六

方法

```java
List<ExampleDO> findByNameStartingWithAndSortGreaterThanEqualsOrderBySortDesc(@Param("name") String name, @Param("sort") int sort);
```

SQL片段

```xml
<!--auto mapper generate-->
<select id="findByNameStartingWithAndSortGreaterThanEqualsOrderBySortDesc" resultType="fun.fengwk.automapper.example.model.ExampleDO">
    select id, name, sort
    from example
    where name like concat(#{name}, '%')
    and sort&gt;=#{sort}
    order by sort desc
</select>
```

说明

AutoMapper通过读取返回值泛型ExampleDO类中的字段生成select语句，根据By后条件生成where语句，因为只有多个参数必须添加`@Param`注解来说明参数名称。

## 示例七

方法

```java
List<E> pageAll(@Param("offset") int offset, @Param("limit") int limit);
```

SQL片段

```xml
<!--auto mapper generate-->
<select id="pageAll" resultType="fun.fengwk.automapper.example.model.ExampleDO">
    select id, name, sort
    from example
    limit #{offset},#{limit}
</select>
```

说明

AutoMapper解析父类泛型E得到ExampleDO，继而读取ExampleDO字段生成select语句，由于是page方法，必须添加offset和limit参数。

# 原理

AutoMapper基于JSR 269 Annotation Processing API实现，Annotation Processing API是Javac程序的一个SPI扩展点，通过编译期读取原文件信息自动生成相应代码片段，类似原理实现的框架有Lombok、Google auto......
