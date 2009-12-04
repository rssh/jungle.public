
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

insert into CityNames(id,name_en,name_ru,name_uk,
                         description_en,description_ru,description_uk)
  values(1,'Kyiv','Киев','Київ',
                         'Good city', 'Хороший город', 'Добре місто');

insert into CityWithText(id,text)
  values(1, 'Welcome to Kiev');

insert into CityWithTextTranslations(id,text_en,text_ru,text_uk)
  values(1, 'Welcome to Kiev', 'Добро пожаловать в Киев', 'Вітаємо у Київі');
  

insert into City(id,country_code,name,description)
  values(2,'RU','Moskow','');

insert into CityNames(id,name_en,name_ru,name_uk,
                         description_en,description_ru,description_uk)
  values(2,'Moskow','Москва','Москва',
                         'Big city', 'Большой город', 'Велике місто');

insert into CityWithText(id,text)
  values(2,'Moscow does not believe in tears');

insert into CityWithTextTranslations(id,text_en,text_ru,text_uk)
  values(2,'Moscow does not believe in tears',
           'Москва слезам не верит',
           'Москва сльозам не вірить');


insert into City(id,country_code,name,description)
  values(3,'UA','Cherkassy','');


insert into CityNames(id,name_en,name_ru,name_uk)
  values(3,'Cherkassy','Черкассы','Черкаси');


insert into City(id,country_code,name,description)
  values(4,'UA','Vinnica','');

insert into CityNames(id,name_en,name_ru,name_uk)
  values(4,'Vinnica','Винница','Вінніца');


insert into City(id,country_code,name,description)
  values(5,'UA','Jitomir','');

insert into CityNames(id,name_en,name_ru,name_uk, 
                      description_en,description_ru,description_uk)
  values(5,'Jitomir','Житомир','Житомир',
             'famous as province','знаменито как провинция',
             'відоме як провінція');

insert into City(id,country_code,name,description)
  values(6,'UA','Odessa','');

insert into CityNames(id,name_en,name_ru,name_uk, 
                      description_en,description_ru,description_uk)
  values(6,'Odessa','Одесса','Одеса',
             'famous with jokes','знаменито своеборазным юмором',
             'відоме своєрідним гумором');

insert into CityWithText(id,text)
  values(6,'Odessa-mother');

insert into CityWithTextTranslations(id,text_en,text_ru,text_uk)
  values(6, 'Odessa-mother', 'Одесса-мама', 'Одеса - мати');
 
insert into localization_bundle_tables(table_name,
                                       translation_table_name,
                                       pk_column_name,
                                       bundle_name,
                                       entity_classname)
  values('OrgCities','OrgNames','id','testdata',
              'ua.gradsoft.jungle.localization.testdata.OrgCities');

insert into localization_bundle_table_columns(table_name,column_prefix)
  values('OrgCities','name');


insert into OrgCities(id,name,location_city, residence_city)
  values(1,'Bank Kiev',1,1);

insert into OrgNames(id,name_en,name_ru,name_uk)
  values(1,'Bank Kiev','Банк Киев', 'Банк Київ');

insert into OrgCities(id,name,location_city, residence_city)
  values(2,'Bank Moskva',2,2);

insert into OrgNames(id,name_en,name_ru,name_uk)
  values(2,'Bank Moskva','Банк Москва', 'Банк Москва');

insert into OrgCities(id,name,location_city, residence_city)
  values(3,'Moskva-Kiev',2,1);

insert into OrgNames(id,name_en,name_ru,name_uk)
  values(3,'Moskva-Kiev','Москва-Киев','Москва-Київ');

insert into OrgCities(id,name,location_city, residence_city)
  values(4,'Kiev-Moskva',1,2);

insert into OrgNames(id,name_en,name_ru,name_uk)
  values(4,'Kiev-Moskva','Киев-Москва','Київ-Москва');

insert into OrgCities(id,name,location_city, residence_city)
  values(5,'KC',1,3);

insert into OrgNames(id,name_en,name_ru,name_uk)
  values(5,'КС','КС','Кс');


insert into OrgCities(id,name,location_city, residence_city)
  values(6,'Kiev-unknown',1,NULL);

insert into OrgNames(id,name_en,name_ru,name_uk)
  values(6,'Kiev-unknown','Киев-неизвестно','Київ-невідомо');

insert into message(id, message) values(1,'first message');


