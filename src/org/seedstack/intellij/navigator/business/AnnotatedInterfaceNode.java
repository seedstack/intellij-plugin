package org.seedstack.intellij.navigator.business;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.common.InterfaceNode;

public abstract class AnnotatedInterfaceNode<T extends InterfaceNode> extends SeedStackGroupNode<T> {
    public AnnotatedInterfaceNode(SeedStackSimpleNode parent) {
        super(parent);
    }

    @Override
    protected MultiMap<PsiFile, T> computeChildren(@Nullable PsiFile psiFile) {
        MultiMap<PsiFile, T> children = new MultiMap<>();
        Project project = getProject();
        if (project != null) {
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            PsiClass serviceAnnotation = javaPsiFacade.findClass(getAnnotationQName(), GlobalSearchScope.allScope(project));
            if (serviceAnnotation != null) {
                AnnotatedElementsSearch.searchPsiClasses(serviceAnnotation, GlobalSearchScope.allScope(project)).forEach(psiClass -> {
                    if (psiClass.isInterface() && isSatisfying(psiClass)) {
                        children.putValue(psiClass.getContainingFile(), createChild(psiClass));
                    }
                    return true;
                });
            }
        }
        return children;
    }

    protected abstract T createChild(PsiClass psiClass);

    protected abstract boolean isSatisfying(PsiClass psiClass);

    protected abstract String getAnnotationQName();
}
