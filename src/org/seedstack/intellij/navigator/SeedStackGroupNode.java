package org.seedstack.intellij.navigator;

import com.intellij.ui.treeStructure.SimpleNode;

import java.util.Collections;
import java.util.List;

public abstract class SeedStackGroupNode extends SeedStackSimpleNode {
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

    protected <T extends SeedStackSimpleNode> void insertSorted(List<T> list, T newObject) {
        int pos = Collections.binarySearch(list, newObject, SeedStackStructure.NODE_COMPARATOR);
        list.add(pos >= 0 ? pos : -pos - 1, newObject);
    }

    protected void sort(List<? extends SeedStackSimpleNode> list) {
        list.sort(SeedStackStructure.NODE_COMPARATOR);
    }
}
