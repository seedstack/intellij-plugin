package org.seedstack.intellij.navigator.business;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.business.application.ApplicationNode;
import org.seedstack.intellij.navigator.business.domain.DomainNode;

class BusinessNode extends SeedStackGroupNode<SeedStackGroupNode> {
    private static final String NAME = "Business";
    private final DomainNode domainNode;
    private final ApplicationNode applicationNode;

    BusinessNode(SeedStackSimpleNode parent) {
        super(parent);
        this.domainNode = new DomainNode(this);
        this.applicationNode = new ApplicationNode(this);
        setIcon(SeedStackIcons.BUSINESS);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected MultiMap<PsiFile, SeedStackGroupNode> computeChildren(@Nullable PsiFile psiFile) {
        MultiMap<PsiFile, SeedStackGroupNode> children = new MultiMap<>();
        children.put(null, Lists.newArrayList(domainNode, applicationNode));
        return children;
    }
}
