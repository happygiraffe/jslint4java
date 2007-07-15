package net.happygiraffe.jslint;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    private static final String PROGNAME = "jslint";
    
    /**
     * The main entry point. Try passing in "--help" for more details.
     * 
     * @param args
     *                One or more JavaScript files.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Main main = new Main();
        for (String file : args) {
            main.lintFile(file);
        }
        System.exit(main.isErrored() ? 1 : 0);
    }

    private boolean errored = false;

    private JSLint lint;

    private Main() throws IOException {
        lint = new JSLint();
        lint.addOption(Option.EQEQEQ);
        lint.addOption(Option.UNDEF);
        lint.addOption(Option.WHITE);
    }

    private void err(String message) {
        System.err.println(PROGNAME + ":" + message);
        setErrored(true);
    }

    private boolean isErrored() {
        return errored;
    }

    private void lintFile(String file) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));
            List<Issue> issues = lint.lint(file, reader);
            for (Issue issue : issues) {
                err(issue.toString());
            }
        } catch (FileNotFoundException e) {
            err(file + ":No such file or directory.");
        }
    }

    private void setErrored(boolean errored) {
        this.errored = errored;
    }

}
