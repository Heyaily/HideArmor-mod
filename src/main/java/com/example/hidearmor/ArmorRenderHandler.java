package com.example.hidearmor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorRenderHandler {
    public static Map<UUID, ItemStack[]> backups = new HashMap<UUID, ItemStack[]>();
    public static Map<UUID, Byte> arrowBackups = new HashMap<UUID, Byte>();

    @SubscribeEvent
    public void onRenderPre(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        boolean[] global = ConfigHandler.getSettings("Global");
        boolean[] playerSpecific = ConfigHandler.getSettings(player.getName());

        boolean modified = false;
        ItemStack[] backup = new ItemStack[4];

        for (int i = 0; i < 4; i++) {
            boolean hide = global[i] || playerSpecific[i];
            backup[i] = player.inventory.armorInventory[i];
            
            if (hide && backup[i] != null) {
                player.inventory.armorInventory[i] = null;
                modified = true;
            }
        }
        if (modified) backups.put(player.getUniqueID(), backup);

        boolean hideArrows = global[4] || playerSpecific[4];
        if (hideArrows) {
            byte arrowCount = player.getDataWatcher().getWatchableObjectByte(9);
            if (arrowCount > 0) {
                player.getDataWatcher().updateObject(9, (byte)0);
                arrowBackups.put(player.getUniqueID(), arrowCount);
            }
        }
    }

    @SubscribeEvent
    public void onRenderPost(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.entityPlayer;
        
        ItemStack[] backup = backups.remove(player.getUniqueID());
        if (backup != null) {
            for (int i = 0; i < 4; i++) {
                player.inventory.armorInventory[i] = backup[i];
            }
        }

        Byte arrowCount = arrowBackups.remove(player.getUniqueID());
        if (arrowCount != null) {
            player.getDataWatcher().updateObject(9, arrowCount);
        }
    }
}