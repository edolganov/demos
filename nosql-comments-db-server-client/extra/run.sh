#! /bin/bash

if [ ! -e "log.txt" ] ; then
 touch "log.txt"
 chmod 664 log.txt
fi

JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
PATH=$JAVA_HOME/bin:$PATH


#-Xms128m -Xmx512m
nohup java -Dfile.encoding=UTF-8 -classpath "lib/*" sc.comp.cdb.server.CommentDbServer 12060 300 ../../comments-data > log.txt 2>&1 &