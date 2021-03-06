/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator.business;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.common.InterfaceNode;

import javax.swing.*;

public class ServiceNode extends InterfaceNode {
    public ServiceNode(SeedStackSimpleNode parent, PsiClass psiClass) {
        super(parent, psiClass);
    }

//    protected Icon getInterfaceIcon() {
//        return SeedStackIcons.SERVICE;
//    }
}
