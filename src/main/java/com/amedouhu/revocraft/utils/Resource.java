package com.amedouhu.revocraft.utils;

import com.amedouhu.revocraft.RevoCraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Resource {
    /* Resourceオブジェクトの実装クラス */
    public static void save(String path, boolean replace) {
        /* リソースを保存する */
        RevoCraft.getPlugin().saveResource(path, replace);
    }

    public static void saveAll(boolean replace) {
        /* 全てのリソースを保存する */
        List<String> resourcePaths = new ArrayList<>();
        resourcePaths.add("commands.buy.yml");
        resourcePaths.add("commands.sell.yml");
        resourcePaths.add("gimmicks.count_bar.yml");
        resourcePaths.add("gimmicks.tab_list.yml");
        resourcePaths.add("utils.price.yml");
        resourcePaths.add("utils.user.yml");
        for (String path : resourcePaths) {
            File file = new File(RevoCraft.getPlugin().getDataFolder(), path);
            if (file.exists()) {
                // 既にファイルが生成されているなら
                continue;
            }
            RevoCraft.getPlugin().saveResource(path ,replace);
        }
    }
}
