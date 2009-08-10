#!/bin/sh
#
# Download & update the latest jslint.
#
# $Id$

dir=${0%/*}

javaDir="src/main/java"
resourceDir="src/main/resources"
pkgDir="com/googlecode/jslint4java"

$dir/fetch-jslint.sh $resourceDir $pkgDir
$dir/extract-options.rb $resourceDir/$pkgDir/fulljslint.js $javaDir/$pkgDir/Option.java > tmp.java
mv tmp.java $javaDir/$pkgDir/Option.java

echo "Please update jslint4java-docs now!"
