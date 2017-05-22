package org.seedstack.intellij.config.usage;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.lexer.YAMLFlexLexer;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import static org.seedstack.intellij.config.util.CoffigUtil.isConfigFile;
import static org.seedstack.intellij.config.util.CoffigUtil.resolvePath;

public class CoffigFindUsagesProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new YAMLFlexLexer(),
                TokenSet.create(YAMLTokenTypes.SCALAR_KEY),
                TokenSet.create(YAMLTokenTypes.COMMENT),
                TokenSet.create(YAMLTokenTypes.SCALAR_TEXT, YAMLTokenTypes.SCALAR_DSTRING, YAMLTokenTypes.SCALAR_STRING));
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return isConfigFile(psiElement) && psiElement instanceof YAMLKeyValue;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof YAMLKeyValue) {
            return "SeedStack configuration";
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof YAMLKeyValue) {
            return resolvePath(element);
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof YAMLKeyValue) {
            if (useFullName) {
                return resolvePath(element);
            } else {
                return ((YAMLKeyValue) element).getKeyText();
            }
        } else {
            return "";
        }
    }
}