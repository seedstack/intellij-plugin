package org.seedstack.intellij.config.yaml;

import com.intellij.openapi.fileTypes.ExactFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class CoffigYAMLFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(CoffigYAMLFileType.INSTANCE, new ExactFileNameMatcher("application.yaml"), new ExactFileNameMatcher("application.yml"));
    }
}