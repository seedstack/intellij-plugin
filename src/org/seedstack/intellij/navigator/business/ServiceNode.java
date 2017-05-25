package org.seedstack.intellij.navigator.business;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

public class ServiceNode extends SeedStackSimpleNode {
    private final PsiClass psiClass;

    public ServiceNode(SeedStackSimpleNode parent, PsiClass psiClass) {
        super(parent);
        this.psiClass = psiClass;
        setIcon(SeedStackIcons.SERVICE);
    }

    @Override
    public String getName() {
        return psiClass.getName();
    }
}
