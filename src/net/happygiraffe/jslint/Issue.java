package net.happygiraffe.jslint;

import org.mozilla.javascript.Scriptable;

/**
 * A single issue with the code that is being checked for problems.
 * 
 * @author dom
 * @version $Id$
 */
public class Issue {

    private String a;

    private String b;

    private String c;

    private int character;

    private String d;

    private String evidence;

    private int line;

    private String raw;

    private String reason;

    private String systemId;

    /**
     * Create this issue with a systemId of "-".
     * 
     * @param err
     */
    Issue(Scriptable err) {
        this("-", err);
    }

    Issue(String systemId, Scriptable err) {
        this.systemId = systemId;
        reason = Util.stringValue("reason", err);
        line = Util.intValue("line", err);
        character = Util.intValue("character", err);
        evidence = Util.stringValue("evidence", err);
        raw = Util.stringValue("raw", err);
        a = Util.stringValue("a", err);
        b = Util.stringValue("b", err);
        c = Util.stringValue("c", err);
        d = Util.stringValue("d", err);
    }

    /**
     * @return A string of auxiliary information.
     */
    public String getA() {
        return a;
    }

    /**
     * @return A string of auxiliary information.
     */
    public String getB() {
        return b;
    }

    /**
     * @return A string of auxiliary information.
     */
    public String getC() {
        return c;
    }

    /**
     * @return the position of the issue within the line. Starts at 0.
     */
    public int getCharacter() {
        return character;
    }

    /**
     * @return a string of auxiliary information.
     */
    public String getD() {
        return d;
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
     * Provide three fields from this issue, separated by colons: line,
     * character, reason.
     */
    @Override
    public String toString() {
        return getSystemId() + ":" + getLine() + ":" + getCharacter() + ":"
                + getReason();
    }
}
