package com.amedouhu.revocraft;

import com.amedouhu.revocraft.commands.Buy;
import com.amedouhu.revocraft.commands.Sell;
import com.amedouhu.revocraft.commands.Send;
import com.amedouhu.revocraft.gimmicks.CountBar;
import com.amedouhu.revocraft.gimmicks.TabList;
import com.amedouhu.revocraft.listeners.*;
import com.amedouhu.revocraft.utils.Resource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RevoCraft extends JavaPlugin {
    /* RevoCraftのプラグインクラス */
    private static JavaPlugin plugin;

    public static JavaPlugin getPlugin() {return plugin;}

    @Override
    public void onEnable() {
        /* プラグインの起動ロジック*/
        // ロジックの定義
        super.onEnable();
        plugin = this;
        // コマンドの定義
        getCommand("buy").setExecutor(new Buy());
        getCommand("sell").setExecutor(new Sell());
        getCommand("send").setExecutor(new Send());
        // イベントリスナーの定義
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClose(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChat(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
        // 設定ファイルの生成
        Resource.saveAll(false);
        // カウントバーの開始
        CountBar.start();
        // タブリストの開始
        TabList.start();
    }

    @Override
    public void onDisable() {
        /* プラグインの終了ロジック */
        // ロジックの定義
        super.onDisable();
        // カウントバーの終了
        CountBar.stop();
        // タブリストの終了
        TabList.stop();
    }
}
