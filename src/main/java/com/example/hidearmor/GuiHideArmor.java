package com.example.hidearmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;

import java.util.ArrayList;
import java.util.List;

public class GuiHideArmor extends GuiScreen {
    private List<String> targets = new ArrayList<String>();
    private int currentTargetIndex = 0;

    private GuiButton btnPrev, btnNext;
    private GuiButton btnHelmet, btnChest, btnLegs, btnBoots;
    private GuiButton btnHideAll, btnShowAll;
    private GuiButton btnArrows, btnIcons;

    @Override
    public void initGui() {
        targets.clear();
        targets.add("Global"); 
        if (Minecraft.getMinecraft().getNetHandler() != null) {
            for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                targets.add(info.getGameProfile().getName());
            }
        }

        int cx = this.width / 2;
        int cy = this.height / 2;

        this.buttonList.add(btnPrev = new GuiButton(0, cx - 100, cy - 50, 20, 20, "<"));
        this.buttonList.add(btnNext = new GuiButton(1, cx + 80, cy - 50, 20, 20, ">"));

        this.buttonList.add(btnHelmet = new GuiButton(5, cx - 60, cy - 25, 120, 20, ""));
        this.buttonList.add(btnChest = new GuiButton(4, cx - 60, cy - 2, 120, 20, ""));
        this.buttonList.add(btnLegs = new GuiButton(3, cx - 60, cy + 21, 120, 20, ""));
        this.buttonList.add(btnBoots = new GuiButton(2, cx - 60, cy + 44, 120, 20, ""));
        this.buttonList.add(btnArrows = new GuiButton(8, cx - 60, cy + 67, 120, 20, ""));
        this.buttonList.add(btnIcons = new GuiButton(9, cx - 60, cy + 90, 120, 20, ""));
        
        this.buttonList.add(btnHideAll = new GuiButton(6, cx - 60, cy + 115, 58, 20, "Hide All"));
        this.buttonList.add(btnShowAll = new GuiButton(7, cx + 2, cy + 115, 58, 20, "Show All"));

        updateButtons();
    }

    private void updateButtons() {
        String target = targets.get(currentTargetIndex);
        boolean[] settings = ConfigHandler.getSettings(target);
        
        btnHelmet.displayString = "Helmet: " + (settings[3] ? "§cHidden" : "§aShown");
        btnChest.displayString = "Chestplate: " + (settings[2] ? "§cHidden" : "§aShown");
        btnLegs.displayString = "Leggings: " + (settings[1] ? "§cHidden" : "§aShown");
        btnBoots.displayString = "Boots: " + (settings[0] ? "§cHidden" : "§aShown");
        btnArrows.displayString = "Stuck Arrows: " + (settings[4] ? "§cHidden" : "§aShown");
        btnIcons.displayString = "Armor Icons: " + (settings[5] ? "§cHidden" : "§aShown");
    }

    private void spawnParticles(String target, boolean isHidden) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;
        
        EntityPlayer player = null;
        if (target.equals("Global")) {
            player = mc.thePlayer;
        } else {
            player = mc.theWorld.getPlayerEntityByName(target);
        }

        if (player != null) {
            for (int i = 0; i < 50; i++) {
                double x = player.posX + (mc.theWorld.rand.nextDouble() - 0.5D) * player.width * 2.5D;
                double y = player.posY + mc.theWorld.rand.nextDouble() * player.height * 1.5D;
                double z = player.posZ + (mc.theWorld.rand.nextDouble() - 0.5D) * player.width * 2.5D;
                
                double vx = (mc.theWorld.rand.nextDouble() - 0.5D) * 0.2D;
                double vy = (mc.theWorld.rand.nextDouble() - 0.5D) * 0.2D;
                double vz = (mc.theWorld.rand.nextDouble() - 0.5D) * 0.2D;

                if (isHidden) {
                    mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, vx, vy, vz);
                    if (i % 2 == 0) mc.theWorld.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, vx * 0.5, vy * 0.5, vz * 0.5);
                } else {
                    mc.theWorld.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, x, y, z, vx, vy, vz);
                    if (i % 2 == 0) mc.theWorld.spawnParticle(EnumParticleTypes.CRIT_MAGIC, x, y, z, vx * 1.5, vy * 1.5, vz * 1.5);
                }
            }
            if (isHidden) mc.theWorld.playSound(player.posX, player.posY, player.posZ, "random.fizz", 0.8F, 0.8F, false);
            else mc.theWorld.playSound(player.posX, player.posY, player.posZ, "random.orb", 0.5F, 1.0F, false);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        String target = targets.get(currentTargetIndex);
        
        if (button.id == 0) { 
            currentTargetIndex--;
            if (currentTargetIndex < 0) currentTargetIndex = targets.size() - 1;
            updateButtons();
        } else if (button.id == 1) { 
            currentTargetIndex++;
            if (currentTargetIndex >= targets.size()) currentTargetIndex = 0;
            updateButtons();
        } else if ((button.id >= 2 && button.id <= 5) || button.id == 8 || button.id == 9) { 
            int slot = button.id == 9 ? 5 : button.id == 8 ? 4 : button.id - 2; 
            boolean current = ConfigHandler.getSettings(target)[slot];
            boolean newValue = !current;
            ConfigHandler.setSetting(target, slot, newValue); 
            
            spawnParticles(target, newValue);
            updateButtons();
        } else if (button.id == 6) { 
            for (int i = 0; i < 5; i++) ConfigHandler.setSetting(target, i, true);
            ConfigHandler.setSetting(target, 5, false);
            spawnParticles(target, true);
            updateButtons();
        } else if (button.id == 7) { 
            for (int i = 0; i < 5; i++) ConfigHandler.setSetting(target, i, false);
            ConfigHandler.setSetting(target, 5, true);
            spawnParticles(target, false);
            updateButtons();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Hide Armor & Effects", this.width / 2, 20, 0xFFFFFF);
        
        String target = targets.get(currentTargetIndex);
        String displayTarget = target.equals("Global") ? "Target: §eGLOBAL (全員)" : "Target: §b" + target;
        this.drawCenteredString(this.fontRendererObj, displayTarget, this.width / 2, this.height / 2 - 45, 0xFFFFFF);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}