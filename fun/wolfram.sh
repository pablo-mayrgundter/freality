echo 'Usage: ./wolfram.sh $COLUMNS $LINES "0 0 0 1 1 1 1 0"'

width=$1
height=$2

input[0]=0
output[0]=0
w=1
while (( w < width )) ; do
    input[$w]=0
    output[$w]=0
    ((w++))
done
input[ $[width/2] ]=1
rule=($3)

r=0
while (( r < 8 )) ; do
    echo "$r: ${rule[$r]}"
    ((r++))
done

color[0]='\033[1;40m' # Background
color[1]='\033[1;46m' # Foreground
h=1
#while (( h < height )) ; do
while (( 1 )) ; do

    w=1
    while (( w < width )) ; do

        m=${input[$w]}

        # Start
        if (( w == 0 )) ; then
            m=0
            l=0
            r=${input[$w+1]}
        elif (( w == width - 1 )) ; then
            m=0
            r=0
            l=${input[$w-1]}
        else
            r=${input[$w+1]}
            l=${input[$w-1]}
        fi

        out=0
        if (( l && m && r )) ; then
            out=${rule[0]}
        elif (( l && m && \!r )) ; then
            out=${rule[1]}
        elif (( l && \!m && r )) ; then
            out=${rule[2]}
        elif (( l && \!m && \!r )) ; then
            out=${rule[3]}
        elif (( \!l && m && r )) ; then
            out=${rule[4]}
        elif (( \!l && m && \!r )) ; then
            out=${rule[5]}
        elif (( \!l && \!m && r )) ; then
            out=${rule[6]}
        else
            out=${rule[7]}
        fi
        output[$w]=$out

        ((w++))
    done

    w=0
    while (( w < width )) ; do
        out=${output[$w]}
        input[$w]=$out
        echo -en "${color[$out]} "
        ((w++))
    done

#    ((h++))
done
