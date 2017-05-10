package org.seedstack.intellij.config.documentation;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.config.util.CoffigResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.seedstack.intellij.config.util.CoffigUtil.isConfigFile;
import static org.seedstack.intellij.config.util.CoffigUtil.resolvePath;

public class CoffigDocumentationProvider implements DocumentationProvider {
    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement psiElement, PsiElement originalElement) {
        return null;
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement psiElement, PsiElement originalElement) {
        return null;
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement psiElement, @Nullable PsiElement originalElement) {
        return resolveConfigInfo(psiElement).map(this::buildDescription).orElse(null);
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object o, PsiElement psiElement) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String s, PsiElement psiElement) {
        return null;
    }

    private String buildDescription(ConfigInfo configInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h1>").append(configInfo.getPath()).append("</h1>");
        if (configInfo.getLongDescription() != null) {
            sb.append(configInfo.getLongDescription());
        } else {
            sb.append(configInfo.getDescription());
        }
        if (configInfo.getType() != null) {
            sb.append("<h2>Type</h2>");
            sb.append("<code>").append(configInfo.getType()).append("</code>");
        }
        if (configInfo.getDefaultValue() != null) {
            sb.append("<h2>Default value</h2>");
            sb.append("<code>").append(configInfo.getDefaultValue()).append("</code>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    private Optional<ConfigInfo> resolveConfigInfo(PsiElement psiElement) {
        if (isConfigFile(psiElement)) {
            String path = resolvePath(psiElement);
            if (!path.isEmpty()) {
                Project project = psiElement.getProject();
                return CoffigResolver.from(project)
                        .onlyAtTopLevel()
                        .find(path, 0)
                        .flatMap(match -> findResourceBundle(project, match.getConfigClass()).flatMap(propertiesFile -> extractConfigInfo(propertiesFile, match)));
            }
        }
        return Optional.empty();
    }

    private Optional<ConfigInfo> extractConfigInfo(PropertiesFile propertiesFile, CoffigResolver.Match match) {
        Optional<String> description = Optional.ofNullable(propertiesFile.findPropertyByKey(match.getUnmatchedPath())).map(IProperty::getValue);
        if (description.isPresent()) {
            // Base info
            ConfigInfo configInfo = new ConfigInfo(match.getFullPath(), description.get());

            // Extended info
            Optional.ofNullable(propertiesFile.findPropertyByKey(match.getUnmatchedPath() + ".long")).map(IProperty::getValue).ifPresent(configInfo::setLongDescription);

            // Field info
            CoffigResolver.Match resolvedMatch = match.fullyResolve();
            if (resolvedMatch.isFullyResolved()) {
                Optional<PsiField> psiField = resolvedMatch.resolveField(resolvedMatch.getUnmatchedPath());
                psiField.map(PsiVariable::getType).map(PsiType::getPresentableText).ifPresent(configInfo::setType);
            }

            return Optional.of(configInfo);
        }
        return Optional.empty();
    }

    private Optional<PropertiesFile> findResourceBundle(Project project, PsiClass configClass) {
        String qualifiedName = configClass.getQualifiedName();
        if (qualifiedName != null) {
            int lastDotIndex = qualifiedName.lastIndexOf(".");
            String packageName = qualifiedName.substring(0, lastDotIndex);
            String className = qualifiedName.substring(lastDotIndex + 1);
            PsiPackage psiPackage = JavaPsiFacade.getInstance(project).findPackage(packageName);
            if (psiPackage != null) {
                return Arrays.stream(psiPackage.getFiles(GlobalSearchScope.allScope(project)))
                        .filter(psiFile -> psiFile instanceof PropertiesFile && psiFile.getVirtualFile().getNameWithoutExtension().equals(className))
                        .map(psiFile -> (PropertiesFile) psiFile)
                        .findFirst();
            }
        }
        return Optional.empty();
    }

    private static class ConfigInfo {
        private final String path;
        private final String description;
        private String longDescription;
        private String type;
        private String defaultValue;

        private ConfigInfo(String path, String description) {
            this.path = path;
            this.description = description;
        }

        String getPath() {
            return path;
        }

        String getDescription() {
            return description;
        }

        String getLongDescription() {
            return longDescription;
        }

        void setLongDescription(String longDescription) {
            this.longDescription = longDescription;
        }

        String getType() {
            return type;
        }

        void setType(String type) {
            this.type = type.replace("<", "&lt;").replace(">", "&gt;");
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }
}
