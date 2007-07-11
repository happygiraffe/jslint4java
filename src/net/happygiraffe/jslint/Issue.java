package net.happygiraffe.jslint;

import org.mozilla.javascript.Scriptable;

public class Issue {

    String a;

    String b;

    String c;

    int character;

    String d;

    String evidence;

    int line;

    String raw;

    String reason;

    Issue(Scriptable err) {
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

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public String getC() {
        return c;
    }

    public int getCharacter() {
        return character;
    }

    public String getD() {
        return d;
    }

    public String getEvidence() {
        return evidence;
    }

    public int getLine() {
        return line;
    }

    public String getRaw() {
        return raw;
    }

    public String getReason() {
        return reason;
    }


}
