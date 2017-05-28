package org.seedstack.intellij.navigator.business.domain;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.common.ClassNode;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

class AggregateNode extends SeedStackGroupNode<ClassNode> {
    private static final String ENTITY_INTERFACE = "org.seedstack.business.domain.Entity";
    private static final String VO_INTERFACE = "org.seedstack.business.domain.ValueObject";
    private final PsiPackage psiPackage;
    private final PsiClass aggregateRoot;
    private final String name;

    AggregateNode(SeedStackSimpleNode parent, PsiClass aggregateRoot) {
        super(parent);
        this.aggregateRoot = aggregateRoot;
        this.psiPackage = resolvePackage(aggregateRoot);
        this.name = resolveName(aggregateRoot);
        setIcon(SeedStackIcons.AGGREGATE);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected MultiMap<PsiFile, ClassNode> computeChildren(@Nullable PsiFile psiFile) {
        MultiMap<PsiFile, ClassNode> children = new MultiMap<>();
        children.putValue(aggregateRoot.getContainingFile(), new EntityNode(this, aggregateRoot));
        Project project = getProject();
        if (project != null) {
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            PsiClass entityInterface = javaPsiFacade.findClass(ENTITY_INTERFACE, GlobalSearchScope.allScope(project));
            PsiClass valueObjectInterface = javaPsiFacade.findClass(VO_INTERFACE, GlobalSearchScope.allScope(project));
            if (entityInterface != null && valueObjectInterface != null) {
                for (PsiClass psiClass : psiPackage.getClasses(GlobalSearchScope.allScope(project))) {
                    if (psiClass.isInheritor(entityInterface, true) && !psiClass.equals(aggregateRoot)) {
                        children.putValue(psiClass.getContainingFile(), new EntityNode(this, psiClass));
                    } else if (psiClass.isInheritor(valueObjectInterface, true)) {
                        children.putValue(psiClass.getContainingFile(), new ValueObjectNode(this, psiClass));
                    }
                }
            }
        }
        return children;
    }

    private String resolveName(PsiClass psiClass) {
        return NavigatorUtil.humanizeString(psiClass.getName(), null);
    }

    private PsiPackage resolvePackage(PsiClass aggregateRoot) {
        String qualifiedName = aggregateRoot.getQualifiedName();
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(getProject());
        return javaPsiFacade.findPackage(qualifiedName.substring(0, qualifiedName.lastIndexOf(".")));
    }

    @NotNull
    @Override
    public Object[] getEqualityObjects() {
        return new Object[]{psiPackage, aggregateRoot};
    }
}
