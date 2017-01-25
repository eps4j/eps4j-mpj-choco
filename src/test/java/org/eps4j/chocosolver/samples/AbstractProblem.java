/**
 * This file is part of eps4j-choco, http://github.com/eps4j/eps4j-choco
 *
 * Copyright (c) 2017, Arnaud Malapert, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.eps4j.chocosolver.samples;

import static java.lang.Runtime.getRuntime;

import org.chocosolver.pf4cs.IProblem;
import org.chocosolver.pf4cs.SetUpException;
import org.chocosolver.solver.Model;
import org.eps4j.chocosolver.ChocoFactory;
import org.kohsuke.args4j.Option;

/**
 * A class that provides a pattern to declare a model and solve it. <br/>
 * 
 * source: org.chocosolver.samples.AbstractProblem
 * 
 * @author Charles Prud'homme, Arnaud Malapert
 * @since 31/03/11
 */
public abstract class AbstractProblem implements IProblem<Model> {

    /**
     * A seed for random purpose
     */
    @Option(name = "-seed", usage = "Seed for Shuffle propagation engine.", required = false)
    protected long seed = 29091981;

    /**
     * Declared problem
     */
    protected Model model;

    private boolean userInterruption = true;

    /**
     * @return the current model
     */
    @Override
    public Model getModel() {
        return model;
    }

    /**
     * Call search configuration
     */
    @Override
    public void configureSearch() {}

    @Override
    public void tearDown() {}


    @Override
    public void setUp(String... args) throws SetUpException {
        ChocoFactory.parseArguments(this, args);    
    }

    private boolean userInterruption() {
        return userInterruption;
    }

    /**
     * Main method: from argument reading to resolution.
     * <ul>
     * <li>read program arguments</li>
     * <li>build the model</li>
     * <li>configure the search</li>
     * <li>launch the resolution</li>
     * </ul>
     * 
     * @param args
     *            list of arguments to pass to the problem
     */
    public final void execute(String... args) {
        try {
            setUp(args); 
            buildModel();
            configureSearch();

            Thread statOnKill = new Thread() {
                public void run() {
                    if (userInterruption()) {
                        System.out.println(model.getSolver().getMeasures().toString());
                    }
                }
            };
            getRuntime().addShutdownHook(statOnKill);
            this.solve();
            userInterruption = false;
            getRuntime().removeShutdownHook(statOnKill);
        } catch (SetUpException e) {
            // Nothing to do: message already displayed
        }	
    }

}
