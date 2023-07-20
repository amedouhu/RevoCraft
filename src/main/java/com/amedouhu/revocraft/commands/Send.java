package com.amedouhu.revocraft.commands;

import com.amedouhu.revocraft.utils.Gui;
import com.amedouhu.revocraft.utils.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class Send implements CommandExecutor {
    /* sendコマンドの処理クラス */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /* コマンドが実行されたとき */
        if (! (sender instanceof Player)) {
            // 送信者がプレイヤーではないなら
            sender.sendMessage("このコマンドはマインクラフト内からのみ実行できます。");
            return false;
        }
        Player player = (Player) sender;
        Inventory gui = Gui.get("/send", player);
        User[] users = User.getValues();
        final boolean search = args.length==1;
        Thread request = new Thread(() -> {
            // mojangにPlayerHeadを要求するときに発生するスレッドの独占を回避する為、非同期で処理する
            int i = 0;
            for (int index = 18; index < gui.getSize(); index++) {
                if (users.length <= i) {
                    // レンダリングが完了したなら
                    return;
                }
                User user = users[i];
                if (search) {
                    // 検索が有効なら
                    if (user.getMcid().equals(args[0])) {
                        // 検索語句と一致するなら
                        index = 18;
                    }else {
                        // 検索語句と一致しないなら
                        i++;
                        continue;
                    }
                }
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                OfflinePlayer offlinePlayer = user.getOfflinePlayer();
                meta.setDisplayName(ChatColor.RESET + user.getMcid() + "に送金する");
                meta.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GRAY + "Click: 1 perica", ChatColor.RESET.toString() + ChatColor.GRAY + "Shift+Click: " + user.getDebt() + " perica (負債全額)", ChatColor.RESET.toString() + ChatColor.RED + "送金分はあなたの残高から引き落とされます。"));
                meta.setOwningPlayer(offlinePlayer);
                item.setItemMeta(meta);
                gui.setItem(index, item);
                i++;
            }
        });
        request.start();
        player.openInventory(gui);
        return true;
    }

    public static void onBackPage(InventoryClickEvent e) {
        /* 前のページが要求されたとき */
        Inventory gui = e.getClickedInventory();
        ItemStack triggerItem = gui.getItem(18);
        if (triggerItem == null) {
            // トリガーが存在しないなら
            return;
        }
        SkullMeta triggerMeta = (SkullMeta) triggerItem.getItemMeta();
        String trigger = triggerMeta.getOwningPlayer().getName();
        User[] users = User.getValues();
        if (trigger.equals(users[0].getMcid())) {
            // 前のページが存在しないなら
            return;
        }
        Thread request = new Thread(() -> {
            /*mojangにPlayerHeadを要求するときに発生するスレッドの独占を回避する為、非同期で処理する*/
            boolean rendering = false;
            int index = gui.getSize()-1;
            for (int i = users.length-1; 0 <= i; i--) {
                User user = users[i];
                if (rendering) {
                    // guiにレンダリングする
                    if (index < 18) {
                        // レンダリングが完了したなら
                        return;
                    }
                    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    OfflinePlayer offlinePlayer = user.getOfflinePlayer();
                    meta.setDisplayName(ChatColor.RESET + user.getMcid() + "に送金する");
                    meta.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GRAY + "Click: 1 perica", ChatColor.RESET.toString() + ChatColor.GRAY + "Shift+Click: " + user.getDebt() + " perica (負債全額)", ChatColor.RESET.toString() + ChatColor.RED + "送金分はあなたの残高から引き落とされます。"));
                    meta.setOwningPlayer(offlinePlayer);
                    item.setItemMeta(meta);
                    gui.setItem(index, item);
                    index--;
                }else {
                    // レンダリング位置を判定する
                    if (user.getMcid().equals(trigger)) {
                        // トリガーに到達したなら
                        rendering = true;
                        Gui.clear(gui);
                    }
                }
            }
        });
        request.start();
    }

    public static void onNextPage(InventoryClickEvent e) {
        /* 次のページを開く */
        Inventory gui = e.getClickedInventory();
        ItemStack triggerItem = gui.getItem(gui.getSize()-1);
        if (triggerItem == null) {
            // トリガーが存在しないなら
            return;
        }
        SkullMeta triggerMeta = (SkullMeta) triggerItem.getItemMeta();
        String trigger = triggerMeta.getOwningPlayer().getName();
        User[] users = User.getValues();
        if (trigger.equals(users[users.length-1].getMcid())) {
            // 次のページが存在しないなら
            return;
        }
        Thread request = new Thread(() -> {
            // mojangにPlayerHeadを要求するときに発生するスレッドの独占を回避する為、非同期で処理する
            int index = 18;
            boolean rendering = false;
            for (User user : users) {
                if (rendering) {
                    // guiにレンダリングする
                    if (gui.getSize() <= index) {
                        // レンダリングが完了したなら
                        return;
                    }
                    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    OfflinePlayer offlinePlayer = user.getOfflinePlayer();
                    meta.setDisplayName(ChatColor.RESET + user.getMcid() + "に送金する");
                    meta.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GRAY + "Click: 1 perica", ChatColor.RESET.toString() + ChatColor.GRAY + "Shift+Click: " + user.getDebt() + " perica (負債全額)", ChatColor.RESET.toString() + ChatColor.RED + "送金分はあなたの残高から引き落とされます。"));
                    meta.setOwningPlayer(offlinePlayer);
                    item.setItemMeta(meta);
                    gui.setItem(index, item);
                    index++;
                }else {
                    // レンダリング位置を判定する
                    if (user.getMcid().equals(trigger)) {
                        rendering = true;
                        Gui.clear(gui);
                    }
                }
            }
        });
        request.start();
    }

    public static void onSend(InventoryClickEvent e) {
        /* 送金が行われたとき */
        if (e.getRawSlot() < 18) {
            return;
        }
        ItemStack item = e.getCurrentItem();
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        User from = new User((Player) e.getWhoClicked());
        User to = new User(meta.getOwningPlayer());
        if (from.getMcid().equals(to.getMcid())) {
            // ユーザが自分のことをさすなら
            from.getPlayer().sendMessage(ChatColor.RED + "自分自身に送金することはできません！");
            return;
        }else if (! to.getOfflinePlayer().isBanned()) {
            // ユーザがBanされていないなら
            from.getPlayer().sendMessage(ChatColor.RED + "Banされていないユーザに送金することはできません！");
            return;
        }else if (to.getDebt() < 1) {
            // 負債がないなら
            from.getPlayer().sendMessage(ChatColor.RED + "このユーザには負債がありません！");
            return;
        }
        // 総金額を演算する
        int perica = 1;
        switch (e.getClick()) {
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                perica = to.getDebt();
        }
        if (from.getBalance() < perica) {
            // 支払いできないなら
            return;
        }
        // 決済処理を行う
        from.addBalance(-perica);
        to.addDebt(-perica);
        // 表示情報を更新する
        OfflinePlayer offlinePlayer = to.getOfflinePlayer();
        User user = new User(offlinePlayer);
        meta.setDisplayName(ChatColor.RESET + user.getMcid() + "に送金する");
        meta.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GRAY + "Click: 1 perica", ChatColor.RESET.toString() + ChatColor.GRAY + "Shift+Click: " + user.getDebt() + " perica (負債全額)", ChatColor.RESET.toString() + ChatColor.RED + "送金分はあなたの残高から引き落とされます。"));
        meta.setOwningPlayer(offlinePlayer);
        item.setItemMeta(meta);
    }
}
