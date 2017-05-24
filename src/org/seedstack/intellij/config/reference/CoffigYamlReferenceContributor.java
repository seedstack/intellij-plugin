package org.seedstack.intellij.config.reference;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.seedstack.intellij.config.util.MacroResolver;

import java.util.ArrayList;
import java.util.List;

import static org.seedstack.intellij.config.util.CoffigUtil.isConfigFile;
import static org.seedstack.intellij.config.util.CoffigUtil.isYamlLeaf;

public class CoffigYamlReferenceContributor extends CoffigBaseReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(YAMLKeyValue.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (isConfigFile(element) && isYamlLeaf(element)) {
                            String valueText = ((YAMLKeyValue) element).getValueText();
                            List<MacroResolver.Match> matches = new MacroResolver().resolve(valueText);
                            List<PsiReference> references = new ArrayList<>();
                            matches.stream()
                                    .filter(match -> !match.isEscaped())
                                    .map(match -> resolvePsiReferences(element, match.getReference(), valueOffset((YAMLKeyValue) element) + match.getStartPos()))
                                    .forEach(references::addAll);
                            return references.toArray(new PsiReference[references.size()]);
                        }
                        return PsiReference.EMPTY_ARRAY;
                    }

                    private int valueOffset(@NotNull YAMLKeyValue yamlKeyValue) {
                        return yamlKeyValue.getTextRange().getEndOffset() - yamlKeyValue.getTextRange().getStartOffset() - yamlKeyValue.getValueText().length();
                    }
                });
    }
}
