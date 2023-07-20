package com.amedouhu.revocraft.gimmicks;

import com.amedouhu.revocraft.RevoCraft;
import com.amedouhu.revocraft.utils.User;
import com.amedouhu.revocraft.utils.Yaml;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TabList {
    /* タブリストの実装クラス */
    private static final Yaml yaml = new Yaml("gimmicks.tab_list");
    private static final BukkitRunnable reloadTask = new BukkitRunnable() {
        @Override
        public void run() {
            reload();
        }
    };
    public static void start() {
        reloadTask.runTaskTimer(RevoCraft.getPlugin(), 0L, 20L);
    }
    public static void stop() {
        reloadTask.cancel();
    }

    public static void reload() {
        /* 更新されたとき */
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = new User(player);
            try {
                // 更新を試みる
                String name;
                if (yaml.get().getString(user.getMcid()) != null) {
                    // 固有の設定を持っているなら
                    name = yaml.get().getString(user.getMcid());
                }else {
                    // 固有の設定を持っていないなら
                    name = yaml.get().getString("$default");
                }
                name = name.replace("${balance}", String.valueOf(user.getBalance()));
                name = name.replace("${debt}", String.valueOf(user.getDebt()));
                name = name.replace("${uuid}", user.getUuid());
                name = name.replace("${mcid}", user.getMcid());
                player.setPlayerListName(name);
            }catch (Exception e) {
                // 更新に失敗したなら
                player.setPlayerListName(user.getMcid());
            }
        }
    }
}
