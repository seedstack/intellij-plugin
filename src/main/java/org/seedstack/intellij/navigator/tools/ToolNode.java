/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator.tools;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.common.ClassNode;

import static org.seedstack.intellij.navigator.util.NavigatorUtil.humanizeString;

class ToolNode extends ClassNode {
    private final String name;

    ToolNode(SeedStackSimpleNode parent, PsiClass psiClass) {
        super(parent, psiClass);
        this.name = buildName(psiClass);
        setIcon(SeedStackIcons.TOOL);
    }

    @Override
    public String getName() {
        return name;
    }

    private String buildName(PsiClass psiClass) {
        String simpleName = psiClass.getName();
        if (simpleName != null) {
            return humanizeString(simpleName, "Tool");
        } else {
            throw new IllegalStateException("Tool PsiClass has no name");
        }
    }
}
