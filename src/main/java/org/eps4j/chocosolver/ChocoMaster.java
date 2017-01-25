/**
 * This file is part of eps4j-choco, http://github.com/eps4j/eps4j-choco
 *
 * Copyright (c) 2017, Arnaud Malapert, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.eps4j.chocosolver;

import java.util.logging.Level;

import org.chocosolver.pf4cs.IProblem;
import org.chocosolver.pf4cs.SetUpException;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.features.Features;
import org.chocosolver.solver.search.SearchState;
import org.chocosolver.util.criteria.Criterion;
import org.eps4j.EPSComm;
import org.eps4j.chocosolver.solver.search.loop.Decompose;
import org.eps4j.chocosolver.solver.search.loop.DecomposeInst;
import org.eps4j.chocosolver.solver.search.measures.MeasuresEPS;
import org.eps4j.specs.actors.IMasterEPS;
import org.eps4j.specs.proc.IBetterObserver;
import org.eps4j.specs.proc.IJobObserver;
import org.eps4j.specs.proc.IMasterProgress;
import org.kohsuke.args4j.Option;

public class ChocoMaster implements IMasterEPS<ChocoJob, ChocoBetter> {

    @Option(name="-d",aliases={"--depth"},usage="Depth of the decomposition.")
    private int decomposeDepth = 2;

    private final IProblem<Model> problem;

    private IMasterProgress masterProgress;

    private Decompose decompose;

    public ChocoMaster(IProblem<Model> problem) {
        super();
        this.problem = problem;
        // problem.setUp(args) is already done
    }

    @Override
    public void setMasterProgress(IMasterProgress masterProgress) {
        this.masterProgress = masterProgress;
    }

    private class MasterStopCriterion implements Criterion {

        @Override
        public boolean isMet() {
            return masterProgress.isStopped();
        }
    }

    @Override
    public void setUp(String... args) throws SetUpException {
        ChocoFactory.parseArguments(this, args); 
        //decompose = new DecomposeDepth(decomposeDepth);
        decompose = new DecomposeInst(decomposeDepth);
    }




    @Override
    public ChocoBetter decompose(IJobObserver<ChocoJob, ChocoBetter> jobObserver, int workers) {
        // build the problem
        problem.buildModel();
        problem.configureSearch();
        // Add decomposition hook 
        final Solver solver = problem.getModel().getSolver();
        ChocoFactory.addDecomposeHook(solver, jobObserver, decompose);
        // Add stop criterion
        problem.getModel().getSolver().addStopCriterion(new MasterStopCriterion());
        problem.solve();
        EPSComm.LOGGER.log(Level.INFO, "Decomposition status:\n{0}", problem.getModel().getSolver().getMeasures());
        return new ChocoBetter(problem.getModel());
    }



    @Override
    public ChocoBetter diversify(IBetterObserver<ChocoBetter> betterObserver) {
        final Model model = problem.getModel();
        final MeasuresEPS measures = new MeasuresEPS(model.getName());
        measures.setSearchState(SearchState.TERMINATED);
        measures.setBoundsManager(model.getSolver().getObjectiveManager());
        return new ChocoBetter(new Features(model), measures);      
    }


    //TODO BeforeDownBranch
    @Override
    public void notifyBetter(ChocoBetter better) {
        // Called by another thread while the solver is running
        problem.getModel().getSolver().getObjectiveManager().updateBestBounds(better.getBounds());
    }


    @Override
    public void tearDown() {}
}
