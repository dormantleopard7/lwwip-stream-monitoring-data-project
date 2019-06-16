package main.converters;

import com.opencsv.bean.AbstractBeanField;

/*
 * Converts the given String into appropriate units.
 * Meant to be used for turbidity and flow units.
 */
public class UnitsTextConverter extends AbstractBeanField<String> {
    @Override
    public Object convert(String s) {
        try {
            if (s.isEmpty() || s.equals("?") || s.charAt(0) == '(') {
                return null;
            }
            if (s.charAt(s.length() - 1) == '?') {
                return s.substring(0, s.length() - 1);
            }
            // specific turbidity case
            if (s.equals("JTU/NTU")) {
                return "JTU";
            }
            // specific flow case
            if (s.equals("/min")) {
                return "rpm";
            }
            // otherwise units are whatever is given
            // (will be fixed later though)
            return s;
        } catch (Exception e) {
            return null;
        }
    }
}
