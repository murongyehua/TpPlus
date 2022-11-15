package com.murongyehua.tpplus.listener;

import com.murongyehua.tpplus.Tpplus;
import com.murongyehua.tpplus.common.TpInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!e.hasBlock()) {
            return;
        }
        Block block = e.getClickedBlock();
        if (block == null) {
            return;
        }
        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            // 判断坐标
            for (TpInfo tpInfo : Tpplus.tpList.values()) {
                String[] locations = tpInfo.getLocation().split(" ");
                Block tempBlock = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(new Location(Bukkit.getWorld("world"), Double.parseDouble(locations[0]), Double.parseDouble(locations[1]) - 1, Double.parseDouble(locations[2])));
                if (tempBlock.getLocation().equals(block.getLocation())) {
                    Player p = e.getPlayer();
                    p.chat("/tpplus");
                }
            }
        }
    }

}
