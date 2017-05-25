package org.seedstack.intellij.navigator.tools;

import com.google.common.base.CaseFormat;
import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

class ToolNode extends SeedStackSimpleNode {
    private final PsiClass psiClass;
    private final String name;

    ToolNode(SeedStackSimpleNode parent, PsiClass psiClass) {
        super(parent);
        this.psiClass = psiClass;
        this.name = buildName(psiClass);
        setIcon(SeedStackIcons.TOOL);
    }

    @Override
    public String getName() {
        return name;
    }

    private String buildName(PsiClass psiClass) {
        String simpleName = psiClass.getName();
        if (simpleName != null) {
            String name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, simpleName);
            if (name.endsWith("_tool")) {
                name = name.substring(0, name.length() - 5);
            }
            name = name.replace("_", " ");
            return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        } else {
            throw new IllegalStateException("Tool PsiClass has no name");
        }
    }

}
