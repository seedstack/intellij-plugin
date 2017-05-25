package org.seedstack.intellij.navigator.tools;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.SeedStackLog;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

import java.util.ArrayList;
import java.util.List;

class ToolsNode extends SeedStackGroupNode {
    private static final String ORG_SEEDSTACK_SEED_SPI_SEED_TOOL = "org.seedstack.seed.spi.SeedTool";
    private static final String NAME = "Tools";
    private final List<ToolNode> toolNodes = new ArrayList<>();

    ToolsNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.TOOLS);
        NavigatorUtil.runDumbAware(getProject(), this::updateTools);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected List<? extends SeedStackSimpleNode> doGetChildren() {
        return toolNodes;
    }

    private void updateTools() {
        toolNodes.clear();
        toolNodes.addAll(detectProjectTools(myProject));
        sort(toolNodes);
        childrenChanged();
    }

    private List<ToolNode> detectProjectTools(Project project) {
        List<ToolNode> results = new ArrayList<>();
        PsiClass toolInterface = JavaPsiFacade.getInstance(project).findClass(ORG_SEEDSTACK_SEED_SPI_SEED_TOOL, GlobalSearchScope.allScope(project));
        if (toolInterface != null) {
            ClassInheritorsSearch.search(toolInterface, GlobalSearchScope.allScope(project), true).forEach(psiClass -> {
                if (isNotAbstract(psiClass)) {
                    try {
                        results.add(new ToolNode(this, psiClass));
                    } catch (Exception e) {
                        SeedStackLog.LOG.warn("Unable to resolve SeedStack tool", e);
                    }
                }
            });
        }
        return results;
    }

    private boolean isNotAbstract(PsiClass psiClass) {
        PsiModifierList modifierList = psiClass.getModifierList();
        return modifierList != null && !modifierList.hasModifierProperty("abstract");
    }
}
