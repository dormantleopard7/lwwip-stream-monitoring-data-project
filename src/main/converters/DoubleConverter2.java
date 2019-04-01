package main.converters;

import com.opencsv.bean.AbstractCsvConverter;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class DoubleConverter2 extends AbstractCsvConverter {
    private DoubleConverter converter = new DoubleConverter();

    @Override
    public Object convertToRead(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        try {
            return converter.convert(s);
        } catch (Exception e) {
            return null;
        }
    }
}
