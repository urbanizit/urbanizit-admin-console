#!/bin/sh

cd `dirname "$0"`
java -Dconfig.path=conf/ -jar urbanizit-admin-console.jar $*