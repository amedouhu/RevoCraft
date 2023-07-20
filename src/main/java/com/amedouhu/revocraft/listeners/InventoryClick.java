package com.amedouhu.revocraft.listeners;

import com.amedouhu.revocraft.commands.Buy;
import com.amedouhu.revocraft.commands.Sell;
import com.amedouhu.revocraft.commands.Send;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.Set;

public class InventoryClick implements Listener {
    /* InventoryClickEventの処理クラス */

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        /* InventoryClickEventが発生したとき */
        Player player = (Player) e.getWhoClicked();
        String title = player.getOpenInventory().getTitle();
        boolean cancel = false;
        if (e.getCurrentItem() == null) {
            // アイテムがクリックされていないなら
            return;
        }
        // 検索タグを付与されているなら
        Set<String> scoreboardTags = player.getScoreboardTags();
        for (String scoreboardTag : scoreboardTags) {
            switch (scoreboardTag) {
                case "revocraft.search.buy":
                case "revocraft.search.sell":
                case "revocraft.search.send":
                    player.removeScoreboardTag(scoreboardTag);
            }
        }
        switch (title) {
            case "/buy":
                cancel = true;
                if (e.getClickedInventory().getType() != InventoryType.CHEST) {
                    // クリックされたのがGuiではないなら
                    break;
                }
                switch (e.getRawSlot()) {
                    case 0:
                        // 前のページを開く
                        Buy.onBackPage(e);
                        break;
                    case 3:
                        // 検索を開く
                        // 既に検索タグを持っているなら
                        player.addScoreboardTag("revocraft.search.buy");
                        player.sendMessage("チャット欄に検索語句を入力してください。アイテムIDの完全一致で検索を行えます。");
                        player.closeInventory();
                        break;
                    case 5:
                        // ページを閉じる
                        player.closeInventory();
                        break;
                    case 8:
                        // 次のページを開く
                        Buy.onNextPage(e);
                        break;
                    default:
                        // アイテムを購入する
                        Buy.onBuy(e);
                }
                break;
            case "/sell":
                cancel = true;
                if (e.getClickedInventory().getType() == InventoryType.CHEST) {
                    // クリックされたのがGuiなら
                    switch (e.getRawSlot()) {
                        case 3:
                            // 検索を開く
                            // 既に検索タグを持っているなら
                            player.addScoreboardTag("revocraft.search.sell");
                            player.sendMessage("チャット欄に検索語句を入力してください。アイテムIDの完全一致で検索を行えます。");
                            player.closeInventory();
                            break;
                        case 5:
                            // ページを閉じる
                            player.closeInventory();
                            break;
                        case 49:
                            // アイテムを売却する
                            Sell.onSell(e);
                            break;
                        default:
                            // アイテムを返す
                            Sell.onRenderingPage(e);
                    }
                }else {
                    // クリックされたのがGuiではないなら
                    // アイテムを追加する
                    Sell.onRenderingPage(e);
                    break;
                }
                break;
            case "/send":
                cancel = true;
                if (e.getClickedInventory().getType() != InventoryType.CHEST) {
                    // クリックされたのがGuiではないなら
                    break;
                }
                switch (e.getRawSlot()) {
                    case 0:
                        // 前のページを開く
                        Send.onBackPage(e);
                        break;
                    case 3:
                        // 検索を開く
                        // 既に検索タグを持っているなら
                        player.addScoreboardTag("revocraft.search.send");
                        player.sendMessage("チャット欄に検索語句を入力してください。プレイヤーIDの完全一致で検索を行えます。");
                        player.closeInventory();
                        break;
                    case 5:
                        // ページを閉じる
                        player.closeInventory();
                        break;
                    case 8:
                        // 次のページを開く
                        Send.onNextPage(e);
                        break;
                    default:
                        Send.onSend(e);
                }
        }
        if (cancel) {
            // イベントのキャンセルが有効なら
            e.setCancelled(true);
        }
    }
}
