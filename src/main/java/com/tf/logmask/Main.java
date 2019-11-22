package com.tf.logmask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tf.logmask.xml.ConfidentialXMLLoggerPattern.CONFIDENTIAL;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String sampleInput = "<CreditCard>5555555555554444</CreditCard><Password>HGHA%$^%%S223</Password>";
        LOG.info("{} {}", CONFIDENTIAL, sampleInput);
    }
}
