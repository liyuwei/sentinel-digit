package com.alibaba.csp.sentinel.slots.digit.digit;

/**
 * @ Author     ：l.yw
 * @ Date       ：Created in 13:49 2020-09-13
 * @ Modified By：
 */
public enum DigitFilterType {
    /**
     * 默认从小到大
     */
    ASC,
    /**
     * 从大到小
     */
    DESC,
    /**
     * 生成随机数
     */
    RANDOM
}
