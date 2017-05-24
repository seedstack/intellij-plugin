package org.seedstack.intellij.config.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLQuotedText;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.seedstack.intellij.config.util.MacroResolver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
        private static final String MACRO_START = "${";
        private static final String MACRO_END = "}";
        private static final KeyCompletionProvider KEY_COMPLETION_PROVIDER = new KeyCompletionProvider();
        private static final ValueCompletionProvider VALUE_COMPLETION_PROVIDER = new ValueCompletionProvider();

        @Override
        protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
            Stream<LookupElementBuilder> stream = null;
            PsiElement position = completionParameters.getPosition();
            PsiElement originalPosition = completionParameters.getOriginalPosition();

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
            else if (isValue(originalPosition)) {
                YAMLScalar yamlScalar = (YAMLScalar) originalPosition.getContext();
                if (yamlScalar != null) {
                    int cursorOffset = calculateCursorOffset(completionParameters, originalPosition, yamlScalar);
                    List<MacroResolver.Match> matches = new MacroResolver().resolve(yamlScalar.getTextValue().substring(0, cursorOffset));
                    if (!matches.isEmpty()) {
                        MacroResolver.Match closestMatch = findClosestMatch(matches, cursorOffset);
                        if (closestMatch != null) {
                            MacroInfo macroInfo = resolveMacroInfo(closestMatch, completionResultSet, cursorOffset);
                            completionResultSet = macroInfo.completionResultSet;
                            stream = KEY_COMPLETION_PROVIDER.resolve(macroInfo.path, originalPosition);
                        }
                    } else {
                        stream = VALUE_COMPLETION_PROVIDER.resolve(resolvePath(originalPosition), originalPosition);
                    }
                }
            }

            // Add lookup elements to completion results
            if (stream != null) {
                stream.forEach(completionResultSet::addElement);
            }
        }

        private int calculateCursorOffset(@NotNull CompletionParameters completionParameters, PsiElement position, YAMLScalar yamlScalar) {
            // YAML quoted text is shifted by one because of starting quote
            return completionParameters.getOffset() - (position.getTextRange().getStartOffset() + (yamlScalar instanceof YAMLQuotedText ? 1 : 0));
        }

        private MacroResolver.Match findClosestMatch(List<MacroResolver.Match> matches, int offset) {
            MacroResolver.Match closest = null;
            for (MacroResolver.Match match : matches) {
                int currentShift = offset - match.getStartPos();
                if (currentShift > 0 && match.isIncomplete()) {
                    if (closest == null || currentShift < offset - closest.getStartPos()) {
                        closest = match;
                    }
                }
            }
            return closest;
        }

        private MacroInfo resolveMacroInfo(MacroResolver.Match match, CompletionResultSet completionResultSet, int cursorOffset) {
            String reference = match.getReference();
            reference = reference.substring(0, Math.min(cursorOffset - match.getStartPos(), reference.length()));
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
            return position != null && position.getContext() instanceof YAMLScalar;
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
