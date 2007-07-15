package net.happygiraffe.jslint;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A command line interface to {@link JSLint}.
 * 
 * @author dom
 * @version $Id$
 */
public class Main {

    /**
     * The main entry point. Try passing in "--help" for more details.
     * 
     * @param args  One or more JavaScript files.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        JSLint lint = new JSLint();
        lint.addOption(Option.EQEQEQ);
        lint.addOption(Option.UNDEF);
        lint.addOption(Option.WHITE);
        for (String file : args) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));
            List<Issue> issues = lint.lint(file, reader);
            for (Issue issue : issues) {
                System.err.println(issue);
            }
        }
    }

}
