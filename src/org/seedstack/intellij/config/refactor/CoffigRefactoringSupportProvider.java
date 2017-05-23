package org.seedstack.intellij.config.refactor;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import static org.seedstack.intellij.config.util.CoffigUtil.isConfigFile;

public class CoffigRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isInplaceRenameAvailable(PsiElement element, PsiElement context) {
        return isConfigFile(element) && element instanceof YAMLKeyValue;
    }

    @Override
    public boolean isMemberInplaceRenameAvailable(PsiElement element, PsiElement context) {
        return isConfigFile(element) && element instanceof YAMLKeyValue;
    }

    @Override
    public boolean isSafeDeleteAvailable(@NotNull PsiElement element) {
        return isConfigFile(element);
    }
}