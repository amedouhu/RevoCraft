package com.amedouhu.revocraft.utils;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class Price {
    /* Priceの実装クラス */

    final private static Yaml yaml = new Yaml("utils.price");

    public static int getBuy(ItemStack itemStack) {
        /* 買取価格を取得する */
        YamlConfiguration priceYaml = yaml.get();
        return yaml.get().getInt(itemStack.getType() + ".buy");
    }

    public static int getSell(ItemStack itemStack) {
        /* 販売価格を取得する */
        return yaml.get().getInt(itemStack.getType() + ".sell");
    }

    public static Map<String, Integer> getBuyMap() {
        /* 販売価格のマップを取得する */
        YamlConfiguration yamlConfig = yaml.get();
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String key : yamlConfig.getKeys(false)) {
            if (Material.getMaterial(key) == null) {
                // Materialオブジェクトが存在しないなら
                continue;
            }
            map.put(key, yamlConfig.getInt(key + ".buy"));
        }
        return map;
    }

    public static Map<String, Integer> getSellMap() {
        /* 買取価格のマップを取得する */
        YamlConfiguration yamlConfig = yaml.get();
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String key : yamlConfig.getKeys(false)) {
            try {
                new ItemStack(Material.getMaterial(key));
            }catch (Exception e) {
                continue;
            }
            map.put(key, yamlConfig.getInt(key + ".sell"));
        }
        return map;
    }

    public static void setSell(ItemStack itemStack, int price) {
        /* 販売価格を設定する */
        YamlConfiguration yamlConfig = yaml.get();
        yamlConfig.set(itemStack.getType() + ".sell", price);
        yaml.set(yamlConfig);
    }

    public static void setBuy(ItemStack itemStack, int price) {
        /* 買取価格を設定する */
        YamlConfiguration yamlConfig = yaml.get();
        yamlConfig.set(itemStack.getType() + ".buy", price);
        yaml.set(yamlConfig);
    }
}
