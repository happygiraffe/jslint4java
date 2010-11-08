package com.googlecode.jslint4java.cli;

import com.beust.jcommander.Parameter;

/**
 * Flags which correspond directly to JSLint options.
 */
class JSLintFlags {
    // BEGIN-OPTIONS
    @Parameter(names = "--adsafe", description = "If adsafe should be enforced")
    public Boolean adsafe = null;

    @Parameter(names = "--bitwise", description = "If bitwise operators should not be allowed")
    public Boolean bitwise = null;

    @Parameter(names = "--browser", description = "If the standard browser globals should be predefined")
    public Boolean browser = null;

    @Parameter(names = "--cap", description = "If upper case html should be allowed")
    public Boolean cap = null;

    @Parameter(names = "--css", description = "If css workarounds should be tolerated")
    public Boolean css = null;

    @Parameter(names = "--debug", description = "If debugger statements should be allowed")
    public Boolean debug = null;

    @Parameter(names = "--devel", description = "If logging should be allowed (console, alert, etc.)")
    public Boolean devel = null;

    @Parameter(names = "--eqeqeq", description = "If === should be required")
    public Boolean eqeqeq = null;

    @Parameter(names = "--es5", description = "If es5 syntax should be allowed")
    public Boolean es5 = null;

    @Parameter(names = "--evil", description = "If eval should be allowed")
    public Boolean evil = null;

    @Parameter(names = "--forin", description = "If for in statements must filter")
    public Boolean forin = null;

    @Parameter(names = "--fragment", description = "If html fragments should be allowed")
    public Boolean fragment = null;

    @Parameter(names = "--immed", description = "If immediate invocations must be wrapped in parens")
    public Boolean immed = null;

    @Parameter(names = "--indent", description = "The number of spaces used for indentation (default is 4)")
    public String indent = null;

    @Parameter(names = "--laxbreak", description = "If line breaks should not be checked")
    public Boolean laxbreak = null;

    @Parameter(names = "--maxerr", description = "The maximum number of warnings reported (default is 50)")
    public String maxerr = null;

    @Parameter(names = "--maxlen", description = "Maximum line length")
    public String maxlen = null;

    @Parameter(names = "--newcap", description = "If constructor names must be capitalized")
    public Boolean newcap = null;

    @Parameter(names = "--nomen", description = "If names should be checked")
    public Boolean nomen = null;

    @Parameter(names = "--on", description = "If html event handlers should be allowed")
    public Boolean on = null;

    @Parameter(names = "--onevar", description = "If only one var statement per function should be allowed")
    public Boolean onevar = null;

    @Parameter(names = "--passfail", description = "If the scan should stop on first error")
    public Boolean passfail = null;

    @Parameter(names = "--plusplus", description = "If increment/decrement should not be allowed")
    public Boolean plusplus = null;

    @Parameter(names = "--predef", description = "The names of predefined global variables.")
    public String predef = null;

    @Parameter(names = "--regexp", description = "If the . should not be allowed in regexp literals")
    public Boolean regexp = null;

    @Parameter(names = "--rhino", description = "If the rhino environment globals should be predefined")
    public Boolean rhino = null;

    @Parameter(names = "--safe", description = "If use of some browser features should be restricted")
    public Boolean safe = null;

    @Parameter(names = "--strict", description = "Require the \"use strict\"; pragma")
    public Boolean strict = null;

    @Parameter(names = "--sub", description = "If all forms of subscript notation are tolerated")
    public Boolean sub = null;

    @Parameter(names = "--undef", description = "If variables should be declared before used")
    public Boolean undef = null;

    @Parameter(names = "--white", description = "If strict whitespace rules apply")
    public Boolean white = null;

    @Parameter(names = "--widget", description = "If the yahoo widgets globals should be predefined")
    public Boolean widget = null;

    @Parameter(names = "--windows", description = "If ms windows-specigic globals should be predefined")
    public Boolean windows = null;

    // END-OPTIONS
}
