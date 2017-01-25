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
package org.eps4j.chocosolver.solver.search.loop;

import java.util.HashSet;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.decision.DecisionPath;
import org.chocosolver.solver.variables.Variable;

public class DecomposeInst implements Decompose {

	public final int depth;
	
	public DecomposeInst(int depth) {
		super();
		this.depth = depth;
	}

	@Override
	public boolean sendNode(Solver solver) {
	    final HashSet<Variable> instVars = new HashSet<>();
	    final DecisionPath decPath = solver.getDecisionPath();
	    final int n = decPath.size();
	    for (int i = 1; i < n; i++) {
                final Variable var = decPath.getDecision(i).getDecisionVariable();
                if(var.isInstantiated()) {
                    instVars.add(var);
                }
            }
	    return instVars.size() >=depth;
	}
}