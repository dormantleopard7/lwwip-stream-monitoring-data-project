package main.converters;

import com.opencsv.bean.AbstractCsvConverter;

/*
 * Converts given String into a Java Double.
 * Works well with conductivity and pH.
 * Basically, if numeric, converts to Double, otherwise null.
 */

public class SimpleDoubleConverter extends AbstractCsvConverter {
    @Override
    public Object convertToRead(String s) {
        try {
            // nice numeric
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }
}
