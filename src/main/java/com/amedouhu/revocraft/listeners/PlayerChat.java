package com.amedouhu.revocraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.Set;

public class PlayerChat implements Listener {
    /* PlayerChatEventの処理クラス */

    @EventHandler
    public void onPlayerChat(PlayerChatEvent e) {
        /* PlayerChatEventが発生したとき */
        Player player = e.getPlayer();
        Set<String> scoreboardTags = player.getScoreboardTags();
        for (String scoreboardTag : scoreboardTags) {
            switch (scoreboardTag) {
                case "revocraft.search.buy":
                case "revocraft.search.sell":
                case "revocraft.search.send":
                    e.setCancelled(true);
                    String[] query = scoreboardTag.split("\\.");
                    player.performCommand(query[2] + " " + e.getMessage());
                    player.removeScoreboardTag(scoreboardTag);
            }
        }
    }
}
