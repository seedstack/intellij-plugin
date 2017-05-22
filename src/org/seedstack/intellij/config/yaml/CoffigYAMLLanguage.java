package org.seedstack.intellij.config.yaml;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLLanguage;

class CoffigYAMLLanguage extends Language {
    static final CoffigYAMLLanguage INSTANCE = new CoffigYAMLLanguage();

    private CoffigYAMLLanguage() {
        super(YAMLLanguage.INSTANCE, "coffig/yaml", "application/yaml");
    }

    @NotNull
    public String getDisplayName() {
        return "Coffig YAML";
    }
}
