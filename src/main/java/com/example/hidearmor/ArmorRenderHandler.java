package com.example.hidearmor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorRenderHandler {
    public static Map<UUID, ItemStack[]> backups = new HashMap<UUID, ItemStack[]>();
    public static Map<UUID, Byte> arrowBackups = new HashMap<UUID, Byte>();

    @SubscribeEvent
    public void onRenderPre(RenderLivingEvent.Pre event) {
        EntityLivingBase entity = event.entity;
        boolean[] settings;

        if (entity instanceof EntityPlayer) {
            boolean[] global = ConfigHandler.getSettings("Global");
            boolean[] specific = ConfigHandler.getSettings(entity.getName());
            settings = new boolean[6];
            for (int i = 0; i < 6; i++) settings[i] = global[i] || specific[i];
        } else {
            settings = ConfigHandler.getSettings("Mobs");
        }

        boolean modified = false;
        ItemStack[] backup = new ItemStack[4];

        for (int i = 0; i < 4; i++) {
            if (settings[i]) {
                ItemStack stack = entity.getEquipmentInSlot(i + 1);
                if (stack != null) {
                    backup[i] = stack;
                    entity.setCurrentItemOrArmor(i + 1, null);
                    modified = true;
                }
            }
        }
        if (modified) backups.put(entity.getUniqueID(), backup);

        if (entity instanceof EntityPlayer && settings[4]) {
            byte arrowCount = entity.getDataWatcher().getWatchableObjectByte(9);
            if (arrowCount > 0) {
                entity.getDataWatcher().updateObject(9, (byte)0);
                arrowBackups.put(entity.getUniqueID(), arrowCount);
            }
        }
    }

    @SubscribeEvent
    public void onRenderPost(RenderLivingEvent.Post event) {
        EntityLivingBase entity = event.entity;
        ItemStack[] backup = backups.remove(entity.getUniqueID());
        if (backup != null) {
            for (int i = 0; i < 4; i++) {
                if (backup[i] != null) {
                    entity.setCurrentItemOrArmor(i + 1, backup[i]);
                }
            }
        }
        Byte arrowCount = arrowBackups.remove(entity.getUniqueID());
        if (arrowCount != null) {
            entity.getDataWatcher().updateObject(9, arrowCount);
        }
    }
}