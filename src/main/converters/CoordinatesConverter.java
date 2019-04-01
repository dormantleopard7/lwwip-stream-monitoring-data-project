package main.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import main.Coordinates;

public class CoordinatesConverter extends AbstractBeanField<String> {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        try {
            Coordinates result = new Coordinates(s);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
