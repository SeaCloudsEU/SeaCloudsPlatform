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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleConstraintParser {
	
	public static class SimpleConstraintElements {
	 
		private static final String[] EMPTY = new String[0];
		private static Pattern removeSpacesPattern = Pattern.compile(" *, *");
		private String left;
		private Operator operator;
		private String right;
		private double[] rightArray;
		
		public SimpleConstraintElements(String left, Operator operator, String right) {

			this.left = left;
			this.operator = operator;
			this.right = removeSpacesPattern.matcher(right).replaceAll(",");
			
			String[] aux = "".equals(this.right)? EMPTY : this.right.split(",");
			this.rightArray = new double[aux.length];

			for (int i = 0; i < rightArray.length; i++) {
				
				rightArray[i] = Double.parseDouble(aux[i]);
			}

			if (!operator.isValidRight(this.rightArray)) {
				throw new IllegalArgumentException(String.format("%s: not valid right operand for operator %s",
						Arrays.toString(this.rightArray),
						operator.toString()));
			}
//			if (operator == Operator.BETWEEN) {
//				
//				if (rightArray.length < 2) {
//				}
//			}
		}
	
	
		public String getLeft() {
			return left;
		}
	
	
		public Operator getOperator() {
			return operator;
		}
	
	
		public String getRight() {
			return right;
		}	
		
		public double[] getRightArray() {
			
			return rightArray;
		}
	}

	private final String regex = String.format("((?:\\w|.)+) +(%1$s)(?: +[(]?(%2$s(?: *, *%2$s)*)[)]?)? *",
			Operator.getAlternative(), 
			"(?:[+-]?\\d+\\.?\\d*)");
	private final Pattern re = Pattern.compile(regex);

	public SimpleConstraintParser.SimpleConstraintElements parse(String constraint) {

		SimpleConstraintParser.SimpleConstraintElements result;
		Matcher m = re.matcher(constraint);
		try {
			if (m.matches()) {
				Operator op = Operator.valueOf(m.group(2));

				String right = getRightOperandGroup(m);
				
				result = new SimpleConstraintParser.SimpleConstraintElements(
						m.group(1), op, right);
				
				return result;
			}
		}
		catch (Exception e) {
			/* continues below */
		}
		throw new IllegalArgumentException("[" + constraint + "] is not a valid constraint");
	}

	private String getRightOperandGroup(Matcher m) {
		String right;
		
		if (m.groupCount() == 3 && m.group(3) != null) {
			right = m.group(3);
		}
		else {
			right = "";
		}
		return right;
	}
}
