

create table languages
(
  CODE CHAR(2)  PRIMARY KEY,  -- iso639-1 language code
  CODE_ISO_639_2 CHAR(2) unique not null, -- appropriative 639_2 language code
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
  COUNTRY_CODE  CHAR(2)  references county_linfos(code)
 primary key(country_code, language_code)
);

create table localization_type
(
  ID                 INTEGER primary key,
  ONE_TABLE          CHAR(1) default '0'
  TRANSLATE_TABLES   CHAR(1) default '0'
);

create table localization_bundles
(
  NAME              VARCHAR(64) PRIMARY KEY,
  PRIMARY_LANGUAGE  CHAR(2) references languages(code),
  LOCALIZATION_TYPE INTEGER
);

create table localization_bundle_tables
(
  TABLE_PREFIX          VARCHAR(64) primary key,
  TABLE_PK_COLUMN_NAME  VARCHAR(32)
  BUNDLE_NAME        VARCHAR(64) references localization_bundles(name),
);


create table localization_bundle_table_columns
(
  TABLE_PREFIX          VARCHAR(64),
  COLUMN_PREFIX         VARCHAR(64)
      primary key(table_prefix, column_prefix),
  BUNDLE_NAME        VARCHAR(64)
      references localization_bundles(id)
);
  
