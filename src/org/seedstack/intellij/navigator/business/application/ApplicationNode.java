package org.seedstack.intellij.navigator.business.application;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

public class ApplicationNode extends SeedStackGroupNode<SeedStackGroupNode> {
    private static final String NAME = "Application";
    private final ApplicationServicesNode applicationServicesNode;

    public ApplicationNode(SeedStackSimpleNode parent) {
        super(parent);
        this.applicationServicesNode = new ApplicationServicesNode(this);
        setIcon(SeedStackIcons.FOLDER);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected MultiMap<PsiFile, SeedStackGroupNode> computeChildren(@Nullable PsiFile psiFile) {
        MultiMap<PsiFile, SeedStackGroupNode> children = new MultiMap<>();
        children.put(null, Lists.newArrayList(applicationServicesNode));
        return children;
    }
}
