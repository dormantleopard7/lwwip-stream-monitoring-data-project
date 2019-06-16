package main.converters;

import com.opencsv.bean.AbstractCsvConverter;

/*
 * Converts the given String into a Java Double.
 * Basically is the AbstractCsvConverter version of DoubleConverter.
 * Works well for turbidity, DO, and flow.
 */

public class DoubleConverter2 extends AbstractCsvConverter {
    // basically just the same as a DoubleConverter
    private DoubleConverter converter = new DoubleConverter();

    @Override
    public Object convertToRead(String s) {
        try {
            return converter.convert(s);
        } catch (Exception e) {
            return null;
        }
    }
}
