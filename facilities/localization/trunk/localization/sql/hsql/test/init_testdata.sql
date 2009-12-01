
insert into localization_bundles(name,primary_language,localization_type)
  values('testdata','EN',1);

insert into localization_bundle_languages(bundle_name,language_code)
   values('testdata','EN');
insert into localization_bundle_languages(bundle_name,language_code)
   values('testdata','RU');
insert into localization_bundle_languages(bundle_name,language_code)
   values('testdata','UK');

insert into localization_bundle_tables(table_name,
                                       translation_table_name,
                                       pk_column_name,
                                       bundle_name,
                                       entity_classname)
  values('Country','CountryNames','isoCode','testdata',
              'ua.gradsoft.jungle.localization.testdata.Country');


insert into localization_bundle_table_columns(table_name,column_prefix)
  values('Country','name');

insert into Country(isoCode,name)
  values('UA', 'Ukraine');

insert into CountryNames(isoCode, name_en, name_ru, name_uk)
   values('UA','Ukraine','Украина','Україна');

insert into Country(isoCode,name)
  values('RU', 'Russia');

insert into CountryNames(isoCode, name_en, name_ru, name_uk)
   values('RU','Russia','Рoссия','Россія');

insert into Country(isoCode,name)
  values('AF', 'Afganistan');

insert into CountryNames(isoCode, name_en, name_ru, name_uk)
  values('AF', 'Afganistan','Афганистан','Афганістан');

insert into Country(isoCode,name)
  values('AL', 'Albania');

insert into CountryNames(isoCode, name_en, name_ru, name_uk)
  values('AL', 'Albania','Албания','Албанія');

insert into Country(isoCode,name)
  values('DZ', 'Algeria');

insert into CountryNames(isoCode, name_en, name_ru, name_uk)
  values('DZ', 'Algeria','Альгерия','Алгерія');

insert into localization_bundle_tables(table_name,
                                       translation_table_name,
                                       pk_column_name,
                                       bundle_name,
                                       entity_classname)
  values('City','CityNames','id','testdata',
              'ua.gradsoft.jungle.localization.testdata.City');

insert into localization_bundle_table_columns(table_name,column_prefix)
  values('City','name');
insert into localization_bundle_table_columns(table_name,column_prefix)
  values('City','description');


insert into City(id,country_code,name,description)
  values(1,'UA','Kyiv','');

insert into City(id,country_code,name,description)
  values(2,'RU','Moskow','');

insert into City(id,country_code,name,description)
  values(3,'UA','Cherkassy','');

insert into City(id,country_code,name,description)
  values(4,'UA','Vinnica','');

insert into City(id,country_code,name,description)
  values(5,'UA','Jitomir','');


insert into OrgCities(id,name,location_city, residence_city)
  values(1,'Bank Kiev',1,1);

insert into OrgCities(id,name,location_city, residence_city)
  values(2,'Bank Moskva',2,2);

insert into OrgCities(id,name,location_city, residence_city)
  values(3,'Moskva-Kiev',2,1);

insert into OrgCities(id,name,location_city, residence_city)
  values(4,'Kiev-Moskva',1,2);

insert into OrgCities(id,name,location_city, residence_city)
  values(5,'KC',1,3);

insert into message(id, message) values(1,'first message');


