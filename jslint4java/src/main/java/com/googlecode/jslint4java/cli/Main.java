package com.googlecode.jslint4java.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Option;
import com.googlecode.jslint4java.formatter.JSLintXmlFormatter;
import com.googlecode.jslint4java.formatter.JUnitXmlFormatter;
import com.googlecode.jslint4java.formatter.PlainFormatter;
import com.googlecode.jslint4java.formatter.ReportFormatter;

/**
 * A command line interface to {@link JSLint}.
 *
 * @author dom
 */
class Main {

    /**
     * This is just to avoid calling {@link System#exit(int)} outside of main()…
     */
    @SuppressWarnings("serial")
    private static class DieException extends RuntimeException {
        private final int code;

        public DieException(String message, int code) {
            super(message);
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private static final String PROGNAME = "jslint";

    /**
     * The main entry point. Try passing in "--help" for more details.
     *
     * @param args
     *            One or more JavaScript files.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        try {
            Main main = new Main();
            for (String file : main.processOptions(args)) {
                main.lintFile(file);
            }
            System.exit(main.isErrored() ? 1 : 0);
        } catch (DieException e) {
            if (e.getMessage() != null) {
                System.err.println(PROGNAME + ": " + e.getMessage());
            }
            System.exit(e.getCode());
        }
    }

    private Charset encoding = Charset.defaultCharset();

    private boolean errored = false;

    private final Flags flags = new Flags();

    private JSLint lint;

    private Main() throws IOException {
        lint = new JSLintBuilder().fromDefault();
    }

    private void die(String message) {
        throw new DieException(message, 1);
    }

    private void err(String message) {
        System.out.println(PROGNAME + ":" + message);
        setErrored(true);
    }

    /**
     * Fetch the named {@link Option}, or null if there is no matching one.
     */
    private Option getOption(String optName) {
        try {
            return Option.valueOf(optName.toUpperCase(Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void info(String message) {
        System.out.println(message);
    }

    private boolean isErrored() {
        return errored;
    }

    private void lintFile(String file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            JSLintResult result = lint.lint(file, reader);
            if (flags.report.equals("plain")) {
                info(new PlainFormatter().format(result));
            } else if (flags.report.equals("jslint")) {
                info(new JSLintXmlFormatter().format(result));
            } else if (flags.report.equals("junit")) {
                info(new JUnitXmlFormatter().format(result));
            } else if (flags.report.equals("html")) {
                info(new ReportFormatter().format(result));
            } else {
                for (Issue issue : result.getIssues()) {
                    err(issue.toString());
                }
            }
        } catch (FileNotFoundException e) {
            die(file + ": No such file or directory.");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private List<String> processOptions(String[] args) {
        JSLintFlags jslintFlags = new JSLintFlags();
        JCommander jc = new JCommander(new Object[] { flags, jslintFlags }, args);
        jc.setProgramName("jslint4java");
        if (flags.help) {
            usage(jc);
        }
        if (flags.encoding != null) {
            encoding = flags.encoding;
        }
        if (flags.jslint != null) {
            setJSLint(flags.jslint);
        }
        for (ParameterDescription pd : jc.getParameters()) {
            Field field = pd.getField();
            // Is it declared on JSLintFlags?
            if (!field.getDeclaringClass().isAssignableFrom(JSLintFlags.class)) {
                continue;
            }
            try {
                // Need to get Option.
                Option o = getOption(field.getName());
                // Need to get value.
                Object val = field.get(jslintFlags);
                if (val == null) {
                    continue;
                }
                Class<?> type = field.getType();
                if (type.isAssignableFrom(Boolean.class)) {
                    lint.addOption(o);
                }
                // In theory, everything else should be a String for later parsing.
                else if (type.isAssignableFrom(String.class)) {
                    lint.addOption(o, (String) val);
                } else {
                    die("unknown type \"" + type + "\" (for " + field.getName() + ")");
                }
            } catch (IllegalArgumentException e) {
                die(e.getMessage());
            } catch (IllegalAccessException e) {
                die(e.getMessage());
            }
        }
        if (flags.files.isEmpty()) {
            usage(jc);
            return null; // can never happen
        } else {
            return flags.files;
        }
    }

    private void setErrored(boolean errored) {
        this.errored = errored;
    }

    private void setJSLint(String jslint) {
        try {
            lint = new JSLintBuilder().fromFile(new File(jslint));
        } catch (IOException e) {
            die(e.getMessage());
        }
    }

    private void usage(JCommander jc) {
        jc.usage();
        info("using jslint version " + lint.getEdition());
        throw new DieException(null, 0);
    }

}
