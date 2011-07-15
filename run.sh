#!/bin/sh

DIR=`dirname $0`
CP="${DIR}/.:${DIR}/bin"
JAR_PATH="${DIR}/lib/"
CLASS="com.thedeanda.ajaxproxy.Main"

for F in $( ls ${JAR_PATH}*.jar ); do
	CP="${CP}:${F}"
done

java -Xms24m -Xmx128m -classpath ${CP} ${CLASS} $@
