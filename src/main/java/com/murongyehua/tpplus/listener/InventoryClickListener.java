package com.murongyehua.tpplus.listener;

import com.murongyehua.tpplus.Tpplus;
import com.murongyehua.tpplus.common.TpInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        ItemStack itemStack = e.getCurrentItem();
        if (itemStack == null) {
            return;
        }
        if (itemStack.getItemMeta() == null) {
            return;
        }
        String tpId = itemStack.getItemMeta().getDisplayName();
        Player p = (Player) e.getWhoClicked();
        TpInfo tpInfo = Tpplus.tpList.get(tpId);
        if (tpInfo == null) {
            return;
        }
        String location = tpInfo.getLocation();
        Tpplus tpplus = new Tpplus();
        String[] address = location.split(" ");
        if (!tpplus.judgeLocation(new Location(Bukkit.getWorld("world"), Double.parseDouble(address[0]), Double.parseDouble(address[1]), Double.parseDouble(address[2])))) {
            tpplus.sendMsg(p, "目标传送阵已被破坏，为确保安全，请前往修复后再使用");
            tpplus = null;
            System.gc();
            return;
        }
        tpplus = null;
        System.gc();
        p.chat(String.format("/tp %s", location));
        p.closeInventory();
        e.setCancelled(true);
    }

}
