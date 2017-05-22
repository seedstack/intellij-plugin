package org.seedstack.intellij.config.yaml;

import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.yaml.YAMLParserDefinition;

class CoffigYAMLParserDefinition extends YAMLParserDefinition {
    private static final IFileElementType FILE = new IFileElementType(CoffigYAMLLanguage.INSTANCE);

    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(final FileViewProvider viewProvider) {
        return new CoffigYAMLFileImpl(viewProvider);
    }
}