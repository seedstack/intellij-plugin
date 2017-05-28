package org.seedstack.intellij.navigator.business.domain;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.business.AnnotatedInterfaceNode;

import java.util.Optional;

class PoliciesNode extends AnnotatedInterfaceNode<PolicyNode> {
    private static final String POLICY_ANNOTATION = "org.seedstack.business.domain.DomainPolicy";
    private static final String NAME = "Policies";

    PoliciesNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.POLICY);
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    protected PolicyNode createChild(PsiClass psiClass) {
        return new PolicyNode(this, psiClass);
    }

    @Override
    protected boolean isSatisfying(PsiClass psiClass) {
        return Optional.ofNullable(psiClass.getQualifiedName())
                .filter(qualifiedName -> qualifiedName.contains(".domain."))
                .isPresent();
    }

    @Override
    protected String getAnnotationQName() {
        return POLICY_ANNOTATION;
    }
}
