package org.seedstack.intellij.navigator.business;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

public abstract class ServicesNode extends AnnotatedInterfaceNode<ServiceNode> {
    private static final String SERVICE_ANNOTATION = "org.seedstack.business.Service";

    public ServicesNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.SERVICE);
    }

    @Override
    protected ServiceNode createChild(PsiClass psiClass) {
        return new ServiceNode(this, psiClass);
    }

    @Override
    protected String getAnnotationQName() {
        return SERVICE_ANNOTATION;
    }

    protected abstract boolean isSatisfying(PsiClass psiClass);
}
