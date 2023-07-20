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
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Buy implements CommandExecutor {
    /* buyコマンドの処理クラス */
    private static final Yaml yaml = new Yaml("commands.buy");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /* コマンドが実行されたとき */
        if (! (sender instanceof Player)) {
            // 送信者がプレイヤーではないなら
            sender.sendMessage("このコマンドはマインクラフト内からのみ実行できます。");
            return false;
        }
        Player player = (Player) sender;
        Inventory gui = Gui.get("/buy", player);
        Map<String, Integer> priceMap = Price.getBuyMap();
        int index = 18;
        final boolean search = args.length==1;
        for (Map.Entry<String, Integer> entry : priceMap.entrySet()) {
            if (gui.getSize() <= index) {
                // レンダリングが完了したなら
                break;
            }
            ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(entry.getKey())));
            if (search) {
                // 検索が有効なら
                if (item.getType().getKey().getKey().equals(args[0])) {
                    // 検索結果と一致するなら
                    index = 18;
                }else {
                    // 検索語句と一致しないなら
                    continue;
                }
            }
            ItemMeta meta = item.getItemMeta();
            Objects.requireNonNull(meta).setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GRAY + "名称: " + entry.getKey(),ChatColor.RESET.toString() + ChatColor.GRAY + "価格: " + entry.getValue() + " perica"));
            item.setItemMeta(meta);
            gui.setItem(index, item);
            index ++;
        }
        player.openInventory(gui);
        return true;
    }

    public static void onBackPage(InventoryClickEvent e) {
        /* 前のページが要求されたとき */
        Inventory gui = e.getInventory();
        ItemStack trigger = gui.getItem(18);
        Map<String, Integer> priceMap = Price.getBuyMap();
        List<String> keyList = new ArrayList<>(priceMap.keySet());
        if (trigger == null) {
            // トリガーが存在しないなら
            return;
        }
        if (trigger.getType().toString().equals(keyList.get(0))) {
            // 前のページが存在しないなら
            return;
        }
        Collections.reverse(keyList);
        int index = gui.getSize()-1;
        boolean rendering = false;
        for (String key : keyList) {
            if (rendering) {
                // guiにレンダリングする
                if (index<18) {
                    // レンダリングが完了したなら
                    return;
                }
                ItemStack item = new ItemStack(Material.getMaterial(key));
                ItemMeta meta = item.getItemMeta();
                meta.setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GRAY + "名称: " + key,ChatColor.RESET.toString() + ChatColor.GRAY + "価格: " + priceMap.get(key) + " perica"));
                item.setItemMeta(meta);
                gui.setItem(index, item);
                index --;
            }else {
                // レンダリング位置を判定する
                if (trigger.getType().toString().equals(key)) {
                    // トリガーに到達したなら
                    rendering = true;
                    Gui.clear(gui);
                }
            }
        }
    }

    public static void onNextPage(InventoryClickEvent e) {
        /* 次のページが要求されたとき */
        Inventory gui = e.getClickedInventory();
        ItemStack trigger = gui.getItem(gui.getSize()-1);
        Map<String, Integer> priceMap = Price.getBuyMap();
        List<String> keyList = new ArrayList<>(priceMap.keySet());
        if (trigger == null) {
            // トリガーが存在しないなら
            return;
        }
        if (trigger.getType().toString().equals(keyList.get(keyList.size()-1))) {
            // 次のページが存在しないなら
            return;
        }
        int index = 18;
        boolean rendering = false;
        for (Map.Entry<String, Integer> entry : priceMap.entrySet()) {
            if (rendering) {
                // guiにレンダリングする
                if (gui.getSize() <= index) {
                    // レンダリングが完了したなら
                    return;
                }
                ItemStack item = new ItemStack(Material.getMaterial(entry.getKey()));
                ItemMeta meta = item.getItemMeta();
                Objects.requireNonNull(meta).setLore(Arrays.asList(ChatColor.RESET.toString() + ChatColor.GRAY + "名称: " + entry.getKey(),ChatColor.RESET.toString() + ChatColor.GRAY + "価格: " + entry.getValue() + " perica"));
                item.setItemMeta(meta);
                gui.setItem(index, item);
                index ++;
            }else {
                // レンダリング位置を判定する
                if (trigger.getType().toString().equals(entry.getKey())) {
                    // トリガーに到達したなら
                    rendering = true;
                    Gui.clear(gui);
                }
            }
        }
    }

    public static void onBuy(InventoryClickEvent e) {
        /* アイテムがの購入が要求されたとき */
        if (e.getRawSlot() < 18) {
            // クリックされたのが共通スロットなら
            return;
        }
        // 購入個数を演算する
        int amount = 1;
        switch (e.getClick()) {
            case MIDDLE:
            case SHIFT_RIGHT:
            case SHIFT_LEFT:
                amount = 64;
                break;
        }
        // 購入の決済を行う
        Player player = (Player) e.getWhoClicked();
        User user = new User(player);
        ItemStack item = new ItemStack(Material.getMaterial(e.getCurrentItem().getType().toString()), amount);
        final int price = Price.getBuy(item) * amount;
        if (getMaxDebt() < user.getDebt() + price) {
            // 最大負債額を超えるなら
            return;
        }
        user.addDebt(price);
        player.getInventory().addItem(item);
        // Guiを更新する
        Gui.reload(e.getInventory(), player);
    }

    public static int getMaxDebt() {
        /* 負債の最大値を返す */
        return yaml.get().getInt("max_debt");
    }
}
