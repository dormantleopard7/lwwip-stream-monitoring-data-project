package main.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class UnitsTextConverter extends AbstractBeanField<String> {
    @Override
    public Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        try {
            if (s.isEmpty() || s.equals("?") || s.charAt(0) == '(') {
                return null;
            }
            if (s.charAt(s.length() - 1) == '?') {
                return s.substring(0, s.length() - 1);
            }
            if (s.equals("JTU/NTU")) {
                return "JTU";
            }
            if (s.equals("/min")) {
                return "rpm";
            }
            return s;
        } catch (Exception e) {
            return null;
        }
    }
}
