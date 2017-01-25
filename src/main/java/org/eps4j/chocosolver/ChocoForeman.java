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
package org.eps4j.chocosolver;

import java.util.logging.Level;

import org.chocosolver.pf4cs.SetUpException;
import org.chocosolver.solver.objective.IBoundsManager;
import org.chocosolver.solver.search.SearchState;
import org.chocosolver.solver.search.limits.ICounter;
import org.chocosolver.solver.search.limits.SolutionCounter;
import org.chocosolver.util.criteria.Criterion;
import org.eps4j.EPSComm;
import org.eps4j.chocosolver.solver.search.measures.MeasuresEPS;
import org.eps4j.exception.EPSRuntimeException;
import org.eps4j.specs.actors.IForemanEPS;
import org.kohsuke.args4j.Option;


public class ChocoForeman implements IForemanEPS<ChocoBetter, ChocoJob> {

    @Option(name = "-s", aliases = {"--solution"}, usage = "Solution limit.")
    private int solutionLimit = 0;

    private MeasuresEPS measures;

    private Criterion stopCriterion = ICounter.Impl.None;

    public ChocoForeman() {
        super();
    }

    @Override
    public void setUp(String... args) throws SetUpException {
        ChocoFactory.parseArguments(this, args);        
    }

    @Override
    public boolean hasEnded() {
        //EPSComm.LOGGER.severe("ERROR " + measures );   	
        //TODO avoid the non-null condition
        return measures != null && measures.getSearchState() != SearchState.RUNNING;
    }

    private void updateOptimization() {
        final IBoundsManager bm = measures.getBoundsManager();
        if(bm.isOptimization()) {
            if (measures.getSolutionCount() > 0 &&  
                    bm.getBestLB().equals(bm.getBestUB())) {
                measures.setSearchState(SearchState.TERMINATED);
            }
        }
    }

    private void updateCriterion() {
        if (stopCriterion.isMet()) {
            measures.setSearchState(SearchState.STOPPED);
        }
    }

    private void recordBounds(IBoundsManager bounds) {
        if (this.measures == null) {
            //FIXME create only if optimization problem
            this.measures = new MeasuresEPS("Foreman");
            this.measures.setBoundsManager(bounds);
            this.measures.setSearchState(SearchState.RUNNING);
            if (solutionLimit > 0) {
                stopCriterion = new SolutionCounter(measures, solutionLimit);
            }
        } else {
            this.measures.aggregate(bounds);
        }
        updateOptimization();
    }

    
    private void recordMeasures(MeasuresEPS measures) {
        if (this.measures == null) {
            this.measures = new MeasuresEPS(measures);
            this.measures.setSearchState(SearchState.RUNNING);
            if (solutionLimit > 0) {
                stopCriterion = new SolutionCounter(this.measures, solutionLimit);
            }
        } else {
            this.measures.aggregate(measures);
        }
        // Beware : do not change the order
        updateCriterion();
        updateOptimization();
    }

    private boolean recordSearchState(ChocoBetter better) {
        switch (better.getSearchState()) {
        case STOPPED: return false;
        case KILLED: {
            measures.setSearchState(SearchState.KILLED);       
            return false;
        }
        case TERMINATED: return true;
        default: {
            EPSComm.LOGGER.warning("Job has not begun or is still running.\n"+better.toString());
            throw new EPSRuntimeException("Job has not begun or is still running.");
        }
        }
    }

    @Override
    public boolean recordCollect(ChocoJob job, ChocoBetter better) {
        recordMeasures(better.getMeasures());
        return recordSearchState(better);
    }

    @Override
    public void recordCollect(ChocoBetter better) {
        recordMeasures(better.getMeasures());
        recordSearchState(better);
    }

    @Override
    public void recordBetter(ChocoBetter better) {
        recordBounds(better.getBounds());
    }

    @Override
    public void recordBetter(ChocoJob job, ChocoBetter better) {
        recordBounds(better.getBounds());
    }

    @Override
    public ChocoBetter getBetter() {
        return new ChocoBetter(null, measures);
    }

    @Override
    public void tearDown() {
        if (measures == null) {
            EPSComm.LOGGER.severe("No Foreman Measures.");
        } else if (EPSComm.LOGGER.isLoggable(Level.INFO)) {
            if(measures.getSearchState() == SearchState.RUNNING) {
                measures.setSearchState(SearchState.TERMINATED);
            }
            EPSComm.LOGGER.info("\n"+measures.toDimacsString());
        }
    }
}