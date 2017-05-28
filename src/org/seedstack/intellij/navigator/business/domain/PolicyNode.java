package org.seedstack.intellij.navigator.business.domain;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.common.InterfaceNode;

import javax.swing.*;

class PolicyNode extends InterfaceNode {
    PolicyNode(SeedStackSimpleNode parent, PsiClass psiClass) {
        super(parent, psiClass);
    }

    protected Icon getInterfaceIcon() {
        return SeedStackIcons.SERVICE;
    }
}
