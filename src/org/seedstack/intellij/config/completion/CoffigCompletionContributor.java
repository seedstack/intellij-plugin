package org.seedstack.intellij.config.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;

import java.util.Optional;
import java.util.stream.Stream;

import static org.seedstack.intellij.config.util.CoffigUtil.extractMacroReference;
import static org.seedstack.intellij.config.util.CoffigUtil.getMacroOffsets;
import static org.seedstack.intellij.config.util.CoffigUtil.isConfigFile;
import static org.seedstack.intellij.config.util.CoffigUtil.resolvePath;

public class CoffigCompletionContributor extends CompletionContributor {
    private static final DispatchingProvider DISPATCHING_COMPLETION_PROVIDER = new DispatchingProvider();

    public CoffigCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(YAMLTokenTypes.TEXT).withLanguage(YAMLLanguage.INSTANCE), DISPATCHING_COMPLETION_PROVIDER);
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(YAMLTokenTypes.SCALAR_STRING).withLanguage(YAMLLanguage.INSTANCE), DISPATCHING_COMPLETION_PROVIDER);
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(YAMLTokenTypes.SCALAR_DSTRING).withLanguage(YAMLLanguage.INSTANCE), DISPATCHING_COMPLETION_PROVIDER);
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(YAMLTokenTypes.SCALAR_KEY).withLanguage(YAMLLanguage.INSTANCE), DISPATCHING_COMPLETION_PROVIDER);
    }

    private static class DispatchingProvider extends CompletionProvider<CompletionParameters> {
        private static final KeyCompletionProvider KEY_COMPLETION_PROVIDER = new KeyCompletionProvider();
        private static final ValueCompletionProvider VALUE_COMPLETION_PROVIDER = new ValueCompletionProvider();

        @Override
        protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
            Stream<LookupElementBuilder> stream = null;
            PsiElement position = completionParameters.getPosition();

            // No completion on ordinary YAML files
            if (!isConfigFile(position)) {
                return;
            }

            // Completion for YAML keys
            if (isKey(position)) {
                stream = KEY_COMPLETION_PROVIDER.resolve(resolvePath(position), position)
                        .map(prev -> LookupElementBuilder.create(prev.getLookupString() + ": ")
                                .withPresentableText(prev.getLookupString())
                        );
            }
            // Completion for YAML values
            else if (isValue(position)) {
                PsiElement originalPosition = completionParameters.getOriginalPosition();
                if (originalPosition != null) {
                    TextRange textRange = originalPosition.getTextRange();
                    int endOffset = textRange.getEndOffset() - textRange.getStartOffset();
                    Optional<int[]> macroOffsets = getMacroOffsets(originalPosition.getText(), endOffset);
                    if (macroOffsets.isPresent()) {
                        MacroInfo macroInfo = resolveMacroInfo(originalPosition.getText(), macroOffsets.get(), completionResultSet);
                        completionResultSet = macroInfo.completionResultSet;
                        stream = KEY_COMPLETION_PROVIDER.resolve(macroInfo.path, position);
                    }
                } else {
                    stream = VALUE_COMPLETION_PROVIDER.resolve(resolvePath(position), position);
                }
            }

            // Add lookup elements to completion results
            if (stream != null) {
                stream.forEach(completionResultSet::addElement);
            }
        }

        private MacroInfo resolveMacroInfo(String value, int[] macroOffsets, CompletionResultSet completionResultSet) {
            String reference = extractMacroReference(value, macroOffsets);
            int lastDotIndex = reference.lastIndexOf(".");
            String path;
            if (reference.isEmpty()) {
                // empty reference
                completionResultSet = completionResultSet.withPrefixMatcher("");
                path = "";
            } else if (lastDotIndex != -1) {
                // reference with at least one dot
                completionResultSet = completionResultSet.withPrefixMatcher(reference.substring(lastDotIndex + 1));
                path = reference.substring(0, lastDotIndex);
            } else {
                // reference without dot
                completionResultSet = completionResultSet.withPrefixMatcher(reference);
                path = "";
            }
            return new MacroInfo(path, completionResultSet);
        }


        private boolean isKey(PsiElement position) {
            PsiElement parentContext = position.getParent().getContext();
            PsiElement leftContext = Optional.ofNullable(position.getContext()).map(PsiElement::getPrevSibling).orElse(null);
            return parentContext instanceof YAMLMapping || parentContext instanceof YAMLDocument || leftContext != null && ((LeafPsiElement) leftContext).getElementType() == YAMLTokenTypes.INDENT;
        }

        private boolean isValue(PsiElement position) {
            return position.getParent().getContext() instanceof YAMLKeyValue;
        }

        private static class MacroInfo {
            private final String path;
            private final CompletionResultSet completionResultSet;

            private MacroInfo(String path, CompletionResultSet completionResultSet) {
                this.path = path;
                this.completionResultSet = completionResultSet;
            }
        }
    }
}
