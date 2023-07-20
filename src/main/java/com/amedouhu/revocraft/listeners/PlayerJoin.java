package com.amedouhu.revocraft.listeners;

import com.amedouhu.revocraft.gimmicks.CountBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;

public class PlayerJoin implements Listener {
    /* PlayerJoinEventの処理クラス */

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        /* PlayerJoinEventが発生したとき */
        Player player = e.getPlayer();
        CountBar.add(player);
        Set<String> scoreboardTags = player.getScoreboardTags();
        for (String scoreboardTag : scoreboardTags) {
            switch (scoreboardTag) {
                case "revocraft.search.buy":
                case "revocraft.search.sell":
                case "revocraft.search.send":
                    player.removeScoreboardTag(scoreboardTag);
            }
        }
    }
}
