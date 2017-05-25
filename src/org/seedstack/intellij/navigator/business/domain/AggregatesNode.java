package org.seedstack.intellij.navigator.business.domain;

import com.intellij.psi.JavaPsiFacade;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

import java.util.ArrayList;
import java.util.List;

class AggregatesNode extends SeedStackGroupNode {
    private static final String NAME = "Aggregates";
    private final List<AggregateNode> aggregateNodes = new ArrayList<>();

    AggregatesNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.AGGREGATE);
        NavigatorUtil.runDumbAware(getProject(), this::updateAggregates);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected List<? extends SeedStackSimpleNode> doGetChildren() {
        return aggregateNodes;
    }

    void updateAggregates() {
        aggregateNodes.clear();
        aggregateNodes.add(new AggregateNode(this, JavaPsiFacade.getInstance(getProject()).findPackage("org.seedstack.seed")));
        sort(aggregateNodes);
        childrenChanged();
    }
}
