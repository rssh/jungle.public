

create table languages
(
  CODE CHAR(2)  PRIMARY KEY,  -- iso639-1 language code
  CODE_ISO_639_2 CHAR(2) not null, -- appropriative 639_2 language code
  NAME_ENG   VARCHAR(64) not null, -- english name of language.
    unique(CODE_ISO_639_2),
    unique(NAME_ENG)
);

create table countries_linfos
(
  CODE   CHAR(2)  PRIMARY KEY, -- iso 3166 country code.
  NAME_ENG        VARCHAR(64),
  -- default language for country if exists.
  DEFAULT_LANGUAGE_CODE CHAR(2),
  DEFAULT_CURRENCY_CODE CHAR(3), 
    unique(NAME_ENG),
    foreign key(DEFAULT_LANGUAGE_CODE) references languages(code)
);

create table languages_in_countries
(
  LANGUAGE_CODE CHAR(2),
  COUNTRY_CODE  CHAR(2),
 primary key(country_code, language_code),
 foreign key(language_code)  references languages(code),
 foreign key(country_code) references countries_linfos(code)
);

create table localization_types
(
  ID                 INTEGER primary key,
  NAME               VARCHAR(32),
  DESCRIPTION        VARCHAR(1024),
   unique(name)
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
  PRIMARY_LANGUAGE  CHAR(2),
  LOCALIZATION_TYPE INTEGER,
  foreign key(PRIMARY_LANGUAGE) references languages(code),
  foreign key(LOCALIZATION_TYPE) references localization_types(id)
);


create table localization_bundle_languages
(
  bundle_name   VARCHAR(64),
  language_code  CHAR(2),
 primary key(bundle_name, language_code),
  foreign key(bundle_name) references localization_bundles(name),
  foreign key(language_code) references languages(code)
);


create table localization_bundle_tables
(
  TABLE_NAME          VARCHAR(64) primary key,
  TRANSLATION_TABLE_NAME    VARCHAR(64),
  PK_COLUMN_NAME  VARCHAR(32) not null,
  BUNDLE_NAME        VARCHAR(64) not null,
  ENTITY_CLASSNAME   VARCHAR(255),
  foreign key(BUNDLE_NAME) references localization_bundles(name),
  unique(TRANSLATION_TABLE_NAME),
  unique(ENTITY_CLASSNAME)
);


create table localization_bundle_table_columns
(
  TABLE_NAME          VARCHAR(64),
  COLUMN_PREFIX         VARCHAR(64),
      primary key(table_name, column_prefix)
);
  
