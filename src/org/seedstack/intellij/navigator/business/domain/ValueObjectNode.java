package org.seedstack.intellij.navigator.business.domain;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.common.ClassNode;

import javax.swing.*;

class ValueObjectNode extends ClassNode {
    ValueObjectNode(SeedStackSimpleNode parent, PsiClass psiClass) {
        super(parent, psiClass);
    }

    protected Icon getClassIcon() {
        return SeedStackIcons.VALUE_OBJECT;
    }
}
