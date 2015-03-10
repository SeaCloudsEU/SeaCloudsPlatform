/**
 * Copyright 2015 SeaClouds
 * Contact: SeaClouds
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
package eu.atos.sla.evaluation.constraint.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.atos.sla.evaluation.constraint.simple.Operator;

public class OperatorTest  {

	/**
	 * Calculates three comparisons to test an operator, where the operands are:
	 * op1 = op2
	 * op1 > op2
	 * op1 < op2
	 * 
	 * @param operator Operator to test
	 * @param assert1 the operator should return this for the first set of operands (i.e. if
	 *   assert1 is true, the operator should be EQ, GE or LE)
	 * @param assert2
	 * @param assert3
	 */
	public void testOperator(Operator operator, boolean assert1, boolean assert2, boolean assert3) {
		
		double[] _0 = new double[] { 0 };
		double[] _1 = new double[] { 1 };
		
		assertEquals(operator.eval(0, _0), assert1);
		assertEquals(operator.eval(1, _0), assert2);
		assertEquals(operator.eval(0, _1), assert3);
	}
	
	@Test
	public void testEval() {
		
		/*
		 * Test simple operators
		 */
		testOperator(Operator.GT, false, true,  false);
		testOperator(Operator.GE, true,  true,  false);
		testOperator(Operator.EQ, true,  false, false);
		testOperator(Operator.LT, false, false, true);
		testOperator(Operator.LE, true,  false, true);
		testOperator(Operator.NE, false, true,  true);
		
		double[] _0_1 = new double[] { 0d, 1d };

		/*
		 * Test between
		 */
		assertTrue(Operator.BETWEEN.eval(0, _0_1));
		assertTrue(Operator.BETWEEN.eval(1, _0_1));
		assertTrue(Operator.BETWEEN.eval(0.5, _0_1));
		assertFalse(Operator.BETWEEN.eval(-0.1, _0_1));
		assertFalse(Operator.BETWEEN.eval(1.1, _0_1));
		
		/*
		 * Test in
		 */
		assertTrue(Operator.IN.eval(0, _0_1));
		assertTrue(Operator.IN.eval(1, _0_1));
		assertFalse(Operator.IN.eval(2, _0_1));
		
		/*
		 * Tests exists (right operand is don't care)
		 */
		assertTrue(Operator.EXISTS.eval(0, new double[] {}));
		
		/*
		 * Tests not exists (right operand is don't care)
		 */
		assertFalse(Operator.NOT_EXISTS.eval(0, new double[] {}));
	}

}
