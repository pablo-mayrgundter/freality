NAME=JosDesktop
LOG=$NAME.log
JAR=$NAME.jar
CLASSES=`cat $LOG | egrep -v 'Loaded (com.|java.|javax.|org.xml.|sun.|apple.)' | sed 's/\./\//g' | awk '/Loaded/{print $2".class"}' | sort | uniq`
SRCS=`cat $LOG | grep Loaded | egrep -v 'Loaded (com.|java.|javax.|org.xml.|sun.|apple.)' | sed -e 's/$.*//g' -e 's/\./\//g' | awk '/Loaded/{print $2".java"}' | sort | uniq`
DATA="jos/desktop/*.properties desktop.jpg"
jar cmf $NAME.mf $JAR $CLASSES $SCRS $DATA
jarsigner -keystore myKeys $JAR freality.googlecode.com
