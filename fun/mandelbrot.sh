#!/bin/bash

export TERM=linux

COLUMNS=${COLUMNS:-$1}
LINES=${LINES:-$2}

WHITE=47
BLACK=40

#. $HOME/bin/gfx

# z_n+1 = z_n^2 + c
N=0;
function mand() {
  c1=$1
  c2=$2
  thresh=$3
  z1=$c1
  z2=$c2
  n=0
  while [ $n -lt $thresh ] ; do

    z1=`echo "($z1^2)-($z2^2)+$c1" | bc -l`
    z2=`echo "(2*$z1*$z2)+$c2" | bc -l`
    a=`echo "scale=0;x=$z1/1*100;if(x<0){-1*x}else{x}" | bc -l`
    b=`echo "scale=0;x=$z2/1*100;if(x<0){-1*x}else{x}" | bc -l`
    #echo "$z1, $z2, $a, $b"
    if [ $a -gt 100 ] ; then
      N=$n;
      return;
    fi
    if [ $b -gt 100 ] ; then
      N=$n;
      return;
    fi
        
    n=$[ n + 1 ]
  done
  N=$n
  return;
}

x=0
y=0
width=$COLUMNS
height=$LINES
color=$5
thresh=9

#rect 0 0 $COLUMNS $LINES $BLACK

h=1
while [ $h -le $height ] ; do
    
  cy=$[ y + h ]
    
  w=1
  while [ $w -le $width ] ; do
    c1=`echo "-2+(($w/$width)*4)" | bc -l`
    c2=`echo "2-(($h/$height)*4)" | bc -l`
    mand $c1 $c2 $thresh
    color=$[ 40 + N ]
    cx=$[ x + w ]
    #echo "$c1, $c2"
    echo -en "\033[$cy;${cx}f\033[1;${color}m "
    w=$[ w + 1 ]
  done
    
  h=$[ h + 1 ]
done
