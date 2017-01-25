/**
 * This file is part of eps4j-choco, http://github.com/eps4j/eps4j-choco
 *
 * Copyright (c) 2017, Arnaud Malapert, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.eps4j.chocosolver.samples;

import static org.chocosolver.solver.search.strategy.Search.minDomLBSearch;

import java.util.HashSet;
import java.util.stream.IntStream;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Settings;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
import org.kohsuke.args4j.Option;

public class AllNQueenBinary extends AbstractProblem {

    @Option(name = "-q", usage = "Number of queens.", required = false)
    private int n = 4;

    private IntVar[] vars;

    @Option(name = "-e", usage = "arithmetic expression.", required = false)
    private boolean useArithExpr;


    public AllNQueenBinary() {
        super();
    }

    HashSet<Constraint> set;

    @Override
    public void buildModel() {
        model = new Model("NQueen");
        vars = model.intVarArray("Q", n, 1, n);
        if(useArithExpr) {
            IntStream.range(0, n-1).forEach(i ->
            IntStream.range(i+1, n).forEach(j ->{
                vars[i].ne(vars[j]).post();
                vars[i].ne(vars[j].sub(j - i)).post();
                vars[i].ne(vars[j].add(j - i)).post();
            })
                    );
        } else {
            for(int i  = 0; i < n-1; i++){
                for(int j = i + 1; j < n; j++){
                    model.arithm(vars[i], "!=",vars[j]).post();
                    model.arithm(vars[i], "!=", vars[j], "-", j - i).post();
                    model.arithm(vars[i], "!=", vars[j], "+", j - i).post();
                }
            }
        }
        //        vars[0].eq(1).post();
        //        vars[0].le(4).post();
        model.set(new Settings() {

            @Override
            public boolean checkModel(Solver solver) {
                return true;
            }

        });
    }
    
    @Override
    public void configureSearch() {
        model.getSolver().setSearch(minDomLBSearch(vars));
        // model.getSolver().showSolutions();
    }


    @Override
    public void solve() {
        model.getSolver().streamSolutions().count();		
    }

    public static void main(String[] args) {
        final AllNQueenBinary nqueen = new AllNQueenBinary();
        nqueen.execute(args);
        System.out.println(nqueen.model.getSolver().toDimacsString());
    } 
    

}
