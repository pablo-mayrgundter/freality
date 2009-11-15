#!/bin/bash
export TERM=linux
COLUMNS=$1
LINES=$2
while true ; do
    x=$((RANDOM%COLUMNS))
    y=$((RANDOM%LINES))
    c=$((40+(RANDOM%10)))
    width=$((1+(RANDOM%((COLUMNS-x)+1))))
    height=$((1+(RANDOM%((LINES-y)+1))))
    h=$y;
    while [ $h -lt $height ] ; do
        w=$x;
        while [ $w -lt $width ] ; do
            xx=$[ x+w ]
            echo -en "\033[$yy;${xx}f\033[1;${c}m "
            w=$[ w + 1 ]
        done
        yy=$[ y+h ]
        h=$[ h + 1 ]
    done
done