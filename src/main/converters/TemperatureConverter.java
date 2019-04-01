package main.converters;

import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

// !!! redesign ???

// should work with air and water temp
public class TemperatureConverter extends SimpleDoubleConverter {
    DoubleConverter converter = new DoubleConverter();

    @Override
    public Object convertToRead(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        try {
            if (s.endsWith("F") || s.endsWith("Fahrenheit")) { // might add something with °
                // fahrenheit value
                Double value = Double.parseDouble(s.substring(0, s.indexOf("F")));
                return fahrenheitToCelsius(value);
            }
            return converter.convert(s);
        } catch (Exception e) {
            return null;
        }
    }

    public Double fahrenheitToCelsius(Double f) {
        return (f - 32.0) * (5.0 / 9.0);
    }
}
