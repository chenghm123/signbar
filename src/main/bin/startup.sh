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
    $JAVA_HOME/bin/java -jar $BASE_DIR/lib/signbar.jar >/dev/null 2>&1 &
    RETVAL=$?
    if [ $RETVAL == 0 ] ; then
        echo "signbar is running!"
    else
        echo "signbar running failed!"
    fi
else
    RETVAL=1;
    echo "signbar is already running!"
fi

exit $RETVAL;