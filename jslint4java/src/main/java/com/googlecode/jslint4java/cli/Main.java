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
import com.beust.jcommander.ParameterException;
import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Option;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;
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
     * The default command line output.
     */
    private static final class DefaultFormatter implements JSLintResultFormatter {
        public String format(JSLintResult result) {
            StringBuilder sb = new StringBuilder();
            for (Issue issue : result.getIssues()) {
                sb.append(PROGNAME);
                sb.append(':');
                sb.append(issue.toString());
                sb.append('\n');
            }
            return sb.toString();
        }

        public String footer() {
            return null;
        }

        public String header() {
            return null;
        }
    }

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
            System.exit(new Main().run(args));
        } catch (DieException e) {
            if (e.getMessage() != null) {
                System.err.println(PROGNAME + ": " + e.getMessage());
            }
            System.exit(e.getCode());
        }
    }

    private int run(String[] args) throws IOException {
        List<String> files = processOptions(args);
        if (formatter.header() != null) {
            info(formatter.header());
        }
        for (String file : files) {
            lintFile(file);
        }
        if (formatter.footer() != null) {
            info(formatter.footer());
        }
        return isErrored() ? 1 : 0;
    }

    private Charset encoding = Charset.defaultCharset();

    private boolean errored = false;

    private JSLintResultFormatter formatter;

    private JSLint lint;

    private Main() throws IOException {
        lint = new JSLintBuilder().fromDefault();
    }

    private void die(String message) {
        throw new DieException(message, 1);
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
            info(formatter.format(result));
            if (!result.getIssues().isEmpty()) {
                setErrored(true);
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
        Flags flags = new Flags();
        JCommander jc = new JCommander(new Object[] { flags , jslintFlags });
        jc.setProgramName("jslint4java");
        try {
			jc.parse(args);
		} catch (ParameterException e) {
			info(e.getMessage());
			usage(jc);
		}
        if (flags.help) {
            usage(jc);
        }
        if (flags.encoding != null) {
            encoding = flags.encoding;
        }
        if (flags.jslint != null) {
            setJSLint(flags.jslint);
        }
        setResultFormatter(flags.report);
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

    private void setResultFormatter(String reportType) {
        if (reportType == null || reportType.equals("")) {
            // The original CLI behaviour: one-per-line, with prefix.
            formatter = new DefaultFormatter();
        } else if (reportType.equals("plain")) {
            formatter = new PlainFormatter();
        } else if (reportType.equals("xml")) {
            formatter = new JSLintXmlFormatter();
        } else if (reportType.equals("junit")) {
            formatter = new JUnitXmlFormatter();
        } else if (reportType.equals("report")) {
            formatter = new ReportFormatter();
        } else {
            die("unknown report type '" + reportType + "'");
        }

    }

    private void usage(JCommander jc) {
        jc.usage();
        info("using jslint version " + lint.getEdition());
        throw new DieException(null, 0);
    }

}
