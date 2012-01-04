#!/usr/bin/groovy

def withOut=false;
def xxout=System.out;

try {
  def xbasedir=basedir;
}catch(MissingPropertyException ex){
  def basedir="."; 
}

try {
 xxout = xout;
 withOut=true;
 println("property xout is ${xout} ");
}catch(MissingPropertyException ex){
  println("Missint property on xout ");
}

if (!withOut) {
  xout=xxout;
}else{
  println("out was here ");
}


def byCountryCode = [:];
def byCountryCodeOrigins = [:];
def byIso639_1Code = [:];
def byIso639_2Code = [:];

int maxLanguagesPerCountry = 0;
String maxCountryCode=null;
int maxLanguagesPerCountryOrigins = 0;
String maxCountryCodeOrigins=null;

new File("${basedir}/ISO-639-2_utf-8.txt").eachLine {

 String[] names = it.toUpperCase().split("\\|");
 String   fln = names[3].split(";")[0];
 //println("it:${it}");
 //println("iso-639-2-t=${names[0]}, iso-639-1=${names[2]}, name_eng=${fln}");
 String iso639_2=names[0];
 String iso639_1=names[2];

 if (iso639_1.length()>0) {
   byIso639_1Code[iso639_1]=iso639_2;
   byIso639_2Code[iso639_2]=iso639_1;
 }

}

new File("${basedir}/ethnologue/LanguageIndex.tab").eachLine {

 String[] names = it.toUpperCase().split("[\t]+");
 String   fln = names[3].split(";")[0];
 //println("it:${it}");
 String iso639_2Code = names[0];
 String countryCode = names[1];
 String state=names[2];
 String languageName=names[3];

 if (byIso639_2Code[iso639_2Code]!=null) {
   //println("names:${names}");
   List<String> clangs = byCountryCode[countryCode];
   if (clangs==null) {
      clangs = new ArrayList<String>(); 
      byCountryCode[countryCode] = clangs;
   }
   if (!clangs.contains(iso639_2Code)) {
      clangs.add(iso639_2Code);
      if (clangs.size() > maxLanguagesPerCountry) {
        maxLanguagesPerCountry = clangs.size();
        maxCountryCode=countryCode;
      }
   }
 }

}

//println("max_languages_per_country:"+maxLanguagesPerCountry+" in "+maxCountryCode);

new File("${basedir}/ethnologue/LanguageCodes.tab").eachLine {
 
 String[] names = it.toUpperCase().split("[\t]+");
 String   fln = names[3].split(";")[0];
 String iso639_2Code = names[0];
 String countryCode = names[1];
 String languageName=names[3];

 if (byIso639_2Code[iso639_2Code]!=null) {
   //println("names:${names}");
   List<String> clangs = byCountryCodeOrigins[countryCode];
   if (clangs==null) {
      clangs = new ArrayList<String>(); 
      byCountryCodeOrigins[countryCode] = clangs;
   }
   if (!clangs.contains(iso639_2Code)) {
     clangs.add(iso639_2Code);
     if (clangs.size() > maxLanguagesPerCountryOrigins) {
       maxLanguagesPerCountryOrigins = clangs.size();
       maxCountryCodeOrigins=countryCode;
     }
   }
 }

}

//println("max_languages_per_country_origins:"+maxLanguagesPerCountryOrigins+" in "+maxCountryCodeOrigins);

new File("${basedir}/ethnologue/CountryCodes.tab").eachLine{

 String[] fields = it.split("[\t]+");

 if (fields.length > 1 ) {
   fields[0]=fields[0].replace(" ","");
   if (fields[0].length()==2) {
     countryNames[fields[0]]=fields[1];
   }
 }
 

}

new File("${basedir}/ISO-3166.txt").eachLine {

 String[] names = it.split("[\t]+");
 if (names.length > 1) {
   String countryCode=names[1];
   countryCode=countryCode.replaceAll(" ","");
   //println("it:${it}");
   //println("name=${names[0]}, code=${names[1]}, all:${names}");

   List<String> origins = byCountryCodeOrigins[countryCode];
   List<String> alls = byCountryCode[countryCode];

   String defaultLanguage=null;

   if (origins!=null) {
      if (origins.size()==1) {
         defaultLanguage=origins[0];
         defaultLanguage=byIso639_2Code[defaultLanguage];
      }
   }
   String countryName=countryNames[countryCode];

   def sql;
   if (countryName!=null) {
     countryName=countryName.replace("'","''");
     if (defaultLanguage!=null) {
       sql="""
       insert into countries_linfos(code, name_eng, default_language_code)
           values('${countryCode}','${countryName}','${defaultLanguage}');
       """;
     }else{
       sql="""
         insert into countries_linfos(code, name_eng, default_language_code)
           values('${countryCode}','${countryName}',NULL);
       """;
     }

     xout.println(sql.toString());

     if (alls!=null && false) {

//     println("alls:${alls}");

       alls.each {
         def languageCode = byIso639_2Code[it];
         if (languageCode!=null) {
           sql="""
             insert into languages_in_countries(language_code, country_code)
                values('${languageCode}','${countryCode}');
           """;
           xout.println(sql);
         }
       }
     }
   }

 }

 

}



