XALL=`find $1 -name 'build.xml'`
echo "XALL=$XALL";
if [ "$XALL" = "" ]
then
 echo "can't find build.xml files"
 exit 1
fi
XJAVAC=`grep -l javac $XALL | sort -u | tee t.javac`
echo "XJACAC=$XJAVAC";
if [ "$XJAVAC" = "" ]
then
 echo "files with javac calls not found"
 exit 1
fi
echo "XJAVAC=$XJAVAC";
XJAVAC_TARGET=`grep -l javac.target $XJAVAC | sort -i | tee t.javac_target`
if [ "$XJAVAC_TARGET" = "" ]
then
 echo "all files need to be updated"
else
 echo "result:"
 comm -2 -3 t.javac  t.javac_target 
fi

