/**
 * Copyright 2014 Atos
 * Contact: Atos <roman.sosa@atos.net>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.atos.sla.evaluation.guarantee;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.ICompensation;
import eu.atos.sla.datamodel.ICompensation.IPenalty;
import eu.atos.sla.datamodel.ICompensationDefinition.IPenaltyDefinition;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.PenaltyDefinition;
import eu.atos.sla.datamodel.bean.Violation;
import eu.atos.sla.enforcement.TestAgreementFactory;

public class SimpleBusinessValuesEvaluatorTest {

    private static final Date _0 = new Date(0);
    private static final Date _2 = new Date(2000);
    private static final Date _3 = new Date(3000);
    private static final Date _5 = new Date(5000);
    
//    private final static Date SECOND = new Date(1000);
//    private final static Date MINUTE = new Date(60 * SECOND.getTime());
//    private final static Date HOUR = new Date(60 * MINUTE.getTime());

    private int[] times = new int[] {
            0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0
    };
    private IViolation[] violations;
    IAgreement contract;
    
    public SimpleBusinessValuesEvaluatorTest() {
    }

    @Before
    public void setUp() throws Exception {
        
        String kpiName = "LATENCY";
        String constraint = kpiName + " LT 100";

        contract = TestAgreementFactory.newAgreement(
            Arrays.asList(
                TestAgreementFactory.newGuaranteeTerm(
                    kpiName, 
                    constraint,
                    Arrays.<IPenaltyDefinition>asList(
                        new PenaltyDefinition(1, _0, "discount", "euro", "10", "P1D"),
                        new PenaltyDefinition(2, _2, "terminate", "", "", ""),
                        new PenaltyDefinition(2, _3, "discount", "euro", "10", ""),
                        new PenaltyDefinition(3, _3, "discount", "euro", "10", ""),
                        new PenaltyDefinition(3, _5, "discount", "euro", "10", "")
                    )
                )
            )
        );

        violations = new IViolation[times.length];
        for (int i = 0; i < violations.length; i++) {
            if (times[i] != 0) {
                IViolation violation = newViolation(i);
                violations[i] = violation;
            }
        }
    }

    private IViolation newViolation(int time) {
        Date datetime = new Date(1000 * time);
        IViolation violation = new Violation(contract, contract.getGuaranteeTerms().get(0), null, "", "", datetime);
        return violation;
    }
    
    @Test
    public void testEvaluate() {
        
        SimpleBusinessValuesEvaluator bvEval = new SimpleBusinessValuesEvaluator();
        bvEval.setCompensationRepository(new DummyViolationRepository(Arrays.asList(violations)));
        IGuaranteeTerm term = contract.getGuaranteeTerms().get(0);
        
        List<? extends ICompensation> compensations;
        
        for (int i = 0; i < times.length; i++) {
            Date now = new Date(i * 1000);
            
            List<IViolation> newViolations = (times[i] != 0)?
                Collections.singletonList(TestAgreementFactory.newViolation(contract, term, null, now)) :
                Collections.<IViolation>emptyList();

            compensations = bvEval.evaluate(
                contract, 
                term, 
                newViolations,
                now
            );

            for (IPenaltyDefinition def : contract.getGuaranteeTerms().get(0).getBusinessValueList().getPenalties()) {
                int count = def.getCount();
                int step = (int)def.getTimeInterval().getTime() / 1000;
                
                boolean expectedPenalty = sum(times, i - step, i) >= count;
                boolean actualPenalty = foundPenaltyDefinition(def, compensations);
                
                if (expectedPenalty != actualPenalty) {
                    assertEquals(expectedPenalty, actualPenalty);
                }
                assertEquals(expectedPenalty, actualPenalty);
            }
        }
    }
    
    private int sum(int[] arr, int begin, int end) {
        int result = 0;
        if (begin < 0) {
            begin = 0;
        }
        for (int i = begin; i <= end; i++) {
            result += arr[i];
        }
        return result;
    }
    
    private boolean foundPenaltyDefinition(IPenaltyDefinition def, List<? extends ICompensation> compensations) {
        for (ICompensation c : compensations) {
            if (c instanceof IPenalty) {
                IPenalty p = (IPenalty) c;
                if (p.getDefinition().equals(def)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static class DummyViolationRepository implements IViolationRepository {
        
        List<IViolation> violations = new ArrayList<IViolation>();
        
        public DummyViolationRepository(List<IViolation> violations) {
            this.violations.addAll(violations);
        }

        @Override
        public List<IViolation> getViolationsByTimeRange(IAgreement agreement,
                String guaranteeTermName, Date begin, Date end) {

            List<IViolation> result = new ArrayList<IViolation>();
            for (IViolation violation: violations) {
                if (violation == null) {
                    continue;
                }
                Date date = violation.getDatetime();
                if (begin.compareTo(date) <= 0 && end.compareTo(date) > 0) {
                    result.add(violation);
                }
            }
            return result;
        }
    }
}
