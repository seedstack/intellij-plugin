/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator;

import com.intellij.psi.PsiFile;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SeedStackGroupNode<T extends SeedStackSimpleNode> extends SeedStackSimpleNode {
    private final MultiMap<PsiFile, T> children = new MultiMap<>();

    public SeedStackGroupNode(SeedStackSimpleNode parent) {
        super(parent);
    }

    SeedStackGroupNode(SeedStackStructure seedStackStructure, SeedStackSimpleNode parent) {
        super(seedStackStructure, parent);
    }

    @Override
    public boolean isVisible() {
        if (getDisplayKind() == DisplayKind.ALWAYS) return true;

        for (SimpleNode each : getChildren()) {
            if (((SeedStackSimpleNode) each).isVisible()) return true;
        }
        return false;
    }

    @Override
    protected final void refresh(PsiFile psiFile) {
        MultiMap<PsiFile, T> refreshed = computeChildren(psiFile);
        if (refreshed != null) {
            children.keySet().removeIf(key -> !refreshed.containsKey(key));
            for (PsiFile key : refreshed.keySet()) {
                if (children.containsKey(key)) {
                    Collection<T> oldCollection = children.get(key);
                    Collection<T> newCollection = refreshed.get(key);
                    oldCollection.removeIf(item -> !newCollection.contains(item));
                    for (T item : newCollection) {
                        if (oldCollection.contains(item)) {
                            oldCollection.remove(item);
                            oldCollection.add(item);
                        } else {
                            oldCollection.add(item);
                        }
                    }

                } else {
                    children.put(key, refreshed.get(key));
                }
            }
        }
        // Give the opportunity for all children to refresh
        children.values().forEach(child -> child.refresh(psiFile));
        childrenChanged();
    }

    protected MultiMap<PsiFile, T> computeChildren(@Nullable PsiFile psiFile) {
        return new MultiMap<>();
    }

    @Override
    protected final List<T> doGetChildren() {
        ArrayList<T> result = new ArrayList<>(children.values());
        result.sort(SeedStackStructure.NODE_COMPARATOR);
        return result;
    }
}
