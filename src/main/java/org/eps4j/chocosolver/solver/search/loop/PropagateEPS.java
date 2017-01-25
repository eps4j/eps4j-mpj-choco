/**
 * This file is part of eps4j-choco, http://github.com/eps4j/eps4j-choco
 *
 * Copyright (c) 2017, Arnaud Malapert, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.eps4j.chocosolver.solver.search.loop;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.loop.propagate.Propagate;
import org.eps4j.chocosolver.ChocoBetter;
import org.eps4j.chocosolver.ChocoJob;
import org.eps4j.specs.proc.IJobObserver;

public class PropagateEPS implements Propagate {

    private final Propagate propagate;
    private final Decompose decompose;
    private final IJobObserver<ChocoJob, ChocoBetter> jobObserver;

    public PropagateEPS(Propagate propagate, Decompose decompose, IJobObserver<ChocoJob, ChocoBetter> jobObserver) {
        super();
        this.propagate = propagate;
        this.decompose = decompose;
        this.jobObserver = jobObserver;
    }

    @Override
    public void execute(Solver solver) throws ContradictionException {
        propagate.execute(solver);
        if(decompose.sendNode(solver)) {
            jobObserver.notifyJob(new ChocoJob(solver), new ChocoBetter(solver.getModel()));
            solver.getEngine().fails(null, null, "EPS Decomposition");
        }
    }
}
