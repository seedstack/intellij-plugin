package org.seedstack.intellij.config.completion;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.seedstack.intellij.config.util.CoffigResolver;

import java.util.stream.Stream;

class KeyCompletionProvider implements CompletionResolver {
    @Override
    public Stream<LookupElementBuilder> resolve(String path, PsiElement position) {
        Project project = position.getProject();
        Stream<String> keys;
        if (path.isEmpty()) {
            keys = CoffigResolver.from(project)
                    .onlyAtTopLevel()
                    .classes()
                    .map(CoffigResolver.Match::getName);
        } else {
            keys = CoffigResolver.from(project)
                    .find(path)
                    .map(CoffigResolver.Match::allProperties)
                    .orElse(Stream.empty());
        }
        return keys.map(key -> key.split("\\.")[0]).map(LookupElementBuilder::create);
    }
}
