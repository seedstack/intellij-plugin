package org.seedstack.intellij.navigator.rest;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.util.containers.MultiMap;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

class ResourcesNode extends SeedStackGroupNode<ResourceNode> {
    static final String PATH_ANNOTATION = "javax.ws.rs.Path";
    private static final String NAME = "REST root resources";

    ResourcesNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.CONFIG);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public MultiMap<PsiFile, ResourceNode> computeChildren(PsiFile psiFile) {
        Project project = getProject();
        MultiMap<PsiFile, ResourceNode> children = new MultiMap<>();
        if (project != null) {
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            PsiClass pathAnnotation = javaPsiFacade.findClass(PATH_ANNOTATION, GlobalSearchScope.allScope(project));
            if (pathAnnotation != null) {
                AnnotatedElementsSearch.searchPsiClasses(pathAnnotation, GlobalSearchScope.allScope(project)).forEach(psiClass -> {
                    if (!psiClass.isInterface() && !NavigatorUtil.isAbstract(psiClass)) {
                        children.putValue(psiClass.getContainingFile(), new ResourceNode(ResourcesNode.this, pathAnnotation, psiClass));
                    }
                    return true;
                });
            }
        }
        return children;
    }
}
