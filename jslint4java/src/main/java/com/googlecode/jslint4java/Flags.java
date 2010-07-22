package com.googlecode.jslint4java;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

/**
 * Command line flags for jslint4java.
 */
public class Flags {

    @Parameter(names = "--encoding", description = "Specify the input encoding")
    public String encoding;

    @Parameter(names = "--jslint", description = "Specify an alternative version of jslint.js")
    public String jslint;

    /**
     * All remaining files on the command line. The ones that actually need
     * linting.
     */
    @Parameter
    public List<String> files = new ArrayList<String>();

    // BEGIN-OPTIONS
    // END-OPTIONS
}
