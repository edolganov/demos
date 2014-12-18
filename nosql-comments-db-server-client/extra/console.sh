#! /bin/bash
JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
PATH=$JAVA_HOME/bin:$PATH
java -Dfile.encoding=UTF-8 -classpath "lib/*" sc.comp.cdb.client.CommentDbConsole localhost 12060