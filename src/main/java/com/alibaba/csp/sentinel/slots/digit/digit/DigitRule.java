package com.alibaba.csp.sentinel.slots.digit.digit;

import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import lombok.Data;

/**
 * @ Author     ：l.yw
 * @ Date       ：Created in 14:09 2020-09-12
 * @ Modified By：
 */
@Data
public class DigitRule extends AbstractRule {

    /**
     * 默认 通过率 100/100
     * passMolecule 基数（分母）
     * passDenominator可通过率 （分子）
     */
    int passMolecule = 100;
    /**
     * passDenominator可通过率 （分子）
     */
    int passDenominator = 100;

    /**
     * 尾号截取长度
     */
    int len = 3;

    /**
     * 要过滤的尾号
     * 10-30,41,51,61
     */
    String digitFilterFormat;

    /**
     * 随机补位
     * 配置的尾号不足，接受 通过率随机补位
     */
    DigitFilterType digitFilterType = DigitFilterType.ASC;

    DigitRuleData data;

    public boolean checkoutPass(String digitNumStr) {
        if (data == null) {
            data = new DigitRuleData(this);
            this.len = Math.max(parseDigitNumLen(), 1);
            System.out.println(this.toString());
        }
        String subNum = digitNumStr;
        int length = digitNumStr.length();
        if (length > len) {
            subNum = digitNumStr.substring(length - len, length);
        }

        Integer lastDigit = Integer.parseInt(subNum) % passMolecule;
        boolean filter = data.filter(lastDigit);
        System.out.println("==========================data.getDigitSet().contains(" + lastDigit + ") = " + filter);
        return !filter;

    }

    private int parseDigitNumLen() {
        int pm = passMolecule;
        int len = 0;
        while (pm > 0) {
            len += 1;
            pm = pm / 10;
        }
        return len;
    }

    public boolean isValidRule(DigitRule rule) {
        return true;
    }

    @Override
    public String toString() {
        return "DigitRule{" +
                "passMolecule=" + passMolecule +
                ", passDenominator=" + passDenominator +
                ", len=" + len +
                ", digitFilterFormat='" + digitFilterFormat + '\'' +
                ", digitFilterType=" + digitFilterType +
                ", data=" + data +
                '}';
    }
}
