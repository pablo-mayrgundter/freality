export CLASSPATH=~/freality
(cd ~/freality && [ -f net/http/Server.class ] || javac net/http/Server.java)
java -Dport=8090 net.http.Server
