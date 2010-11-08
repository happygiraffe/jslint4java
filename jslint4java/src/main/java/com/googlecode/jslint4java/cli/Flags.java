package com.googlecode.jslint4java.cli;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

/**
 * Command line flags for jslint4java.
 *
 * @see JSLintFlags
 */
class Flags {

    @Parameter(names = "--encoding", description = "Specify the input encoding", converter = CharsetConverter.class)
    public Charset encoding;

    @Parameter(names = "--jslint", description = "Specify an alternative version of jslint.js")
    public String jslint;

    @Parameter(names = "--help", description = "Display usage information")
    public boolean help;

    @Parameter(names = "--report", description = "Display report in different formats: plain, xml, junit and report")
    public String report;

    /**
     * All remaining files on the command line. The ones that actually need linting.
     */
    @Parameter(description = "file.js ...")
    public List<String> files = new ArrayList<String>();
}
