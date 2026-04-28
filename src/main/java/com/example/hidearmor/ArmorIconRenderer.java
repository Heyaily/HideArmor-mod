package com.example.hidearmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ArmorIconRenderer {
    @SubscribeEvent
    public void onRenderSpecialsPost(RenderLivingEvent.Specials.Post event) {
        EntityLivingBase entity = event.entity;
        Minecraft mc = Minecraft.getMinecraft();

        if (entity == mc.thePlayer) {
            if (mc.gameSettings.thirdPersonView == 0 || mc.currentScreen instanceof GuiInventory) return;
        } else if (mc.thePlayer.getDistanceToEntity(entity) > 10.0F) {
            return;
        }

        boolean[] settings;
        if (entity instanceof EntityPlayer) {
            boolean[] global = ConfigHandler.getSettings("Global");
            boolean[] specific = ConfigHandler.getSettings(entity.getName());
            settings = new boolean[6];
            for (int i = 0; i < 6; i++) settings[i] = global[i] || specific[i];
        } else {
            settings = ConfigHandler.getSettings("Mobs");
        }

        if (settings[5]) return;

        List<ItemStack> hiddenArmors = new ArrayList<ItemStack>();
        ItemStack[] backup = ArmorRenderHandler.backups.get(entity.getUniqueID());

        if (backup != null) {
            for (int i = 3; i >= 0; i--) {
                if (settings[i] && backup[i] != null) {
                    hiddenArmors.add(backup[i]);
                }
            }
        }

        if (hiddenArmors.isEmpty()) return;

        double x = event.x;
        double z = event.z;
        
        double y = event.y + (double)entity.height + 0.5D;

        if (entity instanceof EntityPlayer) {
            Scoreboard scoreboard = ((EntityPlayer)entity).getWorldScoreboard();
            ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(2);

            if (objective != null) {
                y += (double)(9.0F * 1.15F * 0.02666667F);
            }
        }

        if (entity.isSneaking()) y -= 0.125D;

        RenderManager renderManager = mc.getRenderManager();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.3D, z); 
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F) * renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        float scale = 0.4F;
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        float spacing = 1.2F; 
        float startX = -((hiddenArmors.size() - 1) * spacing) / 2.0F;

        for (int i = 0; i < hiddenArmors.size(); i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(startX + (i * spacing), 0, 0);
            mc.getRenderItem().renderItem(hiddenArmors.get(i), ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}