LOG=SolarSystem3d.log
JAR=SolarSystem3d.jar
CLASSES=`cat SolarSystem3d.log | egrep -v 'Loaded (com.|java.|javax.|org.xml.|sun.|apple.)' | sed 's/\./\//g' | awk '/Loaded/{print $2".class"}' | sort | uniq`
SRCS=`cat SolarSystem3d.log | grep Loaded | egrep -v 'Loaded (com.|java.|javax.|org.xml.|sun.|apple.)' | sed -e 's/$.*//g' -e 's/\./\//g' | awk '/Loaded/{print $2".java"}' | sort | uniq`
DATA="vr/cpack/space/data/solarSystem.xml vr/cpack/space/textures/*.jpg vr/cpack/space/data/hygfull.csv.gz"
jar cmf SolarSystem3d.mf $JAR $CLASSES $SCRS $DATA
jarsigner -keystore myKeys $JAR freality.googlecode.com
