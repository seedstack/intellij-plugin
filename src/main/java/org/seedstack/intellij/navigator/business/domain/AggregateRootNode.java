/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator.business.domain;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.common.ClassNode;

import javax.swing.*;

class AggregateRootNode extends ClassNode {
    AggregateRootNode(SeedStackSimpleNode parent, PsiClass psiClass) {
        super(parent, psiClass);
    }

    protected Icon getClassIcon() {
        return SeedStackIcons.AGGREGATE_ROOT;
    }
}
