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

enum Operator { 
	
	GT(new IEvaluator() {
		@Override
		public boolean eval(double left, double[] right) {
			return left > right[0];
		}
		@Override
		public boolean isValidRight(double[] right) {
			return right.length == 1;
		}
	}), 

	LT(new IEvaluator() {
		@Override
		public boolean eval(double left, double[] right) {
			return left < right[0];
		}
		@Override
		public boolean isValidRight(double[] right) {
			return right.length == 1;
		}
	}), 
	
	EQ(new IEvaluator() {
		@Override
		public boolean eval(double left, double[] right) {
			return left == right[0];
		}
		@Override
		public boolean isValidRight(double[] right) {
			return right.length == 1;
		}
	}),
	
	GE(new IEvaluator() {
		@Override
		public boolean eval(double left, double[] right) {
			return left >= right[0];
		}
		@Override
		public boolean isValidRight(double[] right) {
			return right.length == 1;
		}
	}),
	
	LE(new IEvaluator() {
		@Override
		public boolean eval(double left, double[] right) {
			return left <= right[0];
		}
		@Override
		public boolean isValidRight(double[] right) {
			return right.length == 1;
		}
	}), 
	
	NE(new IEvaluator() {
		@Override
		public boolean eval(double left, double[] right) {
			return left != right[0];
		}
		@Override
		public boolean isValidRight(double[] right) {
			return right.length == 1;
		}
	}),
	
	BETWEEN(new IEvaluator() {
		
		@Override
		public boolean eval(double left, double[] right) {

			return left >= right[0] && left <= right[1];
		}
		@Override
		public boolean isValidRight(double[] right) {
			return right.length == 2;
		}
	}),
	
	IN(new IEvaluator() {
		@Override
		public boolean eval(double left, double[] right) {
	
			for (double item : right) {
				if (left == item) {
					return true;
				}
			}
			return false;
		}
		@Override
		public boolean isValidRight(double[] right) {
			return right.length > 0;
		}
	}),
	
	EXISTS(new IEvaluator() {
		@Override
		public boolean eval(double left, double[] right) {
			return true;
		}
		@Override
		public boolean isValidRight(double[] right) {
			return right.length == 0;
		}
	}),
	
	NOT_EXISTS(new IEvaluator() {
		
		@Override
		public boolean eval(double left, double[] right) {
			return false;
		}
		@Override
		public boolean isValidRight(double[] right) {
			return right.length == 0;
		}
	})
	; 

	private static final String alternative;
	private IEvaluator evaluator;

	
	static {
	
		StringBuffer str = new StringBuffer();

		String sep = "";
		for (Operator op : Operator.values()) {
			str.append(sep);
			str.append(op.name());
			sep = "|";
		}
		alternative = str.toString();
	}
	
	private Operator(IEvaluator evaluator) {

		this.evaluator = evaluator;
	}
	
	public static String getAlternative() {
		
		return alternative;
	}
	
	public boolean eval(double left, double[] right) {
		
		return this.evaluator.eval(left, right);
	}

	public boolean isValidRight(double[] right) {

		return this.evaluator.isValidRight(right);
	}
	
	interface IEvaluator {
		boolean eval(double left, double[] right);
		boolean isValidRight(double[] right);
	}
		
}