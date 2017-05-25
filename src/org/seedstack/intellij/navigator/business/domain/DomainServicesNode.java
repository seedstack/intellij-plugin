package org.seedstack.intellij.navigator.business.domain;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.search.GlobalSearchScope;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.business.ServiceNode;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

import java.util.ArrayList;
import java.util.List;

class DomainServicesNode extends SeedStackGroupNode {
    private static final String NAME = "Services";
    private final List<ServiceNode> serviceNodes = new ArrayList<>();

    DomainServicesNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.SERVICE);
        NavigatorUtil.runDumbAware(getProject(), this::updateServices);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected List<? extends SeedStackSimpleNode> doGetChildren() {
        return serviceNodes;
    }

    void updateServices() {
        serviceNodes.clear();
        serviceNodes.add(new ServiceNode(this, JavaPsiFacade.getInstance(getProject()).findClass("org.seedstack.seed.spi.SeedTool", GlobalSearchScope.allScope(getProject()))));
        sort(serviceNodes);
        childrenChanged();
    }
}
