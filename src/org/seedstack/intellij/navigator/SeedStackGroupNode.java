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

    SeedStackGroupNode(SeedStackStructure seedStackStructure) {
        super(seedStackStructure);
    }

    public SeedStackGroupNode(SeedStackSimpleNode parent) {
        super(parent);
    }

    @Override
    public boolean isVisible() {
        if (getDisplayKind() == SeedStackStructure.DisplayKind.ALWAYS) return true;

        for (SimpleNode each : getChildren()) {
            if (((SeedStackSimpleNode) each).isVisible()) return true;
        }
        return false;
    }

    @Override
    protected final List<? extends SeedStackSimpleNode> doGetChildren() {
        ArrayList<T> result = new ArrayList<>(children.values());
        sort(result);
        return result;
    }

    @Override
    public final void refresh(PsiFile psiFile) {
        MultiMap<PsiFile, T> refreshed = computeChildren(psiFile);
        if (refreshed != null) {
            children.keySet().removeIf(key -> !refreshed.containsKey(key));
            for (PsiFile key : refreshed.keySet()) {
                if (children.containsKey(key)) {
                    update(children.get(key), refreshed.get(key));
                } else {
                    children.put(key, refreshed.get(key));
                }
            }
        }
        // Give the opportunity for all children to refresh
        children.values().forEach(child -> child.refresh(psiFile));
        childrenChanged();
    }

    private void update(Collection<T> oldCollection, Collection<T> newCollection) {
        oldCollection.removeIf(item -> !newCollection.contains(item));
        for (T item : newCollection) {
            if (oldCollection.contains(item)) {
                oldCollection.remove(item);
                oldCollection.add(item);
            } else {
                oldCollection.add(item);
            }
        }
    }

    private void sort(List<T> list) {
        list.sort(SeedStackStructure.NODE_COMPARATOR);
    }

    protected MultiMap<PsiFile, T> computeChildren(@Nullable PsiFile psiFile) {
        return new MultiMap<>();
    }
}
