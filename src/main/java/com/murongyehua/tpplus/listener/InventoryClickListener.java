package com.murongyehua.tpplus.listener;

import com.murongyehua.tpplus.Tpplus;
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
        p.chat(String.format("/tp %s", Tpplus.tpList.get(tpId).getLocation()));
        p.closeInventory();
        e.setCancelled(true);
    }

}
