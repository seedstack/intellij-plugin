package org.seedstack.intellij.navigator.business.domain;

import com.intellij.psi.PsiPackage;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

class AggregateNode extends SeedStackSimpleNode {
    private final PsiPackage psiPackage;

    AggregateNode(SeedStackSimpleNode parent, PsiPackage psiPackage) {
        super(parent);
        this.psiPackage = psiPackage;
        setIcon(SeedStackIcons.AGGREGATE);
    }

    @Override
    public String getName() {
        return psiPackage.getName();
    }
}
