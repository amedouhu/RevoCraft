package com.amedouhu.revocraft.utils;

import com.amedouhu.revocraft.commands.Buy;
import com.amedouhu.revocraft.commands.Sell;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User {
    /* Userの実装クラス */

    private static final Yaml yaml = new Yaml("utils.user");
    private final String id;

    public User(Player player) {
        /* Playerオブジェクトを引数とした場合のインスタンス処理 */
        this.id = player.getUniqueId().toString();
        YamlConfiguration yamlConfig = yaml.get();
        if (yamlConfig.get(this.id) == null) {
            // ユーザデータが存在しないなら
            yamlConfig.set(this.id + ".balance", 0);
            yamlConfig.set(this.id + ".debt", 0);
        }
        yamlConfig.set(this.id + ".mcid", player.getName());
        yaml.set(yamlConfig);
    }

    public User(OfflinePlayer offlinePlayer) {
        /* OfflinePlayerオブジェクトを引数とした場合のインスタンス処理 */
        this.id = offlinePlayer.getUniqueId().toString();
        YamlConfiguration yamlConfig = yaml.get();
        if (yamlConfig.get(this.id) == null) {
            // ユーザデータが存在しないなら
            yamlConfig.set(this.id + "balance", 0);
            yamlConfig.set(this.id + ".debt", 0);
        }
        yamlConfig.set(this.id + ".mcid", offlinePlayer.getName());
        yaml.set(yamlConfig);
    }

    public User(String uuid) {
        /* Stringオブジェクトを引数とした場合のインスタンス処理 */
        this.id = uuid;
        YamlConfiguration yamlConfig = yaml.get();
        if (yamlConfig.get(this.id) == null) {
            // ユーザデータが存在しないなら
            yamlConfig.set(this.id + ".balance", 0);
            yamlConfig.set(this.id + ".debt", 0);
        }
        yamlConfig.set(this.id + ".mcid", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
        yaml.set(yamlConfig);
    }

    public int getBalance() {
        /* 残高を取得する */
        return yaml.get().getInt(this.id + ".balance");
    }

    public void setBalance(int balance) {
        /* 残高を設定する */
        YamlConfiguration yamlConfig = yaml.get();
        if (balance<=0) {
            // 設定値が0以下なら
            balance = 0;
        }
        if (Sell.getMaxBalance() < balance) {
            balance = Sell.getMaxBalance();
        }
        yamlConfig.set(this.id + ".balance", balance);
        yaml.set(yamlConfig);
    }

    public void addBalance(int balance) {
        /* 残高を追加する */
        YamlConfiguration yamlConfig = yaml.get();
        int result = yamlConfig.getInt(this.id + ".balance") + balance;
        if (result<=0) {
            // 設定値が0以下なら
            setBalance(0);
            return;
        }
        if (Sell.getMaxBalance() < result) {
            result = Sell.getMaxBalance();
        }
        yamlConfig.set(this.id + ".balance", result);
        yaml.set(yamlConfig);
    }

    public int getDebt() {
        /* 負債を取得する */
        return yaml.get().getInt(this.id + ".debt");
    }

    public void setDebt(int debt) {
        /* 負債を設定する */
        YamlConfiguration yamlConfig = yaml.get();
        if (debt<=0) {
            // 設定値が0以下なら
            debt = 0;
        }
        if (Buy.getMaxDebt() < debt) {
            debt = Buy.getMaxDebt();
        }
        yamlConfig.set(this.id + ".debt", debt);
        yaml.set(yamlConfig);
    }

    public void addDebt(int debt) {
        /* 負債を追加する */
        YamlConfiguration yamlConfig = yaml.get();
        int result = yamlConfig.getInt(this.id + ".debt") + debt;
        if (result<=0) {
            // 設定値が0以下なら
            result = 0;
        }
        if (Buy.getMaxDebt() < result) {
            result = Buy.getMaxDebt();
        }
        yamlConfig.set(this.id + ".debt", result);
        yaml.set(yamlConfig);
    }

    public String getUuid() {
        /* uuidを取得する */
        return this.id;
    }

    public String getMcid() {
        /* mcidを取得する */
        return yaml.get().getString(this.id + ".mcid");
    }

    public boolean isOnline() {
        /* オンラインかを取得する */
        boolean online = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().toString().equals(id)) {
                // オンラインなら
                online = true;
            }
        }
        return online;
    }

    public Player getPlayer() {
        /* プレイヤーオブジェクトを取得する */
        return Bukkit.getPlayer(UUID.fromString(this.id));
    }

    public OfflinePlayer getOfflinePlayer() {
        /* オフラインプレイヤーオブジェクトを取得する */
        String mcid = getMcid();
        if (getMcid() == null) {
            return null;
        }
        return Bukkit.getOfflinePlayer(mcid);
    }

    public static User[] getValues() {
        /* 値を取得する */
        User[] users = new User[yaml.get().getKeys(false).size()];
        int i = 0;
        for (String key : yaml.get().getKeys(false)) {
            users[i] = new User(key);
            i++;
        }
        return users;
    }
}
