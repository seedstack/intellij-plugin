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
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

import javax.swing.*;
import java.util.Optional;

public class InterfaceNode extends SeedStackGroupNode<ClassNode> {
    private final PsiClass psiInterface;

    public InterfaceNode(SeedStackSimpleNode parent, PsiClass psiInterface) {
        super(parent);
        if (!psiInterface.isInterface()) {
            throw new IllegalArgumentException("PsiClass " + psiInterface + " is not an interface");
        }
        this.psiInterface = psiInterface;
        setIcon(getInterfaceIcon());
    }

    @Override
    public String getName() {
        return psiInterface.getName();
    }

    @Override
    public DisplayKind getDisplayKind() {
        return DisplayKind.ALWAYS;
    }

    @Nullable
    @Override
    public VirtualFile getVirtualFile() {
        return Optional.ofNullable(psiInterface.getContainingFile()).map(PsiFile::getVirtualFile).orElse(null);
    }

    @Nullable
    @Override
    public Navigatable getNavigatable() {
        PsiElement navigationElement = psiInterface.getNavigationElement();
        if (navigationElement instanceof Navigatable) {
            return (Navigatable) navigationElement;
        } else {
            return null;
        }
    }

    @Override
    protected MultiMap<PsiFile, ClassNode> computeChildren(@Nullable PsiFile psiFile) {
        MultiMap<PsiFile, ClassNode> children = new MultiMap<>();
        ClassInheritorsSearch.search(psiInterface, true).forEach(candidate -> {
            if (!NavigatorUtil.isAbstract(candidate)) {
                children.putValue(candidate.getContainingFile(), new ClassNode(this, candidate));
            }
        });
        return children;
    }

    protected Icon getInterfaceIcon() {
        return SeedStackIcons.INTERFACE;
    }

    @NotNull
    @Override
    public Object[] getEqualityObjects() {
        return new Object[]{psiInterface};
    }
}
