

create table jungle_configitems
(
  id       NUMERIC(40,0) primary key,
  appname  VARCHAR(64),
  name     VARCHAR(64),
   unique(appname,name),
  description VARCHAR(255),
  typecode  INTEGER not null,
  max_len   INTEGER not null,
  regexpr   VARCHAR(32),
  editable  CHARACTER(1) not null,
  value     VARCHAR(255)
);

