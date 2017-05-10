package org.seedstack.intellij.config.completion.value;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import org.seedstack.intellij.spi.config.ValueCompletionResolver;

import java.util.stream.Stream;

public class BooleanCompletionResolver implements ValueCompletionResolver {
    private static final String JAVA_BOOLEAN_CLASS = Boolean.class.getName();
    private static final String ENABLED = "enabled";
    private static final String DISABLED = "disabled";

    @Override
    public boolean canHandle(PsiClass rawType) {
        return JAVA_BOOLEAN_CLASS.equals(rawType.getQualifiedName());
    }

    @Override
    public Stream<LookupElementBuilder> resolveCompletions(String propertyName, PsiClass rawType, PsiType[] parameterTypes) {
        if (ENABLED.equalsIgnoreCase(propertyName) || DISABLED.equalsIgnoreCase(propertyName)) {
            return Stream.of("yes", "no").map(LookupElementBuilder::create);
        }
        return Stream.of("true", "false").map(LookupElementBuilder::create);
    }
}
