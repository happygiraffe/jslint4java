package com.googlecode.jslint4java;

import org.mozilla.javascript.Scriptable;

/**
 * A single issue with the code that is being checked for problems.
 *
 * @author dom
 */
public class Issue {

    /**
     * Allow creating an issue in a couple of different ways.
     */
    public static class IssueBuilder {

        /** Build from a JavaScript context */
        public static Issue fromJavaScript(String systemId, Scriptable err) {
            // These used to be zero-based, but now _appear_ to be one-based.
            int line = Util.intValue("line", err);
            int col = Util.intValue("character", err);
            return new IssueBuilder(systemId, line, col, Util.stringValue("reason", err))
                    .evidence(Util.stringValue("evidence", err))
                    .raw(Util.stringValue("raw", err))
                    .build();
        }

        private final int character;
        private String evidence;
        private final int line;
        private String raw;
        private final String reason;
        private final String systemId;

        public IssueBuilder(String systemId, int line, int character, String reason) {
            this.character = character;
            this.line = line;
            this.reason = reason;
            this.systemId = systemId;
        }

        public Issue build() {
            return new Issue(this);
        }

        public IssueBuilder evidence(String evidence) {
            this.evidence = evidence;
            return this;
        }

        public IssueBuilder raw(String raw) {
            this.raw = raw;
            return this;
        }
    }

    private final int character;
    private final String evidence;
    private final int line;
    private final String raw;
    private final String reason;
    private final String systemId;

    private Issue(IssueBuilder ib) {
        systemId = ib.systemId;
        reason = ib.reason;
        line = ib.line;
        character = ib.character;
        evidence = ib.evidence;
        raw = ib.raw;
    }

    /**
     * @return the position of the issue within the line. Starts at 0.
     */
    public int getCharacter() {
        return character;
    }

    /**
     * @return the contents of the line in which this issue occurs.
     */
    public String getEvidence() {
        return evidence;
    }

    /**
     * @return the number of the line on which this issue occurs.
     */
    public int getLine() {
        return line;
    }

    /**
     * @return a copy of the evidence, but before substitution has occurred.
     */
    public String getRaw() {
        return raw;
    }

    /**
     * @return a textual description of this issue.
     */
    public String getReason() {
        return reason;
    }

    /**
     * @return the name of the file this issue occurred in.
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * Provide four fields from this issue, separated by colons: systemId, line,
     * character, reason.
     */
    @Override
    public String toString() {
        return getSystemId() + ":" + getLine() + ":" + getCharacter() + ":" + getReason();
    }
}
