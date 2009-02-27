
drop table jungle_configitems if exists;

create table jungle_configitems
(
  id       NUMERIC(40,0) primary key,
  appname  VARCHAR(64),
  name     VARCHAR(64),
  description VARCHAR(255),
  typecode  INTEGER not null,
  max_len   INTEGER not null,
  regexpr   VARCHAR(32),
  editable  CHARACTER(1) not null,
  value     VARCHAR(255),
   constraint appname_name_un unique(appname,name)
);

