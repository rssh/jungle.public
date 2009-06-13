#!/usr/bin/groovy

import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

def withOut=false;
def xxout=System.out;


try {
 xxout = xout;
 withOut=true;
 println("property xout is ${xout} ");
}catch(MissingPropertyException ex){
  println("Missint property on xout ");
}

if (!withOut) {
  xout=xxout;
}

try {
  println("countryNames.size() is "+countryNames.size());
}catch(MissingPropertyException ex){
  println("Missint property countryNames ");
}

class CLDRSaxHandler extends DefaultHandler
{
  def inSupplementalData=false;
  def inCurrencyData=false;
  def inLanguageTerritory=false;
  def inVersion=false;
  def inGeneration=false;
  def inCldrVersion=false;
  def inTerritoryContainment=false;
  def inLanguageData=false;
  def inTerritoryInfo=false;
  def inTerritory=false;
  def currentTerritory=null;
  def inLanguagePopulation=false;
  def inCalendarData=false;
  def inCalendarPreferenceData=false;
  def inWeekData=false;
  def inMeasurementData=false;
  def inTimezoneData=false;
  def inCodeMappings=false;
  def inBcp47KeywordMappings=false;
  def inReferences=false;
  def xout;
  def countryNames;

  void setXout(PrintWriter exout)
  {
   xout=exout;
  }

  void setCountryNames(Map<String,String> eCountryNames)
  {
   countryNames = eCountryNames;
  }

  void startElement(String ns, String localName, String qName, 
                    Attributes attrs)
  {
    if (!inSupplementalData) {
       if (qName.equals("supplementalData")) {
         inSupplementalData=true;
       }else{
         xout.println("startElement outside supplementalData:"+qName);
       }
    }else{
       if (inCurrencyData) {
          // skip for now.
          ;
       } else if (qName.equals("currencyData")) {
          inCurrencyData=true;
       } else if (inTerritoryContainment) {
          // skip for now.
          ;
       } else if (qName.equals("territoryContainment")) {
          inTerritoryContainment=true;
       } else if (inLanguageData) {
          // TODO
       } else if (qName.equals("languageData")){
          inLanguageData=true;
       } else if (inTerritoryInfo) {
          if (inTerritory) {
            if (inLanguagePopulation) {
              // impossible
              xout.println("-- impossible tag inside language population");
            } else if (qName.equals("languagePopulation")) {
              String languageName = attrs.getValue("type")?.toUpperCase();
              double populationPercent = Double.parseDouble(
                                          attrs.getValue("populationPercent"));
              boolean officialStatus = attrs.getValue("officialStatus")?.equals("official")!=null;
              //println("language ${languageName}, ${populationPercent}, ${officialStatus}");
              if (languageName.length() == 2) {
               if (officialStatus) {
                xout.println("""
                  update countries_linfos 
                    set default_language_code = '${languageName}'
                    where code='${currentTerritory}';
                """);
               }
               if (populationPercent > 10) {
                if (currentTerritory!=null &&
                    countryNames[currentTerritory]!=null) {
                 xout.println("""
                   insert into languages_in_countries(
                             language_code, country_code)
                   values('${languageName}','${currentTerritory}');
                """);
                }else{
                  xout.println("--skip  ${currentTerritory} null or no country");
                }
               }
              }
            }else{
               xout.println("--strange tag ${qName} inside territory");
            }
          }else if (qName.equals("territory")) {
            inTerritory=true;
            currentTerritory=attrs.getValue("type");
            xout.println("--territory ${currentTerritory}");
          }else{
            xout.println("--strange tag ${qName} inside territoryInfo");
          }
       } else if (qName.equals("territoryInfo")){
          inTerritoryInfo=true;
       } else if (inCalendarData) {
          // skip for now.
          ;
       } else if (qName.equals("calendarData")){
          inCalendarData=true;
       } else if (inCalendarPreferenceData) {
          // skip for now.
          ;
       } else if (qName.equals("calendarPreferenceData")){
          inCalendarPreferenceData=true;
       } else if (inWeekData) {
          // skip for now.
          ;
       } else if (qName.equals("weekData")){
          inWeekData=true;
       } else if (inMeasurementData) {
          // skip;
       } else if (qName.equals("measurementData")) {
          inMeasurementData=true;
       } else if (inTimezoneData) {
          // skip
       } else if (qName.equals("timezoneData")) {
          inTimezoneData=true;
       } else if (inCodeMappings) {
          // skip
       } else if (qName.equals("codeMappings")) {
          inCodeMappings=true;
       } else if (inBcp47KeywordMappings) {
          // skip
       } else if (qName.equals("bcp47KeywordMappings")) {
          inBcp47KeywordMappings=true;
       } else if (inReferences) {
          // skip
       } else if (qName.equals("references")) {
          inReferences=true;
       } else if (qName.equals("version")) {
          inVersion=true;
       } else if (qName.equals("generation")) {
          inGeneration=true;
       } else if (qName.equals("cldrVersion")) {
          inCldrVersion=true;
       } else {
          xout.println("--startElement:"+ns+","+localName+","+qName);
       }
    }
  }

