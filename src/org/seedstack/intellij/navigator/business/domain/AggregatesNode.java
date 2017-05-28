package org.seedstack.intellij.navigator.business.domain;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

import static org.seedstack.intellij.navigator.util.NavigatorUtil.isAbstract;

class AggregatesNode extends SeedStackGroupNode {
    private static final String AGGREGATE_ROOT_INTERFACE = "org.seedstack.business.domain.AggregateRoot";
    private static final String BUSINESS_PACKAGE = "org.seedstack.business";
    private static final String NAME = "Aggregates";

    AggregatesNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.AGGREGATE);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected MultiMap computeChildren(@Nullable PsiFile psiFile) {
        MultiMap<PsiFile, AggregateNode> children = new MultiMap<>();
        Project project = getProject();
        if (project != null) {
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            PsiClass psiClass = javaPsiFacade.findClass(AGGREGATE_ROOT_INTERFACE, GlobalSearchScope.allScope(project));
            if (psiClass != null) {
                ClassInheritorsSearch.search(psiClass, GlobalSearchScope.allScope(project), true).forEach(candidate -> {
                    String qualifiedName = candidate.getQualifiedName();
                    if (qualifiedName != null && !qualifiedName.startsWith(BUSINESS_PACKAGE) && !isAbstract(candidate)) {
                        children.putValue(candidate.getContainingFile(), new AggregateNode(this, candidate));
                    }
                });
            }

        }
        return children;
    }
}
