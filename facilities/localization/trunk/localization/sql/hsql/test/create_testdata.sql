
create table Country
(
  isoCode CHAR(2) primary key,
  name VARCHAR(20)
);

create table City
(
  ID INTEGER primary key,
  name VARCHAR(20),
  description VARCHAR(255)
); 

create table Message
(
  ID INTEGER primary key,
  message VARCHAR(255)
);


