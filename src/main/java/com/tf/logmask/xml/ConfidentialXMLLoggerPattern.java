package com.tf.logmask.xml;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;
import static org.apache.commons.lang3.StringUtils.repeat;

public class ConfidentialXMLLoggerPattern extends PatternLayout {
    public static final String CONFIDENTIAL = "CONFIDENTIAL";
    private static final String CARD_REPLACE_REGEX = "\\b([0-9]{6})[0-9]{0,9}([0-9]{4})\\b";
    private static final String CARD_REGEX = "^(?:(?<visa>4[0-9]{12}(?:[0-9]{3})?)|" +
            "(?<mastercard>5[1-5][0-9]{14})|" +
            "(?<discover>6(?:011|5[0-9]{2})[0-9]{12})|" +
            "(?<amex>3[47][0-9]{13})|" +
            "(?<diners>3(?:0[0-5]|[68][0-9])?[0-9]{11})|" +
            "(?<jcb>(?:2131|1800|35[0-9]{3})[0-9]{11}))$";

    private static final String ANY_CHAR_REPLACE_REGEX = ".";
    private String[] maskFields;
    private String maskingSymbol;
    private String cardMaskSymbol = "$1%s$2";

    public String doLayout(ILoggingEvent event) {
        if (!isStarted()) {
            return EMPTY_STRING;
        }
        String defaultMessage = writeLoopOnConverters(event);
        return defaultMessage.contains(CONFIDENTIAL) ? mask(defaultMessage) : defaultMessage;
    }

    private String mask(String val) {
        StringBuilder maskedVal = new StringBuilder(val);
        for (String field : maskFields) {
            if (maskedVal.indexOf(field) != -1) {
                int valStartIndex = maskedVal.indexOf(field) + field.length() + 1;
                int valEndIndex = maskedVal.lastIndexOf(field) - 2;
                String value = maskedVal.substring(valStartIndex, valEndIndex).split("=")[0];
                if (value.matches(CARD_REGEX)) {
                    this.cardMaskSymbol = String.format(cardMaskSymbol, repeat(maskingSymbol, value.length() - 10));
                    maskedVal.replace(valStartIndex, valEndIndex, value.replaceAll(CARD_REPLACE_REGEX, cardMaskSymbol));
                } else
                    maskedVal.replace(valStartIndex, valEndIndex, value.replaceAll(ANY_CHAR_REPLACE_REGEX, maskingSymbol));
            }
        }
        return maskedVal.toString();
    }

    public void setMaskFields(String fields) {
        this.maskFields = fields.split(",");
    }

    public void setMaskingSymbol(String maskingSymbol) {
        this.maskingSymbol = maskingSymbol;
    }
}
