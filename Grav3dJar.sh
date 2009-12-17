JAR=Grav3d.jar
jar cmf Grav3d.mf $JAR phys/{Grav*,Phys*,GravSim*,Point*,Blob*,ColoredBlob*,Display*}.{java,class} gfx/Display3D*.{java,class} gfx/FullScreenableFrame.{java,class} org/freality/gui vr/cpack/space/SpaceShipNavigator.*
jarsigner -keystore myKeys $JAR freality.googlecode.com
