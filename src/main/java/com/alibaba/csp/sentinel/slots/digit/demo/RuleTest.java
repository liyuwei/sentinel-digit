package com.alibaba.csp.sentinel.slots.digit.demo;

import com.alibaba.csp.sentinel.slots.digit.digit.DigitFilterType;
import com.alibaba.csp.sentinel.slots.digit.digit.DigitRule;
import com.alibaba.csp.sentinel.slots.digit.digit.DigitRuleManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Author     ：l.yw
 * @ Date       ：Created in 11:21 2020-09-14
 * @ Modified By：
 */
public class RuleTest {

    public static final String KEY = "momodigit";

    public static void main(String[] args) {
        initRule();


    }

    public static void initRule() {
        DigitRule rule1 = new DigitRule();
        rule1.setPassMolecule(100);
        rule1.setPassDenominator(50);
        rule1.setDigitFilterType(DigitFilterType.RANDOM);
        rule1.setResource(KEY);
        rule1.build();
        System.out.println(rule1.toString());

    }
}
