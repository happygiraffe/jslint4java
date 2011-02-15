package com.googlecode.jslint4java.cli;

import com.beust.jcommander.Parameter;

/**
 * Flags which correspond directly to JSLint options.
 */
class JSLintFlags {
    // BEGIN-OPTIONS
    @Parameter(names = "--adsafe", description = "If adsafe should be enforced")
    public Boolean ADSAFE = null;

    @Parameter(names = "--bitwise", description = "If bitwise operators should not be allowed")
    public Boolean BITWISE = null;

    @Parameter(names = "--browser", description = "If the standard browser globals should be predefined")
    public Boolean BROWSER = null;

    @Parameter(names = "--cap", description = "If upper case html should be allowed")
    public Boolean CAP = null;

    @Parameter(names = "--continue", description = "If the continuation statement should be tolerated")
    public Boolean CONTINUE = null;

    @Parameter(names = "--css", description = "If css workarounds should be tolerated")
    public Boolean CSS = null;

    @Parameter(names = "--debug", description = "If debugger statements should be allowed")
    public Boolean DEBUG = null;

    @Parameter(names = "--devel", description = "If logging should be allowed (console, alert, etc.)")
    public Boolean DEVEL = null;

    @Parameter(names = "--es5", description = "If es5 syntax should be allowed")
    public Boolean ES5 = null;

    @Parameter(names = "--evil", description = "If eval should be allowed")
    public Boolean EVIL = null;

    @Parameter(names = "--forin", description = "If for in statements must filter")
    public Boolean FORIN = null;

    @Parameter(names = "--fragment", description = "If html fragments should be allowed")
    public Boolean FRAGMENT = null;

    @Parameter(names = "--indent", description = "The number of spaces used for indentation (default is 4)")
    public String INDENT = null;

    @Parameter(names = "--maxerr", description = "The maximum number of warnings reported (default is 50)")
    public String MAXERR = null;

    @Parameter(names = "--maxlen", description = "Maximum line length")
    public String MAXLEN = null;

    @Parameter(names = "--newcap", description = "If constructor names must be capitalized")
    public Boolean NEWCAP = null;

    @Parameter(names = "--nomen", description = "If names should be checked")
    public Boolean NOMEN = null;

    @Parameter(names = "--on", description = "If html event handlers should be allowed")
    public Boolean ON = null;

    @Parameter(names = "--onevar", description = "If only one var statement per function should be allowed")
    public Boolean ONEVAR = null;

    @Parameter(names = "--passfail", description = "If the scan should stop on first error")
    public Boolean PASSFAIL = null;

    @Parameter(names = "--plusplus", description = "If increment/decrement should not be allowed")
    public Boolean PLUSPLUS = null;

    @Parameter(names = "--predef", description = "The names of predefined global variables.")
    public String PREDEF = null;

    @Parameter(names = "--regexp", description = "If the . should not be allowed in regexp literals")
    public Boolean REGEXP = null;

    @Parameter(names = "--rhino", description = "If the rhino environment globals should be predefined")
    public Boolean RHINO = null;

    @Parameter(names = "--safe", description = "If use of some browser features should be restricted")
    public Boolean SAFE = null;

    @Parameter(names = "--strict", description = "Require the \"use strict\"; pragma")
    public Boolean STRICT = null;

    @Parameter(names = "--sub", description = "If all forms of subscript notation are tolerated")
    public Boolean SUB = null;

    @Parameter(names = "--undef", description = "If variables should be declared before used")
    public Boolean UNDEF = null;

    @Parameter(names = "--white", description = "If strict whitespace rules apply")
    public Boolean WHITE = null;

    @Parameter(names = "--widget", description = "If the yahoo widgets globals should be predefined")
    public Boolean WIDGET = null;

    @Parameter(names = "--windows", description = "If ms windows-specigic globals should be predefined")
    public Boolean WINDOWS = null;

    // END-OPTIONS
}
