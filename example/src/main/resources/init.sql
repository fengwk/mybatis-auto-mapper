drop table if exists example;
create table example (
    id bigint unsigned not null auto_increment,
    name varchar(32) default '默认名称',
    sort int,
    primary key(id)
) charset=utf8mb4;
