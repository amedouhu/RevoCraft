package com.amedouhu.revocraft.listeners;

import com.amedouhu.revocraft.utils.User;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLogin implements Listener {
    /* PlayerLoginEventの処理クラス */

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        /* PlayerLoginEventが発生したとき */
        Player player = e.getPlayer();
        User user = new User(player);
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        if (! banList.isBanned(player.getName())) {
            // Banされていないなら
            return;
        }
        if (! banList.getBanEntry(player.getName()).getSource().equals("revocraft")) {
            // 発行元がRevoCraftではないなら
            return;
        }
        if (0<user.getDebt()) {
            // 負債が残っているなら
            return;
        }
        // Banを解除する
        banList.pardon(player.getName());
        e.setKickMessage("振込が確認された為、Banを解除しました。再度ログインしてください。");
    }
}
