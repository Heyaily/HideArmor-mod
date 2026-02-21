package com.example.hidearmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
        if (player == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) return;

        boolean[] global = ConfigHandler.getSettings("Global");
        boolean[] playerSpecific = ConfigHandler.getSettings(player.getName());

        List<ItemStack> hiddenArmors = new ArrayList<ItemStack>();
        for (int i = 3; i >= 0; i--) {
            boolean hide = global[i] || playerSpecific[i];
            ItemStack stack = player.inventory.armorInventory[i];
            
            if (hide && stack != null) {
                hiddenArmors.add(stack);
            }
        }

        if (hiddenArmors.isEmpty()) return;

        RenderManager renderManager = mc.getRenderManager();
        double x = event.x;
        double y = event.y + player.height + 0.9D; 
        if (player.isSneaking()) y -= 0.25D; 
        double z = event.z;

        float viewerYaw = renderManager.playerViewY;
        float viewerPitch = renderManager.playerViewX;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(viewerPitch, 1.0F, 0.0F, 0.0F);
        
        float scale = 0.02F; 
        GlStateManager.scale(-scale, -scale, scale);
        
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth(); 
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        int width = hiddenArmors.size() * 16;
        int startX = -width / 2;

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < hiddenArmors.size(); i++) {
            ItemStack stack = hiddenArmors.get(i);
            int drawX = startX + i * 16;
            int drawY = -8; 
            
            mc.getRenderItem().renderItemIntoGUI(stack, drawX, drawY);
        }

        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}