package com.googlecode.jslint4java.cli;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

/**
 * A utility for using {@link Charset} with JCommander.
 */
public class CharsetConverter implements IStringConverter<Charset> {

    public Charset convert(String value) {
        try {
            return Charset.forName(value);
        } catch (IllegalCharsetNameException e) {
            throw new ParameterException("unknown encoding '" + value + "'");
        } catch (UnsupportedCharsetException e) {
            throw new ParameterException("unknown encoding '" + value + "'");
        }
    }
}
