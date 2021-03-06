/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator.common;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

import javax.swing.*;
import java.util.Optional;

public class ClassNode extends SeedStackSimpleNode {
    private final PsiClass psiClass;

    public ClassNode(SeedStackSimpleNode parent, PsiClass psiClass) {
        super(parent);
        this.psiClass = psiClass;
        setIcon(getClassIcon());
    }

    @Override
    public String getName() {
        return psiClass.getName();
    }

    @Nullable
    @Override
    public VirtualFile getVirtualFile() {
        return Optional.ofNullable(psiClass.getContainingFile()).map(PsiFile::getVirtualFile).orElse(null);
    }

    @Nullable
    @Override
    public Navigatable getNavigatable() {
        PsiElement navigationElement = psiClass.getNavigationElement();
        if (navigationElement instanceof Navigatable) {
            return (Navigatable) navigationElement;
        } else {
            return null;
        }
    }

    protected PsiClass getPsiClass() {
        return psiClass;
    }

    protected Icon getClassIcon() {
        return SeedStackIcons.CLASS;
    }

    @NotNull
    @Override
    public Object[] getEqualityObjects() {
        return new Object[] {psiClass};
    }
}