  void endElement(String ns, String localName, String qName)
  {
    if (inSupplementalData) {
      if (qName.equals("supplementalData")) {
         inSupplementalData=false;
      }else if (inCurrencyData) {
         if (qName.equals("currencyData")) {
            inCurrencyData=false;
         }else{
          // skip all inside currency data for now.
         }
      }else if (inVersion) {
         if (qName.equals("version")) {
           inVersion=false;
         }
      }else if (inGeneration) {
         if (qName.equals("generation")) {
           inGeneration=false;
         }
      }else if (inCldrVersion) {
         if (qName.equals("cldrVersion")) {
           inCldrVersion=false;
         }
      } else if (inTerritoryContainment) {
         if (qName.equals("territoryContainment")) {
            inTerritoryContainment=false;
         }
      } else if (inLanguageData) {
         if (qName.equals("languageData")) {
            inLanguageData=false;
         }
      } else if (inTerritoryInfo) {
         if (inTerritory) {
            if (inLanguagePopulation) {
               if (qName.equals("languagePopulation")) {
                 inLanguagePopulation=false;
               }
            }else if (qName.equals("territory")) {
               inTerritory=false;
            } 
         }else if (qName.equals("territoryInfo")) {
            inTerritoryInfo=false;
         }          
      } else if (inCalendarData) {
         if (qName.equals("calendarData")) {
            inCalendarData=false;
         }          
      } else if (inCalendarPreferenceData) {
         if (qName.equals("calendarPreferenceData")) {
            inCalendarPreferenceData=false;
         }          
      } else if (inWeekData) {
         if (qName.equals("weekData")) {
            inWeekData=false;
         }
      } else if (inMeasurementData) {
         if (qName.equals("measurementData")) {
           inMeasurementData=false;
         }
      } else if (inTimezoneData) {
         if (qName.equals("timezoneData")) {
           inTimezoneData=false;
         }
      } else if (inCodeMappings) {
         if (qName.equals("codeMappings")) {
           inCodeMappings=false;
         }
      } else if (inBcp47KeywordMappings) {
         if (qName.equals("bcp47KeywordMappings")) {
           inBcp47KeywordMappings=false;
         }
      } else if (inReferences) {
         if (qName.equals("references")) {
           inReferences=false;
         }
      }else{
        xout.println("--endElement:"+ns+","+localName+","+qName);
      }
    } else {
      xout.println("endElement outside supplementalData:"+qName);
    }
  }

}


File cldrFile = new File("CLDR/supplementalData.xml");
def cldrHandler = new CLDRSaxHandler();
cldrHandler.setXout(xout);
cldrHandler.setCountryNames(countryNames);
def reader = SAXParserFactory.newInstance().newSAXParser().xmlReader;
reader.setContentHandler(cldrHandler);
try {
  reader.parse(new InputSource(new FileInputStream(cldrFile)));
}catch(Exception ex){
  ex.printStackTrace();
}

