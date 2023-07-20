package com.amedouhu.revocraft.utils;

import com.amedouhu.revocraft.RevoCraft;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Yaml {
    /* Yamlの実装クラス */

    private final String yamlName;

    public Yaml(String yamlName) {
        /* インスタンス処理 */
        this.yamlName = yamlName;
    }

    public YamlConfiguration get() {
        /* YamlConfigurationを取得する */
        File yamlFile = new File(RevoCraft.getPlugin().getDataFolder(), this.yamlName + ".yml");
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(yamlFile);
        set(yamlConfig);
        return yamlConfig;
    }

    public void set(YamlConfiguration yamlConfig) {
        /* YamlConfigurationを設定する */
        File yamlFile = new File(RevoCraft.getPlugin().getDataFolder(), this.yamlName + ".yml");
        try {
            yamlConfig.save(yamlFile);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
