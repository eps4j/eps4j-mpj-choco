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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.strategy.decision.DecisionPath;
import org.chocosolver.solver.search.strategy.decision.EPSDecisionMaker;
import org.chocosolver.solver.search.strategy.decision.EPSDecisionMaker.DecisionEPS;
import org.chocosolver.solver.variables.Variable;
import org.eps4j.exception.EPSRuntimeException;
import org.eps4j.specs.msg.IJob;

public class ChocoJob implements IJob {

    private static final long serialVersionUID = -8710229714088517216L;

    private byte[] binDecisionPath;

    public ChocoJob(final Solver solver) {
        super();	
        DecisionPath dp = solver.getDecisionPath();
        ArrayList<DecisionEPS<?>> decisionPath = new ArrayList<DecisionEPS<?>>(dp.size());
        int last = dp.size();
        while (last > 1) {
            decisionPath.add(EPSDecisionMaker.makeDecisionEPS(dp.getDecision(--last)));
        }
        //immediate serialization because the object will be modified by the branching
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(decisionPath);
            binDecisionPath =  bos.toByteArray();
            bos.close();
            oos.close();
        } catch (IOException e) {
            throw new EPSRuntimeException("can not serialize decision path", e);
        }
    }

    public final void applyDecisionPath(HashMap<String, Variable> varsByNames) throws ContradictionException {
        try {
            final ByteArrayInputStream bis = new ByteArrayInputStream(binDecisionPath);
            final ObjectInputStream ois = new ObjectInputStream(bis);
            List<DecisionEPS<?>> decisionPath = (List<DecisionEPS<?>>) ois.readObject();
            for (DecisionEPS<?> decision : decisionPath) {
                //System.out.println(decision.getVarname() + decision);
                if (varsByNames.containsKey(decision.getVarname())) {
                    decision.apply( varsByNames.get(decision.getVarname()));
                } else {
                    throw new EPSRuntimeException("could not find variable " + decision.getVarname() + ".");
                }
            }
            bis.close();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new EPSRuntimeException("can not deserialize decision path", e);
        } 

    }

    @Override
    public String toString() {
        return "ChocoJob [" + binDecisionPath.hashCode() +  "]";
    }




}