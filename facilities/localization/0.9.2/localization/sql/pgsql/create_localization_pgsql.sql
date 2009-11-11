#
# slony ranges: 58
#

create table languages
(
  CODE CHAR(2)  PRIMARY KEY,  -- iso639-1 language code
  CODE_ISO_639_2 CHAR(3) unique not null, -- appropriative 639_2 language code
  NAME_ENG   VARCHAR(64)  unique not null -- english name of language.
);

create table countries_linfos
(
  CODE   CHAR(2)  PRIMARY KEY, -- iso 3166 country code.
  NAME_ENG        VARCHAR(64)  unique,
  -- default language for country if exists.
  DEFAULT_LANGUAGE_CODE CHAR(2) references languages(code),
  DEFAULT_CURRENCY_CODE CHAR(3) 
);

create table languages_in_countries
(
  LANGUAGE_CODE CHAR(2)  references languages(code),
  COUNTRY_CODE  CHAR(2)  references countries_linfos(code),
 primary key(country_code, language_code)
);

create table localization_types
(
  ID                 INTEGER primary key,
  NAME               VARCHAR(32)  unique,
  DESCRIPTION        VARCHAR(1024)
);

insert into localization_types(id,name,description)
 values(1,'OneRowPerEntity',
  'for each localized entity we have additional table with one row, where\n'||
 'for each field which must be translated we have (nLang) fields in \n'|| 
  'additional table with names like <origin_name>_ua, <origin_name>_ra, .. etc'
);

create table localization_bundles
(
  NAME              VARCHAR(64) PRIMARY KEY,
  PRIMARY_LANGUAGE  CHAR(2) references languages(code),
  LOCALIZATION_TYPE INTEGER references localization_types(id)
);


create table localization_bundle_languages
(
  bundle_name   VARCHAR(64) references localization_bundles(name),
  language_code  CHAR(2) references languages(code),
 primary key(bundle_name, language_code)
);

create table localization_bundle_tables
(
  TABLE_NAME            VARCHAR(64) primary key,
  TRANSLATION_TABLE_NAME  VARCHAR(64) unique,
  PK_COLUMN_NAME     VARCHAR(32),
  BUNDLE_NAME        VARCHAR(64) references localization_bundles(name),
  ENTITY_CLASSNAME   VARCHAR(255) unique
);



create table localization_bundle_table_columns
(
  TABLE_NAME          VARCHAR(64) 
       references localization_bundle_tables(table_name),
  COLUMN_PREFIX         VARCHAR(64),
      primary key(table_name, column_prefix)
);
  
