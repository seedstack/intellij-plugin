package org.seedstack.intellij.navigator.business;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.common.InterfaceNode;

import javax.swing.*;

public class ServiceNode extends InterfaceNode {
    public ServiceNode(SeedStackSimpleNode parent, PsiClass psiClass) {
        super(parent, psiClass);
    }

    protected Icon getInterfaceIcon() {
        return SeedStackIcons.SERVICE;
    }
}
