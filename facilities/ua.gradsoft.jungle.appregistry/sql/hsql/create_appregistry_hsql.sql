
drop table jungle_appregistry if exists;

create table jungle_appregistry
(
  NAME        VARCHAR(64)  PRIMARY KEY,
  VERSION     VARCHAR(16),
  DESCRIPTION VARCHAR(255)
);


