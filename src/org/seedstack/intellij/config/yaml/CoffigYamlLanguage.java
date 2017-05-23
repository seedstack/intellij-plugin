package org.seedstack.intellij.config.yaml;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLLanguage;

class CoffigYamlLanguage extends Language {
    static final CoffigYamlLanguage INSTANCE = new CoffigYamlLanguage();

    private CoffigYamlLanguage() {
        super(YAMLLanguage.INSTANCE, "coffig/yaml", "application/yaml");
    }

    @NotNull
    public String getDisplayName() {
        return "Coffig YAML";
    }
}
