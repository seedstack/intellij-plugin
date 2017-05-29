/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
