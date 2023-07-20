package com.amedouhu.revocraft.listeners;

import com.amedouhu.revocraft.commands.Sell;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryClose implements Listener {
    /* InventoryCloseEventの処理クラス */

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        /* InventoryCloseEventが発生したとき */
        Player player = (Player) e.getPlayer();
        String title = player.getOpenInventory().getTitle();
        if (! title.equals("/sell")) {
            return;
        }
        Sell.onClosePage(e);
    }
}
