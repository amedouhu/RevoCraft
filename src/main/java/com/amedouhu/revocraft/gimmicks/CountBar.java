package com.amedouhu.revocraft.gimmicks;

import com.amedouhu.revocraft.RevoCraft;
import com.amedouhu.revocraft.utils.User;
import com.amedouhu.revocraft.utils.Yaml;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CountBar {
    /* カウントバーの実装クラス */
    private static final Yaml yaml = new Yaml("gimmicks.count_bar");
    private static final BossBar countBar = Bukkit.createBossBar(ChatColor.BOLD + "RevoCraft", BarColor.RED, BarStyle.SEGMENTED_6);
    private static double score = yaml.get().getDouble("count");
    private static double max = score;

    private static final BukkitRunnable reloadTask = new BukkitRunnable() {
        @Override
        public void run() {
            reload();
        }
    };

    public static void start() {
        /* カウントバーを開始する */
        addAll();
        reloadTask.runTaskTimer(RevoCraft.getPlugin(), 0L, 20L);
    }

    public static void stop() {
        /* カウントバーを終了する */
        removeAll();
        reloadTask.cancel();
    }

    public static void reload() {
        /* カウントを更新する */
        try {
            // 更新を試みる
            if (max != yaml.get().getDouble("count")) {
                // 設定が変更されているなら
                throw new Exception();
            }
            if (score<1) {
                // カウントが終了したなら
                // 請求を行う
                score = max;
                List<String> bans = new ArrayList<>();
                for (User user : User.getValues()) {
                    int request = yaml.get().getInt("request");
                    if (user.getDebt()<request) {
                        request = user.getDebt();
                    }
                    if (user.getBalance()<request) {
                        // 支払いできないなら
                        user.addDebt(-user.getBalance());
                        user.setBalance(0);
                        if (! user.getPlayer().isBanned()) {
                            // ユーザがBanされていないなら
                            bans.add(user.getMcid());
                            Bukkit.getBanList(BanList.Type.NAME).addBan(user.getMcid(), yaml.get().getString("ban"), null, "revocraft");
                        }
                        if (user.isOnline()) {
                            // ユーザがオンラインなら
                            user.getPlayer().kickPlayer(yaml.get().getString("kick"));
                        }
                        continue;
                    }
                    if (request < 1) {
                        // 請求額が0なら
                        continue;
                    }
                    // 利子を付与する
                    final int interest =(int) ((double) user.getDebt()*yaml.get().getDouble("interest"));
                    user.addDebt(interest);
                    // 決済処理を行う
                    user.addBalance(-request);
                    user.addDebt(-request);
                    // 領収書を発行する
                    if (user.isOnline()) {
                        // ユーザがオンラインなら
                        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
                        BookMeta meta = (BookMeta) item.getItemMeta();
                        List<String> pages = new ArrayList<>();
                        pages.add("領 収 書\n\n" + user.getMcid() + " 様\n金額 " + request + " perica\n利子 " + interest + " perica\n但 ご購入の代金として\n\n上記正に領収いたしました");
                        meta.setTitle("領収書");
                        meta.setAuthor("RevoCraft");
                        meta.setPages(pages);
                        item.setItemMeta(meta);
                        user.getPlayer().openBook(item);
                    }
                }
                if (yaml.get().getBoolean("output")) {
                    // 出力が有効なら
                    if (0 < bans.size()) {
                        Bukkit.broadcastMessage(ChatColor.RESET + "今回の決済で滞納を行ったユーザ(" + bans.size() + "): " + bans);
                    }
                }
            }
            countBar.setProgress(score/max);
            String title = yaml.get().getString("title");
            title = title.replace("${score}", String.valueOf((int) score));
            countBar.setTitle(title);
            countBar.setColor(BarColor.valueOf(yaml.get().getString("color")));
            countBar.setStyle(BarStyle.valueOf(yaml.get().getString("style")));
            score--;
        }catch (Exception e) {
            // 更新に失敗したとき
            // 設定を再読み込みする
            score = yaml.get().getDouble("count");
            max = score;
        }
    }

    public static void add(Player player) {
        /* プレイヤーをカウントバーに追加する */
        countBar.addPlayer(player);
    }

    public static void addAll() {
        /* 全てのプレイヤーをカウントバーに追加する */
        for (Player player : Bukkit.getOnlinePlayers()) {
            countBar.addPlayer(player);
        }
    }

    public static void remove(Player player) {
        /* プレイヤーをカウントバーから削除する */
        countBar.removePlayer(player);
    }

    public static void removeAll() {
        /* すべてのプレイヤーをカウントバーから削除する */
        countBar.removeAll();
    }
}
