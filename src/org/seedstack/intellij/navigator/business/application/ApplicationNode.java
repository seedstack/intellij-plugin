package org.seedstack.intellij.navigator.business.application;

import com.google.common.collect.Lists;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

import java.util.List;

public class ApplicationNode extends SeedStackGroupNode {
    private static final String NAME = "Application";
    private final ApplicationServicesNode applicationServicesNode;

    public ApplicationNode(SeedStackSimpleNode parent) {
        super(parent);
        this.applicationServicesNode = new ApplicationServicesNode(this);
        setIcon(SeedStackIcons.FOLDER);
        NavigatorUtil.runDumbAware(getProject(), this::updateApplication);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected List<? extends SeedStackSimpleNode> doGetChildren() {
        return Lists.newArrayList(applicationServicesNode);
    }

    public void updateApplication() {
        applicationServicesNode.updateServices();
        childrenChanged();
    }
}
