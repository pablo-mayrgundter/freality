USE_JIKES = YES
JAVAC_FLAGS = -source 1.4

LIBPATH = jos/lib
SRCPATH = jos/desktop


ifeq ($(USE_JIKES), YES)
	JAVAC = jikes
	JAVAC_FLAGS += +E +F +P
	JAVA_RT_CLASSES = /System/Library/Frameworks/JavaVM.framework/Classes/classes.jar
else
	JAVAC = javac
endif

# This Makefile works from either . or code/
ifneq (,$(findstring $(SRCPATH), $(PWD)))
	JOS_BASE = ../..
else
	JOS_BASE = .
endif

#export CLASSPATH=`find $(LIBPATH) -name "*.jar" | tr '\n' ':'`;
#export JIKESPATH=$(JAVA_RT_CLASSES):$$CLASSPATH;
SRC_FILES = `find $(SRCPATH) -name "*.java"`

all: 
	cd $(JOS_BASE); \
	source $(SRCPATH)/classpath; \
	pwd; \
	$(JAVAC) $(JAVAC_FLAGS) $(SRC_FILES)

clean:
	cd $(JOS_BASE); \
	rm $(SRCPATH)/*.class
