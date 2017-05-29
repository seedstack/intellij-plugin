/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.refactor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLElementGenerator;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLValue;
import org.jetbrains.yaml.psi.impl.YAMLKeyValueImpl;
import org.jetbrains.yaml.psi.impl.YAMLPlainTextImpl;
import org.jetbrains.yaml.psi.impl.YAMLQuotedTextImpl;

public class CoffigYamlKeyValueManipulator extends AbstractElementManipulator<YAMLKeyValueImpl> {
    @NotNull
    public TextRange getRangeInElement(@NotNull YAMLKeyValueImpl element) {
        return element.getTextRange();
    }

    public YAMLKeyValueImpl handleContentChange(@NotNull YAMLKeyValueImpl element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        String oldText = element.getText();
        String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
        YAMLValue value = element.getValue();
        Project project = element.getProject();
        if (value instanceof YAMLPlainTextImpl) {
            return (YAMLKeyValueImpl) element.replace(createYamlPlainText(project, element.getKeyText(), newText.substring(element.getTextLength() - element.getValueText().length())));
        }
        if (value instanceof YAMLQuotedTextImpl) {
            return (YAMLKeyValueImpl) element.replace(createYamlDoubleQuotedString(project, element.getKeyText(), newText.substring(element.getTextLength() - element.getValueText().length())));
        }
        return null;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private YAMLKeyValue createYamlDoubleQuotedString(Project project, String keyName, String valueText) {
        PsiFile tempFile = YAMLElementGenerator.getInstance(project).createDummyYamlWithText(keyName + ": " + valueText);
        return (YAMLKeyValue) PsiTreeUtil.collectElementsOfType(tempFile, new Class[]{YAMLKeyValue.class}).iterator().next();
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private YAMLKeyValue createYamlPlainText(Project project, String keyName, String valueText) {
        PsiFile tempFile = YAMLElementGenerator.getInstance(project).createDummyYamlWithText(keyName + ": " + valueText);
        return (YAMLKeyValue) PsiTreeUtil.collectElementsOfType(tempFile, new Class[]{YAMLKeyValue.class}).iterator().next();
    }

}
