package org.seedstack.intellij.navigator.business;

import com.google.common.collect.Lists;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.business.application.ApplicationNode;
import org.seedstack.intellij.navigator.business.domain.DomainNode;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

import java.util.List;

class BusinessNode extends SeedStackGroupNode {
    private static final String NAME = "Business";
    private final DomainNode domainNode;
    private final ApplicationNode applicationNode;

    BusinessNode(SeedStackSimpleNode parent) {
        super(parent);
        this.domainNode = new DomainNode(this);
        this.applicationNode = new ApplicationNode(this);
        setIcon(SeedStackIcons.BUSINESS);
        NavigatorUtil.runDumbAware(getProject(), this::updateAll);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected List<? extends SeedStackSimpleNode> doGetChildren() {
        return Lists.newArrayList(domainNode, applicationNode);
    }

    private void updateAll() {
        domainNode.updateDomain();
        applicationNode.updateApplication();
        childrenChanged();
    }
}
