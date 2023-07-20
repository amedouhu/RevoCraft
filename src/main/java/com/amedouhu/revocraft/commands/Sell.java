package com.amedouhu.revocraft.commands;

import com.amedouhu.revocraft.utils.Gui;
import com.amedouhu.revocraft.utils.Price;
import com.amedouhu.revocraft.utils.User;
import com.amedouhu.revocraft.utils.Yaml;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Sell implements CommandExecutor {
    /* sellコマンドの処理クラス */
    private static final Yaml yaml = new Yaml("commands.sell");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /* コマンドが実行されたとき */
        if (! (sender instanceof Player)) {
            // 送信者がプレイヤーではないなら
            sender.sendMessage("このコマンドはマインクラフト内からのみ実行できます。");
            return false;
        }
        Player player = (Player) sender;
        Inventory gui = Gui.get("/sell", player);
        final boolean search = args.length==1;
        if (search) {
            // 検索が有効なら
            try {
                // 検索語句に一致するアイテムの取得を試みる
                ItemStack item18 = new ItemStack(Material.matchMaterial(args[0]));
                if (Price.getSell(item18) == 0) {
                    // 価格が設定されていないなら
                    throw new Exception();
                }
                ItemMeta meta18 = item18.getItemMeta();
                meta18.setDisplayName(ChatColor.RESET.toString() + ChatColor.RED  + ChatColor.BOLD + "プレビューアイテム");
                meta18.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GRAY + "売却価格: " + Price.getSell(item18) + " perica", ChatColor.RESET.toString() + ChatColor.RED + "プレビューアイテムを売却することはできません！"));
                item18.setItemMeta(meta18);
                gui.setItem(18, item18);
            } catch (Exception e) {
                // 検索語句に一致するアイテムの取得に失敗したなら
            }
        }
        ItemStack item49 = new ItemStack(Material.ANVIL);
        ItemMeta meta49 = item49.getItemMeta();
        meta49.setDisplayName(ChatColor.RESET.toString() + ChatColor.BOLD + "売却");
        meta49.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GRAY + "アイテムを売却します。", ChatColor.RESET.toString() + ChatColor.GRAY + "売却額: 0 perica"));
        item49.setItemMeta(meta49);
        gui.setItem(49, item49);

        player.openInventory(gui);
        return true;
    }

    public static void onRenderingPage(InventoryClickEvent e) {
        /* ページのレンダリングが要求されたとき */
        Player player = (Player) e.getWhoClicked();
        Inventory gui = player.getOpenInventory().getTopInventory();
        ItemStack item = e.getCurrentItem();
        final int slot = e.getRawSlot();
        switch (e.getClickedInventory().getType()) {
            case CHEST:
                // クリックされたのがチェスト型なら
                if (! (9<=slot) && (slot<gui.getSize()-9)) {
                    // クリックされたのが共通スロットまたはシステムスロットなら
                    return;
                }
                if (item.getItemMeta().hasDisplayName()) {
                    // プレビューアイテムなら
                    return;
                }
                gui.clear(e.getRawSlot());
                player.getInventory().addItem(item);
                onReloadPage(e);
                break;
            case PLAYER:
                // クリックされたのがプレイヤー型なら
                if (Price.getSell(item) == 0) {
                    // 買い取り額が0なら
                    return;
                }
                for (int i=18; i<gui.getSize()-9; i++) {
                    if (gui.getItem(i) != null) {
                        // アイテムが存在するなら
                        continue;
                    }
                    e.setCurrentItem(new ItemStack(Material.AIR));
                    gui.setItem(i, item);
                    break;
                }
                onReloadPage(e);
                break;
        }
    }

    public static void onReloadPage(InventoryClickEvent e) {
        /* ページの再読み込みが要求されたとき */
        Player player = (Player) e.getWhoClicked();
        Inventory gui = Gui.reload(player.getOpenInventory().getTopInventory(), player);

        int perica = 0;
        for (int i=18; i<gui.getSize()-9; i++) {
            ItemStack item = gui.getItem(i);
            if (item == null) {
                // アイテムが存在しないなら
                continue;
            }
            if (item.getItemMeta().hasDisplayName()) {
                // プレビューアイテムなら
                continue;
            }
            perica += Price.getSell(item) * item.getAmount();
        }

        ItemStack item49 = new ItemStack(Material.ANVIL);
        ItemMeta meta49 = item49.getItemMeta();
        meta49.setDisplayName(ChatColor.RESET.toString() + ChatColor.BOLD + "売却");
        meta49.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GRAY + "アイテムを売却します。", ChatColor.RESET.toString() + ChatColor.GRAY + "売却額: " + perica + " perica"));
        item49.setItemMeta(meta49);
        gui.setItem(49, item49);
    }

    public static void onClosePage(InventoryCloseEvent e) {
        /* ページが閉じられたとき */
        Player player = (Player) e.getPlayer();
        Inventory gui = e.getInventory();
        Inventory playerInventory = player.getInventory();
        for (int i=18; i<gui.getSize()-9; i++) {
            ItemStack item = gui.getItem(i);
            if (item == null) {
                // アイテムが存在しないならなら
                continue;
            }
            if (item.getItemMeta().hasDisplayName()) {
                // プレビューアイテムなら
                continue;
            }
            playerInventory.addItem(gui.getItem(i));
        }
    }

    public static void onSell(InventoryClickEvent e) {
        /* アイテムが売却されたとき */
        Player player = (Player) e.getWhoClicked();
        User user = new User(player);
        Inventory gui = player.getOpenInventory().getTopInventory();
        // 売却の決済処理を行う
        for (int i=18; i<gui.getSize()-9; i++) {
            ItemStack item = gui.getItem(i);
            if (item == null) {
                // アイテムが存在しないなら
                continue;
            }
            if (item.getItemMeta().hasDisplayName()) {
                // プレビューアイテムなら
                continue;
            }
            final int price = Price.getSell(item) * item.getAmount();
            if (getMaxBalance() < user.getBalance() + price) {
                // 最大売却額を超えるなら
                return;
            }
            user.addBalance(price);
            gui.clear(i);
        }
        onReloadPage(e);
    }

    public static int getMaxBalance() {
        /* 残高の最大値を返す */
        return yaml.get().getInt("max_balance");
    }
}
