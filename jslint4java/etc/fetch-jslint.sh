#/bin/sh
#
# Download the latest jslint.
#

resourceDir="${1:-src/main/resources}"
pkgDir="${2:-net/happygiraffe/jslint}"
curl -R -o $resourceDir/$pkgDir/jslint.js http://www.jslint.com/jslint.js
