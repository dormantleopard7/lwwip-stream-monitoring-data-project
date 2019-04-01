package main.converters;

import com.opencsv.bean.AbstractCsvConverter;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

// !!! Might want to rethink design with these converters
//  maybe have this one do what DoubleConverter does, then have Temp extend this one ???

// should work with conductivity, pH, DO
public class SimpleDoubleConverter extends AbstractCsvConverter {
    @Override
    public Object convertToRead(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        try {
            // nice numeric
            Double value = Double.parseDouble(s);
            return value;
        } catch (Exception e) {
            return null;
        }
    }
}
