package org.seedstack.intellij.config.completion;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;

import java.util.stream.Stream;

public interface CompletionResolver {
    Stream<LookupElementBuilder> resolve(String path, PsiElement position);
}
