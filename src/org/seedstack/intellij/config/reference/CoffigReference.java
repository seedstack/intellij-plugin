package org.seedstack.intellij.config.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.config.util.CoffigUtil;

import java.util.ArrayList;
import java.util.List;

public class CoffigReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final String key;

    public CoffigReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        if (key != null) {
            Project project = myElement.getProject();
            return CoffigUtil.findCoffigKey(project, key).stream()
                    .map(PsiElementResolveResult::new)
                    .toArray(ResolveResult[]::new);
        }
        return new ResolveResult[0];
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        List<LookupElement> variants = new ArrayList<>();
        return variants.toArray();
    }
}