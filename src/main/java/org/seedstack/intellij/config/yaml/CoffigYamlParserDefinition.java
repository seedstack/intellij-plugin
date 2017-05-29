/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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