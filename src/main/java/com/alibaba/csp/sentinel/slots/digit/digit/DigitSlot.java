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

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.spi.SpiOrder;
import com.alibaba.csp.sentinel.util.function.Function;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @ Author     ：l.yw
 * @ Date       ：Created in 14:06 2020-09-12
 * @ Modified By：
 */
@SpiOrder(-4000)
public class DigitSlot extends AbstractLinkedProcessorSlot<DefaultNode> {

    @Override
    public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode node, int count,
                      boolean prioritized, Object... args) throws Throwable {

        performChecking(resourceWrapper, args);

        fireEntry(context, resourceWrapper, node, count, prioritized, args);
    }

    void performChecking(ResourceWrapper r, Object... args) throws BlockException {
        if (args == null || args.length != 1) return;

        //
        Collection<DigitRule> digitRules = ruleProvider.apply(r.getName());

        if (digitRules == null || digitRules.isEmpty()) {
            return;
        }
        String numStr = args[0].toString();
        for (DigitRule rule : digitRules) {
            if (!rule.checkoutPass(numStr)) {
                throw new DigitException(r.getName());
            }
        }
    }

    @Override
    public void exit(Context context, ResourceWrapper r, int count, Object... args) {
//        System.out.println("DegradeSlot exit | in");
        Entry curEntry = context.getCurEntry();
        if (curEntry.getBlockError() != null) {
            fireExit(context, r, count, args);
            return;
        }
        fireExit(context, r, count, args);
//        System.out.println("DegradeSlot exit | out");
    }


    private final Function<String, Collection<DigitRule>> ruleProvider = new Function<String, Collection<DigitRule>>() {
        @Override
        public Collection<DigitRule> apply(String resource) {
            // Flow rule map should not be null.
            Map<String, List<DigitRule>> digitRules = DigitRuleManager.getDigitRules();
            return digitRules.get(resource);
        }
    };
}
