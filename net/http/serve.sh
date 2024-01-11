BASE=~/c/pm/freality
export CLASSPATH=$BASE
(cd $BASE && [ -f net/http/Server.class ] || javac net/http/Server.java)
java -Dport=8090 net.http.Server
