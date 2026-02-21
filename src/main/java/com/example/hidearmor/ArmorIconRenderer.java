package com.example.hidearmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityArmorStand;
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
        if (!(event.entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entity;
        Minecraft mc = Minecraft.getMinecraft();
        
        if (player == mc.thePlayer) {
            if (mc.gameSettings.thirdPersonView == 0) return;
            if (mc.currentScreen instanceof GuiInventory) return;
        } else {
            if (mc.thePlayer.getDistanceToEntity(player) > 10.0F) return;
        }

        boolean[] global = ConfigHandler.getSettings("Global");
        boolean[] playerSpecific = ConfigHandler.getSettings(player.getName());

        if (global[5] || playerSpecific[5]) return;

        List<ItemStack> hiddenArmors = new ArrayList<ItemStack>();
        ItemStack[] backup = ArmorRenderHandler.backups.get(player.getUniqueID());

        for (int i = 3; i >= 0; i--) {
            boolean hide = global[i] || playerSpecific[i];
            ItemStack stack = null;
            
            if (hide && backup != null) {
                stack = backup[i];
            } else if (!hide) {
                stack = player.inventory.armorInventory[i];
            }
            
            if (hide && stack != null) {
                hiddenArmors.add(stack);
            }
        }

        if (hiddenArmors.isEmpty()) return;

        double x = event.x;
        double dynamicYOffset = player.height + 0.7D;
        
        Scoreboard scoreboard = player.getWorldScoreboard();
        if (scoreboard != null) {
            ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(2);
            if (objective != null) {
                dynamicYOffset += 0.3D;
            }
        }

        List<EntityArmorStand> stands = mc.theWorld.getEntitiesWithinAABB(EntityArmorStand.class, player.getEntityBoundingBox().expand(1.0D, 3.0D, 1.0D));
        for (EntityArmorStand stand : stands) {
            if (stand.isInvisible() && stand.hasCustomName() && stand.posY >= player.posY) {
                double standOffset = (stand.posY - player.posY) + (stand.isChild() ? 0.5D : 1.975D) + 0.4D;
                if (standOffset > dynamicYOffset) {
                    dynamicYOffset = standOffset;
                }
            }
        }

        double y = event.y + dynamicYOffset; 
        if (player.isSneaking()) y -= 0.25D; 
        double z = event.z;

        RenderManager renderManager = mc.getRenderManager();
        float viewerYaw = renderManager.playerViewY;
        float viewerPitch = renderManager.playerViewX;
        boolean isThirdPersonFrontal = mc.gameSettings.thirdPersonView == 2;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((isThirdPersonFrontal ? -1.0F : 1.0F) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        
        float scale = 0.4F;
        GlStateManager.scale(scale, scale, scale);
        
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        float spacing = 1.0F; 
        float startX = -((hiddenArmors.size() - 1) * spacing) / 2.0F;

        for (int i = 0; i < hiddenArmors.size(); i++) {
            ItemStack stack = hiddenArmors.get(i);
            
            GlStateManager.pushMatrix();
            GlStateManager.translate(startX + (i * spacing), 0, 0);
            
            mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            
            GlStateManager.popMatrix();
        }

        GlStateManager.enableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}