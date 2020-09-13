/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.slots.digit.digit;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.property.DynamicSentinelProperty;
import com.alibaba.csp.sentinel.property.PropertyListener;
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * One resources can have multiple rules. And these rules take effects in the following order:
 * <ol>
 * <li>requests from specified caller</li>
 * <li>no specified caller</li>
 * </ol>
 * </p>
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class DigitRuleManager {

    private static volatile Map<String, List<DigitRule>> flowRules = new ConcurrentHashMap<String, List<DigitRule>>();

    private static final DigitPropertyListener LISTENER = new DigitPropertyListener();
    private static SentinelProperty<List<DigitRule>> currentProperty = new DynamicSentinelProperty<List<DigitRule>>();


    static {
        currentProperty.addListener(LISTENER);
        fixBug();
    }

    public static final String KEY = "momodigit";

    // TODO bug unfix
    private static void fixBug() {
        List<DigitRule> rules = new ArrayList<>();
        DigitRule rule1 = new DigitRule();
        rule1.setPassMolecule(100);
        rule1.setPassDenominator(50);
        rule1.setDigitFilterType(DigitFilterType.RANDOM);
        rule1.setResource(KEY);
        rules.add(rule1);
        DigitRuleManager.loadRules(rules);
    }

    /**
     * Listen to the {@link SentinelProperty} for {@link DigitRule}s. The property is the source of {@link DigitRule}s.
     * Digit rules can also be set by {@link #loadRules(List)} directly.
     *
     * @param property the property to listen.
     */
    public static void register2Property(SentinelProperty<List<DigitRule>> property) {
        AssertUtil.notNull(property, "property cannot be null");
        synchronized (LISTENER) {
            RecordLog.info("[DigitRuleManager] Registering new property to flow rule manager");
            currentProperty.removeListener(LISTENER);
            property.addListener(LISTENER);
            currentProperty = property;
        }
    }

    /**
     * Get a copy of the rules.
     *
     * @return a new copy of the rules.
     */
    public static List<DigitRule> getRules() {
        List<DigitRule> rules = new ArrayList<DigitRule>();
        for (Map.Entry<String, List<DigitRule>> entry : flowRules.entrySet()) {
            rules.addAll(entry.getValue());
        }
        return rules;
    }

    /**
     * Load {@link DigitRule}s, former rules will be replaced.
     *
     * @param rules new rules to load.
     */
    public static void loadRules(List<DigitRule> rules) {
        currentProperty.updateValue(rules);
    }

    static Map<String, List<DigitRule>> getDigitRuleMap() {
        return flowRules;
    }


    public static boolean hasConfig(String resource) {
        return flowRules.containsKey(resource);
    }

    public static boolean isOtherOrigin(String origin, String resourceName) {
        if (StringUtil.isEmpty(origin)) {
            return false;
        }

        List<DigitRule> rules = flowRules.get(resourceName);

        if (rules != null) {
            for (DigitRule rule : rules) {
                if (origin.equals(rule.getLimitApp())) {
                    return false;
                }
            }
        }

        return true;
    }

    private static final class DigitPropertyListener implements PropertyListener<List<DigitRule>> {

        @Override
        public void configUpdate(List<DigitRule> value) {
            Map<String, List<DigitRule>> rules = buildDigitRuleMap(value);
            if (rules != null) {
                flowRules.clear();
                flowRules.putAll(rules);
            }
            RecordLog.info("[DigitRuleManager] Digit rules received: " + flowRules);
        }

        @Override
        public void configLoad(List<DigitRule> conf) {
            Map<String, List<DigitRule>> rules = buildDigitRuleMap(conf);
            if (rules != null) {
                flowRules.clear();
                flowRules.putAll(rules);
            }
            RecordLog.info("[DigitRuleManager] Digit rules loaded: " + flowRules);
        }
    }


    public static Map<String, List<DigitRule>> buildDigitRuleMap(List<DigitRule> list) {
        System.out.println("buildDigitRuleMap = " + (list == null ? "null" : list.size()));
        Map<String, List<DigitRule>> newRuleMap = new ConcurrentHashMap<>();
        if (list == null || list.isEmpty()) {
            return newRuleMap;
        }
        Map<String, Set<DigitRule>> tmpMap = new ConcurrentHashMap<>();

        for (DigitRule rule : list) {
            if (!rule.isValidRule(rule)) {
                RecordLog.warn("[DigitRuleManager] Ignoring invalid flow rule when loading new flow rules: " + rule);
                continue;
            }

            if (StringUtil.isBlank(rule.getLimitApp())) {
                rule.setLimitApp(RuleConstant.LIMIT_APP_DEFAULT);
            }
            String key = rule.getResource();
            Set<DigitRule> flowRules = tmpMap.get(key);

            if (flowRules == null) {
                // Use hash set here to remove duplicate rules.
                flowRules = new HashSet<>();
                tmpMap.put(key, flowRules);
            }

            flowRules.add(rule);
        }
        for (Map.Entry<String, Set<DigitRule>> entries : tmpMap.entrySet()) {
            List<DigitRule> rules = new ArrayList<>(entries.getValue());
            newRuleMap.put(entries.getKey(), rules);
        }
        System.out.println("buildDigitRuleMap = " + list == null ? "null" : list.size());

        return newRuleMap;
    }


}
