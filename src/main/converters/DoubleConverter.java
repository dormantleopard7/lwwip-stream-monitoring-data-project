package main.converters;

import com.opencsv.bean.AbstractBeanField;

/*
 * Converts given String into Java Double object.
 * Works directly for phosphate, nitrate/nitrite.
 */
public class DoubleConverter extends AbstractBeanField<String> {
    // uses SimpleDoubleConverter as base
    private SimpleDoubleConverter converter = new SimpleDoubleConverter();

    @Override
    public Object convert(String s) {
        try {
            Double value = (Double) converter.convertToRead(s);
            if (value == null) {
                // extra characters/units at end
                int index = -1;
                for (int i = 0; i < s.length(); i++) {
                    if (!Character.isDigit(s.charAt(i)) && s.charAt(i) != '.') {
                        index = i;
                        break;
                    }
                }
                value = (Double) converter.convertToRead(s.substring(0, index));
            }
            if (value == null) {
                // extra characters/units at beginning
                int index = s.length();
                for (int i = s.length() - 1; i >= 0; i--) {
                    if (!Character.isDigit(s.charAt(i)) && s.charAt(i) != '.') {
                        index = i + 1;
                        break;
                    }
                }
                value = (Double) converter.convertToRead(s.substring(index));
            }
            if (value == null) {
                // beginning and end
                int beginIndex = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (Character.isDigit(s.charAt(i))) {
                        beginIndex = i;
                        break;
                    }
                }
                int endIndex = s.length();
                for (int i = s.length() - 1; i >= 0; i--) {
                    if (Character.isDigit(s.charAt(i))) {
                        endIndex = i + 1;
                        break;
                    }
                }
                value = Double.parseDouble(s.substring(beginIndex, endIndex));
            }
            return value;
        } catch (Exception ee) {
            return null;
        }
    }
}
