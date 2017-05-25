package org.seedstack.intellij.navigator.business.domain;

import com.google.common.collect.Lists;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

import java.util.List;

public class DomainNode extends SeedStackGroupNode {
    private static final String NAME = "Domain";
    private final AggregatesNode aggregatesNode;
    private final DomainServicesNode domainServicesNode;

    public DomainNode(SeedStackSimpleNode parent) {
        super(parent);
        this.aggregatesNode = new AggregatesNode(this);
        this.domainServicesNode = new DomainServicesNode(this);
        setIcon(SeedStackIcons.FOLDER);
        NavigatorUtil.runDumbAware(getProject(), this::updateDomain);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected List<? extends SeedStackSimpleNode> doGetChildren() {
        return Lists.newArrayList(aggregatesNode, domainServicesNode);
    }

    public void updateDomain() {
        aggregatesNode.updateAggregates();
        domainServicesNode.updateServices();
        childrenChanged();
    }
}
