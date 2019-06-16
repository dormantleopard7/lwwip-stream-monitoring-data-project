package main.converters;

import com.opencsv.bean.AbstractBeanField;

import java.util.Date;

/*
 * Converts given String into a java.util.Date object.
 * Meant to be used with 'Date' column; example String: 5/12/06 .
 */
public class DateConverter extends AbstractBeanField<String> {
    @Override
    public Object convert(String s) {
        try {
            // ordinary Date: mm/dd/yy
            Date date = new Date(s);
            // deprecated, but DateFormat.parse()? or Calendar.set and such do not work as well
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
