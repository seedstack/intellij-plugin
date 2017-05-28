package org.seedstack.intellij.navigator.business.domain;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.business.ServicesNode;

import java.util.Optional;

class DomainServicesNode extends ServicesNode {
    private static final String NAME = "Services";

    DomainServicesNode(SeedStackSimpleNode parent) {
        super(parent);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected boolean isSatisfying(PsiClass psiClass) {
        return Optional.ofNullable(psiClass.getQualifiedName())
                .filter(qualifiedName -> qualifiedName.contains(".domain."))
                .isPresent();
    }
}
