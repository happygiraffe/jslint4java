#!/bin/sh
#
# Download & update the latest jslint.
#
# $Id$

dir=${0%/*}
base=$(dirname $dir)

javaDir="$base/src/main/java"
resourceDir="$base/src/main/resources"
pkgDir="com/googlecode/jslint4java"
jslint="fulljslint.js"

# Specify an explicit jslint instead of downloading.
if [[ -f $1 ]] ; then
  cp $1 $resourceDir/$pkgDir/$jslint
else
  $dir/fetch-jslint.sh $resourceDir $pkgDir
fi
$dir/extract-options.rb $resourceDir/$pkgDir/$jslint $javaDir/$pkgDir/Option.java $javaDir/$pkgDir/JSLintFlags.java

echo "Please update jslint4java-docs now!"
