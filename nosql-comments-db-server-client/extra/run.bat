::-Xms128m -Xmx512m
start "comment-db-server" java -Dfile.encoding=UTF-8 -classpath "lib\*;" sc.comp.cdb.server.CommentDbServer 12060 300 ../comments-data