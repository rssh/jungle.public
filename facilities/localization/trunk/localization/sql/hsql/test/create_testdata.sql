
create table Country
(
  isoCode CHAR(2) primary key,
  name VARCHAR(20)
);

create table CountryNames
(
  isoCode CHAR(2) primary key,
  name_en VARCHAR(20),
  name_ru VARCHAR(20),
  name_uk VARCHAR(20),
   foreign key(isoCode) references country(isoCode)
);

create table City
(
  ID INTEGER primary key,
  country_code CHAR(2) not null,
  name VARCHAR(20),
  description VARCHAR(255),
   foreign key(country_code) references country(isoCode)
); 

create table CityNames
(
  id INTEGER primary key,
  name_en VARCHAR(20),
  name_ru VARCHAR(20),
  name_uk VARCHAR(20),
  description_en VARCHAR(255),
  description_ru VARCHAR(255),
  description_uk VARCHAR(255),
   foreign key(id) references city(id)
);

create table OrgCities
(
 id INTEGER primary key,
 name VARCHAR(20),
 location_city INTEGER,
 residence_city INTEGER,
 foreign key(location_city) references City(id),
 foreign key(residence_city) references City(id)
);

create table OrgNames
(
 id INTEGER primary key,
 name_en VARCHAR(20),
 name_ru VARCHAR(20),
 name_uk VARCHAR(20),
   foreign key(id) references OrgCities(id)
);

create table Message
(
  ID INTEGER primary key,
  message VARCHAR(255)
);

create table MessageTranslations
(
  ID INTEGER primary key,
  message_en VARCHAR(255),
  message_ru VARCHAR(255),
  message_uk VARCHAR(255),
   foreign key(id) references message(id)
);

