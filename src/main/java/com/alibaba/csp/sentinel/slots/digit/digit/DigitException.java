package com.alibaba.csp.sentinel.slots.digit.digit;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @ Author     ：l.yw
 * @ Date       ：Created in 14:10 2020-09-12
 * @ Modified By：
 */
public class DigitException extends BlockException {

    public DigitException(String ruleLimitApp) {
        super(ruleLimitApp);
    }

    public DigitException(String limitApp, DigitRule rule) {
        super(limitApp, rule);
    }

    @Override
    public DigitRule getRule() {
        return rule.as(DigitRule.class);
    }
}
