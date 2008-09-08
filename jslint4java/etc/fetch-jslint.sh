#/bin/sh
#
# Download the latest jslint.
#
# $Id$

resourceDir="${1:-src/main/resources}"
pkgDir="${2:-net/happygiraffe/jslint}"
curl -R -o $resourceDir/$pkgDir/fulljslint.js http://www.jslint.com/fulljslint.js
