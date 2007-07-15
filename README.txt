jslint for java
===============

This is a java wrapper around the fabulous tool by Douglas Crockford, jslint
(See <http://jslint.com/>). It provides a simple interface for detecting
potential problems in JavaScript code.

The output of this project is two jar files:

* dist/jslint.jar (just the jslint code)
* dist/jslint+rhino.jar (jslint and the rhino JavaScript engine)

If you want a tool to run at the command line, you'll want the latter.

The usage is simple:

  % java -jar jslint+rhino.jar application.js
  jslint:application.js:11:9:Line breaking error ')'.
  jslint:application.js:11:10:Missing semicolon.

There are a multitude of options; try "--help" for more details.

The output is colon separated fields.  The fields are:

* "jslint"
* the file name
* the line number (starting at zero)
* the character number (starting at zero)
* the problem that was found

If you have any comments or queries, please send them to dom [at] happygiraffe.net.

This software is licenced under the BSD licence.

@(#) $Id$