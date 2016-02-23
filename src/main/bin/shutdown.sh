#!/usr/bin/env bash
THIS="$0"

while [ -h "$THIS" ]; do
  ls=`ls -ld "$THIS"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    THIS="$link"
  else
    THIS=`dirname "$THIS"`/"$link"
  fi
done

THIS_DIR=`dirname $THIS`
BASE_DIR=`cd "$THIS_DIR/.." ; pwd`

PID=`ps -ef | grep "$JAVA_HOME/bin/java -jar $BASE_DIR/lib/signbar.jar" | grep -v 'grep' |  awk '{print $2}'`

if [ -z $PID ] ; then
    RETVAL=1
    echo "signbar is not running!"
else
    /bin/kill -9 $PID
    RETVAL=$?
    if [ $RETVAL == 0 ] ; then
        echo "signbar is stoping!"
    else
        echo "signbar stop failed!"
    fi
fi

exit $RETVAL
