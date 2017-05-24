package org.seedstack.intellij.config.reference;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLQuotedText;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.seedstack.intellij.config.util.MacroResolver;

import java.util.ArrayList;
import java.util.List;

import static org.seedstack.intellij.config.util.CoffigUtil.isConfigFile;

public class CoffigYamlReferenceContributor extends CoffigBaseReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(YAMLScalar.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (isConfigFile(element)) {
                            String valueText = ((YAMLScalar) element).getTextValue();
                            List<MacroResolver.Match> matches = new MacroResolver().resolve(valueText);
                            List<PsiReference> references = new ArrayList<>();
                            matches.stream()
                                    .filter(match -> !match.isEscaped())
                                    .map(match -> resolvePsiReferences(element, match.getReference(), (element instanceof YAMLQuotedText ? 1 : 0) + match.getStartPos()))
                                    .forEach(references::addAll);
                            return references.toArray(new PsiReference[references.size()]);
                        }
                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}
