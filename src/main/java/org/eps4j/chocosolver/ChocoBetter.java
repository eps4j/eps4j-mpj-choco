/**
 * This file is part of eps4j-choco, http://github.com/eps4j/eps4j-choco
 *
 * Copyright (c) 2017, Arnaud Malapert, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.eps4j.chocosolver;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.features.Features;
import org.chocosolver.solver.features.IFeatures;
import org.chocosolver.solver.objective.IBoundsManager;
import org.chocosolver.solver.search.SearchState;
import org.eps4j.chocosolver.solver.search.measures.MeasuresEPS;
import org.eps4j.specs.msg.IBetter;

public class ChocoBetter implements IBetter {

    private static final long serialVersionUID = 5603879896084959040L;

    private final IFeatures features;

    private final MeasuresEPS measures;

    public ChocoBetter(IFeatures features, MeasuresEPS measures) {
        super();
        this.features = features;
        this.measures = measures;
    }

    public ChocoBetter(Model model) {
        this(new Features(model), new MeasuresEPS(model.getSolver().getMeasures()));
    }
    
    /**
     * 
     * @param model retrieve information from the model
     * @param state set the search status
     */
    public ChocoBetter(Model model, SearchState state) {
        this(new Features(model), new MeasuresEPS(model.getSolver().getMeasures()));
        measures.setSearchState(state);
    }

   
    public final SearchState getSearchState() {
        return measures.getSearchState();
    }

    public final void setSearchState(SearchState state) {
        this.measures.setSearchState(state);
    }

    public final IFeatures getFeatures() {
        return features;
    }

    public final MeasuresEPS getMeasures() {
        return measures;
    }

    public final IBoundsManager getBounds() {
        return measures.getBoundsManager();
    }

    @Override
    public String toString() {
        return "[" + measures.getSearchState()+ ", " + measures.toCSV() + "]";
    }	

}
