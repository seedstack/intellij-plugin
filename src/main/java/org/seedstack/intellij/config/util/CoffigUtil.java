/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLPsiElement;
import org.seedstack.intellij.config.yaml.CoffigYamlFileType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class CoffigUtil {
    private static final String CONFIGURATION_ANNOTATION_QNAME = "org.seedstack.seed.Configuration";
    private static final String COFFIG_CLASS_QNAME = "org.seedstack.coffig.Coffig";


    private CoffigUtil() {
        // no instantiation allowed
    }

    public static boolean isConfigFile(@NotNull PsiElement psiElement) {
        return Objects.equals(psiElement.getContainingFile().getLanguage().getID(), "coffig/yaml");
    }

    public static boolean isConfigurationAnnotation(@NotNull PsiAnnotation psiAnnotation) {
        return CONFIGURATION_ANNOTATION_QNAME.equals(psiAnnotation.getQualifiedName());
    }

    public static boolean isCoffig(@NotNull PsiClass psiClass) {
        return COFFIG_CLASS_QNAME.equals(psiClass.getQualifiedName());
    }

    public static boolean isLiteralOfConfigurationAnnotation(@NotNull PsiElement element) {
        return Optional.ofNullable(element.getParent())
                .map(PsiElement::getParent)
                .map(PsiElement::getParent)
                .filter(psiElement -> psiElement instanceof PsiAnnotation && CoffigUtil.isConfigurationAnnotation((PsiAnnotation) psiElement))
                .isPresent();
    }

    public static boolean isCoffigMethod(@NotNull PsiElement element) {
        return Optional.ofNullable(element.getParent())
                .map(PsiElement::getParent)
                .flatMap(CoffigUtil::getMethodContainingClass)
                .filter(CoffigUtil::isCoffig)
                .isPresent();
    }

    public static boolean isYamlLeaf(PsiElement psiElement) {
        return psiElement instanceof YAMLKeyValue && !(((YAMLKeyValue) psiElement).getValue() instanceof YAMLMapping);
    }

    public static String resolvePath(PsiElement psiElement) {
        List<String> path = new ArrayList<>();
        do {
            if (psiElement instanceof YAMLKeyValue) {
                path.add(0, ((YAMLKeyValue) psiElement).getKeyText());
            }
        } while ((psiElement = psiElement.getParent()) != null);
        return String.join(".", path);
    }

    public static List<YAMLDocument> findCoffigDocuments(Project project) {
        List<YAMLDocument> result = new ArrayList<>();
        for (VirtualFile virtualFile : findCoffigFiles(project)) {
            YAMLFile coffigFile = (YAMLFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (coffigFile != null) {
                result.addAll(coffigFile.getDocuments());
            }
        }
        return result;
    }

    @NotNull
    public static Collection<VirtualFile> findCoffigFiles(Project project) {
        return FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME,
                CoffigYamlFileType.INSTANCE,
                GlobalSearchScope.allScope(project));
    }

    public static Set<YAMLKeyValue> findCoffigKeys(Project project, String path) {
        Set<YAMLKeyValue> results = new HashSet<>();
        for (YAMLDocument yamlDocument : findCoffigDocuments(project)) {
            YAMLKeyValue yamlKeyValue = searchForKey(yamlDocument, path.split("\\."), 0);
            if (yamlKeyValue != null) {
                results.add(yamlKeyValue);
            }
        }
        return results;
    }

    private static YAMLKeyValue searchForKey(YAMLPsiElement yamlPsiElement, String[] path, int index) {
        if (index < path.length) {
            for (YAMLPsiElement current : yamlPsiElement.getYAMLElements()) {
                if (current instanceof YAMLKeyValue) {
                    if (path[index].equals(((YAMLKeyValue) current).getKeyText())) {
                        if (index == path.length - 1) {
                            return ((YAMLKeyValue) current);
                        } else {
                            return searchForKey(current, path, index + 1);
                        }
                    }
                } else {
                    return searchForKey(current, path, index);
                }
            }
        }
        return null;
    }


    private static Optional<PsiClass> getMethodContainingClass(@NotNull PsiElement psiElement) {
        if (psiElement instanceof PsiMethodCallExpression) {
            return Optional.ofNullable(((PsiMethodCallExpression) psiElement).resolveMethod()).map(PsiMember::getContainingClass);
        } else {
            return Optional.empty();
        }
    }
}
