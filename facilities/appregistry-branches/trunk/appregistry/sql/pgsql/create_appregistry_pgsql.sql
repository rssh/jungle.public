
/**
 * registry of jungle applications in database
 **/
create table appregistry
(
  NAME        VARCHAR(64)  PRIMARY KEY,
  VERSION     VARCHAR(16),
  DESCRIPTION VARCHAR(255),
  PROVIDER_ID NUMBER(40,0)  
);

