#!/bin/bash
export TERM=linux
COLUMNS=$1
LINES=$2
while true ; do
    y=0
    while [ $y -lt $LINES ] ; do
        x=0
        while [ $x -lt $COLUMNS ] ; do
            c=$((40+(RANDOM%10)))
            echo -en "\033[$y;${x}f\033[1;${c}m "
            x=$[ x + 1 ]
        done
        y=$[ $y + 1 ]
    done
done