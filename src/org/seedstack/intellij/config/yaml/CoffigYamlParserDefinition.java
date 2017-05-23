package org.seedstack.intellij.config.yaml;

import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.yaml.YAMLParserDefinition;

class CoffigYamlParserDefinition extends YAMLParserDefinition {
    private static final IFileElementType FILE = new IFileElementType(CoffigYamlLanguage.INSTANCE);

    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(final FileViewProvider viewProvider) {
        return new CoffigYamlFileImpl(viewProvider);
    }
}