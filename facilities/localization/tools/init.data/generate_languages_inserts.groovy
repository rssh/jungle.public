#!/usr/bin/groovy

def withOut=false;
def xxout=System.out;

println("0 ");

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

new File("ISO-639-2_utf-8.txt").eachLine {

 String[] names = it.toUpperCase().split("\\|");
 String   fln = names[3].split(";")[0];
 //println("it:${it}");
 //println("iso-639-2-t=${names[0]}, iso-639-1=${names[2]}, name_eng=${fln}");
 String iso_639_2=names[0];
 String iso_639_1=names[2];

 if (iso_639_1.length() > 0) {
   String tmpl="""
     insert into languages(code,code_iso_639_2,name_eng)
       values('${iso_639_1}','${iso_639_2}','${fln}');
     """;

   xout.println(tmpl);
 }

}



