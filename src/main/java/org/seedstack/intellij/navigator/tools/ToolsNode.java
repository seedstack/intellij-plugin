/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator.tools;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.containers.MultiMap;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

import static org.seedstack.intellij.navigator.util.NavigatorUtil.isAbstract;

class ToolsNode extends SeedStackGroupNode<ToolNode> {
    private static final String TOOL_INTERFACE = "org.seedstack.seed.spi.SeedTool";
    private static final String NAME = "Tools";

    ToolsNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.TOOLS);
    }

    @Override
    public String getName() {
        return NAME;
    }

    protected MultiMap<PsiFile, ToolNode> computeChildren(PsiFile psiFile) {
        MultiMap<PsiFile, ToolNode> children = new MultiMap<>();
        Project project = getProject();
        if (project != null) {
            PsiClass toolInterface = JavaPsiFacade.getInstance(project).findClass(TOOL_INTERFACE, GlobalSearchScope.allScope(project));
            if (toolInterface != null) {
                ClassInheritorsSearch.search(toolInterface, GlobalSearchScope.allScope(project), true).forEach(psiClass -> {
                    PsiFile containingFile = psiClass.getContainingFile();
                    if (!isAbstract(psiClass)) {
                        children.putValue(containingFile, new ToolNode(this, psiClass));
                    }
                });
            }
        }
        return children;
    }
}
