export CLASSPATH=`dirname $BASH_SOURCE | sed 's|/net/http||g'`
java -Dport=8080 net.http.Server
