package com.amedouhu.revocraft.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.Collections;

public class Gui {
    /* Guiの実装クラス */

    public static Inventory get(String title, Player player) {
        /* Guiを取得する */
        Inventory gui = Bukkit.createInventory(null, 54, title);
        return reload(gui, player);
    }

    public static Inventory clear(Inventory gui) {
        /* Guiをクリアする */
        for (int index=18; index<gui.getSize(); index++) {
            gui.clear(index);
        }
        return gui;
    }

    public static Inventory reload(Inventory gui, Player player) {
        /* Guiを更新する */
        if (gui.getSize() != 54) {
            // サイズが規定外なら
            return null;
        }
        for (int i=0; i<18; i++) {
            // Guiの共通部分を初期化する
            gui.clear(i);
        }

        User user = new User(player);

        ItemStack item0 = new ItemStack(Material.ARROW);
        ItemMeta meta0 = item0.getItemMeta();
        meta0.setDisplayName(ChatColor.RESET.toString() + ChatColor.WHITE + ChatColor.BOLD + "戻る");
        meta0.setLore(Collections.singletonList(ChatColor.RESET.toString() + ChatColor.GRAY + "前のページを開きます。"));
        item0.setItemMeta(meta0);
        gui.setItem(0, item0);

        ItemStack item3 = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta3 = item3.getItemMeta();
        meta3.setDisplayName(ChatColor.RESET.toString() + ChatColor.WHITE + ChatColor.BOLD + "検索");
        meta3.setLore(Collections.singletonList(ChatColor.RESET.toString() + ChatColor.GRAY + "ページを検索します。"));
        item3.setItemMeta(meta3);
        gui.setItem(3, item3);

        ItemStack item4 = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta4 = (SkullMeta) item4.getItemMeta();
        meta4.setDisplayName(ChatColor.RESET.toString() + ChatColor.GOLD + ChatColor.BOLD + user.getMcid());
        Thread request = new Thread(() -> {
            /*mojangにPlayerHeadを要求するときに発生するスレッドの独占を回避する為、非同期で処理する*/
            meta4.setOwningPlayer(user.getOfflinePlayer());
            meta4.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GRAY + "残高: " + user.getBalance() + " perica",ChatColor.RESET.toString() + ChatColor.GRAY + "負債: " + user.getDebt() + " perica"));
            meta4.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
            meta4.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item4.setItemMeta(meta4);
            gui.setItem(4, item4);
        });
        request.start();

        ItemStack item5 = new ItemStack(Material.CHEST);
        ItemMeta meta5 = item5.getItemMeta();
        meta5.setDisplayName(ChatColor.RESET.toString() + ChatColor.WHITE + ChatColor.BOLD + "閉じる");
        meta5.setLore(Collections.singletonList(ChatColor.RESET.toString() + ChatColor.GRAY + "ページを閉じます。"));
        item5.setItemMeta(meta5);
        gui.setItem(5, item5);

        ItemStack item8 = new ItemStack(Material.ARROW);
        ItemMeta meta8 = item8.getItemMeta();
        meta8.setDisplayName(ChatColor.RESET.toString() + ChatColor.WHITE + ChatColor.BOLD + "進む");
        meta8.setLore(Collections.singletonList(ChatColor.RESET.toString() + ChatColor.GRAY + "次のページを開きます。"));
        item8.setItemMeta(meta8);
        gui.setItem(8, item8);

        return gui;
    }
}
