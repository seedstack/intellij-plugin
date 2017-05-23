package org.seedstack.intellij.config.reference;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.Optional;

import static org.seedstack.intellij.config.util.CoffigUtil.extractMacroReference;
import static org.seedstack.intellij.config.util.CoffigUtil.getMacroOffsets;
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
                            int offset = offsetOf((YAMLKeyValue) element);
                            Optional<int[]> macroOffsets = getMacroOffsets(valueText, offset);
                            if (macroOffsets.isPresent()) {
                                return resolvePsiReferences(element, extractMacroReference(valueText, macroOffsets.get()), offset);
                            }
                        }
                        return PsiReference.EMPTY_ARRAY;
                    }

                    private int offsetOf(@NotNull YAMLKeyValue yamlKeyValue) {
                        return yamlKeyValue.getTextRange().getEndOffset() - yamlKeyValue.getTextRange().getStartOffset() - (yamlKeyValue.getText().length() - yamlKeyValue.getValueText().length());
                    }
                });
    }
}
