/**
 * This file is part of eps4j-choco, http://github.com/eps4j/eps4j-choco
 *
 * Copyright (c) 2017, Arnaud Malapert, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.eps4j.chocosolver;

import java.util.Arrays;

import org.chocosolver.pf4cs.IProblem;
import org.chocosolver.pf4cs.SetUpException;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.eps4j.chocosolver.solver.search.loop.Decompose;
import org.eps4j.chocosolver.solver.search.loop.PropagateEPS;
import org.eps4j.proc.ForemanProcess;
import org.eps4j.proc.MasterProcess;
import org.eps4j.proc.WorkerProcess;
import org.eps4j.specs.IFactoryEPS;
import org.eps4j.specs.IProcessEPS;
import org.eps4j.specs.proc.IJobObserver;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;


public class ChocoFactory implements IFactoryEPS {

    private IProblem<Model> problem;

    public static void addDecomposeHook(Solver solver, IJobObserver<ChocoJob, ChocoBetter> jobObserver, Decompose decompose) {
        solver.setPropagate(new PropagateEPS(solver.getPropagate(), decompose, jobObserver));
    }

    @Override
    public void setUp(final String... args) throws SetUpException {
        if(args.length > 0) {
            try {
                problem = (IProblem<Model>) Class.forName (args[0]).newInstance ();
                problem.setUp(Arrays.copyOfRange(args, 1, args.length));
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new SetUpException("Can not create the problem.", e);
            }
        } else {
            throw new SetUpException("No problem specified.");
        }
    }

    @Override
    public IProcessEPS buildMaster() {
        return new MasterProcess<>(new ChocoMaster(problem));
    }

    @Override
    public IProcessEPS buildForeman() {
        return new ForemanProcess<>(new ChocoForeman());
    }

    @Override
    public IProcessEPS buildWorker(int rank, int nranks) {
        return new WorkerProcess<>(new ChocoWorker(problem));
    }	

    /**
     * Read the program arguments from object
     * @param object the args4j annotated object
     * @param args list of arguments
     * @return <tt>true</tt> if arguments were correctly read
     */
    public static void parseArguments(Object object, String... args) throws SetUpException {
        //FIXME Duplicated method from org.chocosolver.samples.AbstractProblem.parseArguments(Object, String...)
        final CmdLineParser parser = new CmdLineParser(object);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java " + object.getClass() + " [options...]");
            parser.printUsage(System.err);
            System.err.println();
            throw new SetUpException("Invalid problem options");
        }
    }

}
