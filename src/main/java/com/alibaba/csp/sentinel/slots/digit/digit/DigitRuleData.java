package com.alibaba.csp.sentinel.slots.digit.digit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ Author     ：l.yw
 * @ Date       ：Created in 14:31 2020-09-12
 * @ Modified By：
 */

public class DigitRuleData {

    DigitRule digitRule;
    /**
     * asc parse
     */
    DigitArea ascArea;
    /**
     * desc parse
     */
    DigitArea descArea;

    /**
     * filter parse
     */
    private Set<Integer> baseSet = new HashSet<>();
    List<DigitArea> baseAreaList = new ArrayList<>();

    /**
     * random parse
     */
    private Set<Integer> randomSet = new HashSet<>();

    public DigitRuleData(DigitRule digitRule) {
        this.digitRule = digitRule;
        parse();
        parseType();
    }

    private void parseType() {
        if (this.digitRule.getPassMolecule() <= this.digitRule.getPassDenominator()) {
            ascArea = new DigitArea(0, this.digitRule.getPassMolecule());
            descArea = new DigitArea(0, this.digitRule.getPassMolecule());
            return;
        }
        parseAsc();
        parseDesc();
        parseRandom();
    }


    private void parse() {
        if (this.digitRule.digitFilterFormat == null || this.digitRule.digitFilterFormat.length() == 0) {
            return;
        }
        String[] formatStrs = this.digitRule.digitFilterFormat.split(",");
        for (String formatStr : formatStrs) {
            if (formatStr.contains("-")) {
                String[] areaArr = formatStr.split("-");
                int min = Integer.parseInt(areaArr[0]);
                int max = Integer.parseInt(areaArr[1]);
                DigitArea digitArea = new DigitArea(min, max);
                baseAreaList.add(digitArea);
            } else {
                baseSet.add(Integer.parseInt(formatStr));
            }
        }
    }

    private void parseAsc() {
        if (this.digitRule.getDigitFilterType() != DigitFilterType.ASC) return;
        ascArea = new DigitArea(0, Math.max(digitRule.getPassDenominator() - 1, 0));
    }

    private void parseDesc() {
        if (this.digitRule.getDigitFilterType() != DigitFilterType.DESC) return;
        descArea = new DigitArea(digitRule.getPassDenominator(), digitRule.getPassMolecule());
    }


    private void parseRandom() {
        if (this.digitRule.getDigitFilterType() != DigitFilterType.RANDOM) return;
        // randomCollection = 可用来随机的池子
        List<Integer> randomCollection = new ArrayList<>();

        for (int i = 0; i < this.digitRule.getPassMolecule(); i++) {
            if (!filterConstant(i)) {
                randomCollection.add(i);
            }
        }

        // haveCount = 已经配置数量
        int haveCount = this.digitRule.getPassMolecule() - randomCollection.size();
        // 计算可随机的个数
        int randomCount = this.digitRule.getPassDenominator() - haveCount;
        // 不用补位//
        if (randomCount < 1) {
            return;
        }

        Collections.shuffle(randomCollection);
        randomSet = randomCollection.subList(0, randomCount).stream().collect(Collectors.toSet());
    }

    public boolean filterConstant(Integer num) {
        if (baseSet != null && baseSet.contains(num))
            return true;

        if (baseAreaList != null) {
            for (DigitArea da : baseAreaList) {
                if (da.constant(num)) return true;
            }
        }
        return false;
    }

    public boolean filter(Integer lastDigit) {
        if (this.digitRule.getDigitFilterType() == DigitFilterType.ASC) {
            return ascArea.constant(lastDigit);
        }
        if (this.digitRule.getDigitFilterType() == DigitFilterType.DESC) {
            return descArea.constant(lastDigit);
        }
        if (this.digitRule.getDigitFilterType() == DigitFilterType.RANDOM) {
            if (filterConstant(lastDigit))
                return true;
            if (randomSet != null && randomSet.contains(lastDigit))
                return true;
        }
        return false;
    }

    public static class DigitArea {
        private int min;
        private int max;

        public DigitArea(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public boolean constant(int num) {
            return min <= num && num <= max;
        }

        @Override
        public String toString() {
            return "[" + min + "-" + max + "],";
        }
    }

    @Override
    public String toString() {
        return "DigitRuleData{" +
                ", ascArea=" + ascArea +
                ", descArea=" + descArea +
                ", baseSet=" + baseSet +
                ", baseAreaList=" + baseAreaList +
                ", randomSet=" + randomSet +
                '}';
    }
}
