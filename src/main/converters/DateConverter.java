package main.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.util.Date;

public class DateConverter extends AbstractBeanField<String> {
    @Override
    public Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        try {
            Date date = new Date(s); // should use DateFormat.parse()? or Calendar.set and such?
            return date;
        } catch (Exception e) {
            try {
                // mm/dd/yy?
                Date date = new Date(s.substring(0, s.length() - 1));
                return date;
            } catch (Exception ee) {
                try {
                    // *mm/dd/yy
                    Date date = new Date(s.substring(1));
                    return date;
                } catch (Exception eee) {
                    // others are not interpreted
                    return null;
                }
            }
        }
    }
}
