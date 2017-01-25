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

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import org.chocosolver.pf4cs.IProblem;
import org.chocosolver.pf4cs.SetUpException;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.SearchState;
import org.chocosolver.solver.search.limits.ACounter;
import org.chocosolver.solver.search.limits.NodeCounter;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.criteria.Criterion;
import org.eps4j.EPSComm;
import org.eps4j.exception.EPSRuntimeException;
import org.eps4j.specs.actors.IWorkerEPS;
import org.kohsuke.args4j.Option;

public class ChocoWorker implements IWorkerEPS<ChocoJob, ChocoBetter> {

    //FIXME ctor argument
    @Option(name = "-hf", aliases = {"--helpfreq"}, usage = "frequency of help requests.")
    private int helpEveryXXNodes = 0;
    
    public final IProblem<Model> problem;

    public final HashMap<String, Variable> varsByNames = new HashMap<String, Variable>();

    private ChocoBetter better;

    private ACounter needHelp;

    public ChocoWorker(final IProblem<Model> problem) {
        super();
        this.problem = problem;
        // problem.setUp(args) is already done
    }

    @Override
    public void setUp(String... args) throws SetUpException {
        ChocoFactory.parseArguments(this, args); 
        problem.buildModel();
        problem.configureSearch();
        //System.out.println(Arrays.toString(problem.getModel().getVars()));
        initialPropagation();
        createVarMap();
    }

    @Override
    public void tearDown() {
    }

    private void initialPropagation() throws SetUpException {
        final Model m = problem.getModel();
        final Solver s = m.getSolver();
        if (helpEveryXXNodes > 0) {
            needHelp = new NodeCounter(m, helpEveryXXNodes);
            s.addStopCriterion(new HelpStopCriterion());
        }
        // store state before initial propagation; w = 0 -> 1
        s.getEnvironment().worldPush();  
        // initial propagation
        try {
            s.propagate(); 
        } catch (ContradictionException e) {
            //TODO Handle gracefully trivial unsat instances
            EPSComm.LOGGER.log(Level.WARNING, "<{0}> model is trivially unsat (infeasible at the root node).", EPSComm.rank());
            
            throw new SetUpException("model is infeasible at the root node",e);
        }
        // store state after initial propagation; w = 1 -> 2
        s.getEnvironment().worldPush();  
    }
    
    private void createVarMap() {
        varsByNames.clear();
        final Model model = problem.getModel();
        final int n = model.getNbVars();
        for (int i = 0; i < n; i++) {
            final Variable var = model.getVar(i);
            final String varname = var.getName();
            if (varsByNames.containsKey(varname)) {
                throw new EPSRuntimeException("variable name \"" + varname + "\" is the primary key : no duplicate allowed.");
            } else {
                varsByNames.put(varname, var);
            }
        }
    }

    private class HelpStopCriterion implements Criterion {

        @Override
        public boolean isMet() {
            if (needHelp.isMet()) {
                needHelp.overrideLimit(needHelp.getLimitValue() + helpEveryXXNodes);
                try {
                    ChocoBetter newBetter = EPSComm.sendrecvBetter(better);
                    if (newBetter != null) {
                        better = newBetter;
                        updateObjective();
                        return false;
                    } else {
                        return true;
                    }
                } catch (ClassNotFoundException | IOException e) {
                    throw new EPSRuntimeException("did not receive a better message.", e);
                }
            } else {
                return false;
            }
        }
    }


    protected void applyJob(ChocoJob cjob) throws ContradictionException {
//    	System.out.println(problem.getModel().getSolver().toString());
    	cjob.applyDecisionPath(varsByNames);
    }


    private void updateObjective() {
        problem.getModel().getSolver().getObjectiveManager().updateBestBounds(better.getBounds());
    }


    @Override
    public ChocoBetter execute(ChocoJob job, int limit) {
       //System.out.println("r "+Arrays.toString(problem.getModel().getVars()));
        try {
            updateObjective();
            applyJob(job);
            //System.out.println("j "+Arrays.toString(problem.getModel().getVars()));
            
        } catch (ContradictionException e) {
            EPSComm.LOGGER.log(Level.WARNING, "<{0}> job is trivially unsat (infeasible at the root node).\n{0}", new Object[]{EPSComm.rank(), job});
            return new ChocoBetter(problem.getModel(), SearchState.TERMINATED);
        }
        problem.solve();
        final ChocoBetter better = new ChocoBetter(problem.getModel());
        final Solver s = problem.getModel().getSolver();
        s.reset();
        s.getEnvironment().worldPop();
        // store another time for restart purpose
        s.getEnvironment().worldPush();   
        return better;
    }

    @Override
    public void recordBetter(ChocoBetter better) {
        this.better = ((ChocoBetter) better);
    }
}
