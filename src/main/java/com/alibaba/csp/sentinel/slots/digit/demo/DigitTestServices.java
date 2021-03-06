package com.alibaba.csp.sentinel.slots.digit.demo;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.digit.digit.DigitFilterType;
import com.alibaba.csp.sentinel.slots.digit.digit.DigitRuleManager;
import com.alibaba.csp.sentinel.slots.digit.digit.DigitRule;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @ Author     ：l.yw
 * @ Date       ：Created in 10:50 2020-09-04
 * @ Modified By：
 */
@Component
public class DigitTestServices {

    public static final String KEY = "momodigit";

    // TODO bug unfix
    @PostConstruct
    public static void fixBug() {
        List<DigitRule> rules = new ArrayList<>();
        DigitRule rule1 = new DigitRule();
        rule1.setPassMolecule(100);
        rule1.setPassDenominator(50);
        rule1.setDigitFilterType(DigitFilterType.RANDOM);
        rule1.setResource(KEY);
        rules.add(rule1);
        DigitRuleManager.loadRules(rules);
    }

    @Scheduled(cron = "*/1 * * * * ?")
    private void timerFor() throws BlockException {
        System.out.println("timerFor momodigitnum ");
        momodigitnum(KEY);
    }

    private void momodigitnum(String KEY) {
//        for (int i = 0; i < 100; i++) {
        Entry entry = null;
        try {
            entry = SphU.entry(KEY, EntryType.OUT, 1, 123456);
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName());
            e.printStackTrace();
        }
//        }
    }

}
