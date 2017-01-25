/**
 * This file is part of eps4j-choco, http://github.com/eps4j/eps4j-choco
 *
 * Copyright (c) 2017, Arnaud Malapert, Université Nice Sophia Antipolis. All rights reserved.
 *
 * Licensed under the BSD 3-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.eps4j.chocosolver.samples;

import java.io.IOException;

import org.chocosolver.pf4cs.SetUpException;
import org.eps4j.EPSCmd;
import org.eps4j.exception.EPSException;

public class SamplesTest {

    public static void main(String[] args) throws ClassNotFoundException, EPSException, SetUpException, IOException {
        EPSCmd.doMain(args);
    }
}
