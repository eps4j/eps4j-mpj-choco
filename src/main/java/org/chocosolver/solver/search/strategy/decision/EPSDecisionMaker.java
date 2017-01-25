/**
 * This file is part of eps4j-choco, http://github.com/eps4j/eps4j-choco
 *
 * Copyright (c) 2017, Arnaud Malapert, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
/**
*  This file is part of eps4j.
*
*  Copyright (c) 2017, Arnaud Malapert (Université Côte d’Azur, CNRS, I3S, France)
*  All rights reserved.
*
*  This software may be modified and distributed under the terms
*  of the BSD license.  See the LICENSE file for details.
 */
package org.chocosolver.solver.search.strategy.decision;

import java.io.Serializable;

import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.variables.Variable;

/**
 *
 * @author Charles Prud'homme
 * @since 12/05/2016.
 */
public class EPSDecisionMaker{
	
	public static class DecisionEPS<E extends Variable> implements Serializable {
		
		private static final long serialVersionUID = -9119319581843507014L;

		public final Decision<E> decision;
		
		public final String varname;

		public DecisionEPS(Decision<E> decision) {
			super();
			this.decision = decision;	
			this.varname = decision.getDecisionVariable().getName();
		}

		public final Decision<E> getDecision() {
			return decision;
		}

		public final String getVarname() {
			return varname;
		}
		
		/**
		 * Set the (protected) decision variable and apply the decision.
		 * @param var 
		 * @throws ContradictionException
		 */
		public final void apply(Variable var) throws ContradictionException {
			//TODO use a generic method
			this.decision.var = (E) var;
			this.decision.apply();
		}

		@Override
		public String toString() {
			return "DecisionEPS [varname=" + varname + ", value=" + decision.getDecisionValue()+ ", branch=" + decision.branch + "]";
		}
		
		
		
		
	}
    
    public static <E extends Variable> DecisionEPS<E> makeDecisionEPS(Decision<E> d) {
        return new DecisionEPS<E>(d);
    }
}
