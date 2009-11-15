FG=`echo -en "\033[1;44m "`
BG=`echo -en "\033[1;40m "`

x=0
while [ 1 ] ; do
#        echo "obase=2;(2^$x-1)/($x-1)" | bc -ql | tr -d \\\n\\ | sed "s/1/$FG/g"  | sed "s/0/$BG/g"
        echo "obase=2;$x^2-1/$x-1" | bc -ql | tr -d \\\n\\ | sed "s/1/$FG/g"  | sed "s/0/$BG/g"
        echo -en "$BG"
        echo -en "\033[K"
        x=$[ x + 1 ]
done