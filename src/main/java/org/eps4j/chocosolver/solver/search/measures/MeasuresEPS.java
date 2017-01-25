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
package org.eps4j.chocosolver.solver.search.measures;

import org.chocosolver.solver.objective.IBoundsManager;
import org.chocosolver.solver.objective.ObjectiveFactory;
import org.chocosolver.solver.search.SearchState;
import org.chocosolver.solver.search.measure.IMeasures;
import org.chocosolver.solver.search.measure.Measures;

public class MeasuresEPS extends Measures {

    private static final long serialVersionUID = -3092336853879756113L;

    public MeasuresEPS(String modelName) {
        super(modelName);
    }
    
    /**
     * Copy constructor
     * which tries a deep copy of the bounds manager.
     * @param measures to copy
     */
    public MeasuresEPS(IMeasures measures) {
        super(measures);
        // Required to ensure that the source measures could be modified safely after the ctor
        this.boundsManager = ObjectiveFactory.copy(boundsManager);
    }

    public void setSearchState(SearchState state) {
        this.state = state;
    }
    
    public final void setModelName(String modelName) {
	this.modelName = modelName;
    }

    public void setBoundsManager(IBoundsManager boundsManager) {
        this.boundsManager= boundsManager;
    }
    
    public void aggregate(IBoundsManager bounds) {
	if (boundsManager.isOptimization()) {
	    //FIXME the lower bound can be local to the subproblem !
	    boundsManager.updateBestLB(bounds.getBestLB());
	    boundsManager.updateBestUB(bounds.getBestUB());
	}
    }

    public void aggregate(MeasuresEPS measures) {
        aggregate(measures.getBoundsManager());
	//TODO howto handle the search state
	//TODO howto handle objectiveOptimal 
	//objectiveOptimal |= measures.objectiveOptimal;
	solutionCount += measures.solutionCount;
	timeCount += measures.timeCount;
	readingTimeCount += measures.readingTimeCount;
	nodeCount += measures.nodeCount;
	backtrackCount += measures.backtrackCount;
	failCount += measures.failCount;
	restartCount += measures.restartCount;
	if (maxDepth < measures.maxDepth) {
	    maxDepth = measures.maxDepth;
	}
	this.depth = measures.depth;
	
    }
}
