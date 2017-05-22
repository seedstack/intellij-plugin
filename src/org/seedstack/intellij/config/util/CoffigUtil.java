package org.seedstack.intellij.config.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLPsiElement;
import org.seedstack.intellij.config.yaml.CoffigYAMLFileType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class CoffigUtil {
    private CoffigUtil() {
        // no instantiation allowed
    }

    public static boolean isConfigFile(@NotNull PsiElement psiElement) {
        return Objects.equals(psiElement.getContainingFile().getLanguage().getID(), "coffig/yaml");
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
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(
                FileTypeIndex.NAME,
                CoffigYAMLFileType.INSTANCE,
                GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            YAMLFile coffigFile = (YAMLFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (coffigFile != null) {
                result.addAll(coffigFile.getDocuments());
            }
        }
        return result;
    }

    public static Set<YAMLKeyValue> findCoffigKey(Project project, String path) {
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
}
