package org.seedstack.intellij.config.completion;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;

import java.util.stream.Stream;

public interface ValueCompletionResolver {
    boolean canHandle(PsiClass rawType);

    Stream<LookupElementBuilder> resolveCompletions(String propertyName, PsiClass rawType, PsiType[] parameterTypes);
}
