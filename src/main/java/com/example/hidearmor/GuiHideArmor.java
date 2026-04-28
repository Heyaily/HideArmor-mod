package com.example.hidearmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiHideArmor extends GuiScreen {
    private List<String> playerTargets = new ArrayList<String>();
    private int currentTargetIndex = 0;
    private boolean isMobMode = false;

    private GuiButton btnMode, btnPrev, btnNext;
    private GuiButton btnHelmet, btnChest, btnLegs, btnBoots, btnArrows, btnIcons;
    private GuiButton btnHideAll, btnShowAll;

    @Override
    public void initGui() {
        playerTargets.clear();
        playerTargets.add("Global"); 
        if (Minecraft.getMinecraft().getNetHandler() != null) {
            for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                playerTargets.add(info.getGameProfile().getName());
            }
        }

        int cx = this.width / 2;
        int cy = this.height / 2;
        int startY = cy - 100;

        this.buttonList.add(btnMode = new GuiButton(10, cx - 75, startY, 150, 20, "Mode: Players"));
        this.buttonList.add(btnPrev = new GuiButton(0, cx - 100, startY + 25, 20, 20, "<"));
        this.buttonList.add(btnNext = new GuiButton(1, cx + 80, startY + 25, 20, 20, ">"));
        this.buttonList.add(btnHelmet = new GuiButton(5, cx - 75, startY + 50, 150, 20, "Helmet"));
        this.buttonList.add(btnChest  = new GuiButton(4, cx - 75, startY + 72, 150, 20, "Chestplate"));
        this.buttonList.add(btnLegs   = new GuiButton(3, cx - 75, startY + 94, 150, 20, "Leggings"));
        this.buttonList.add(btnBoots  = new GuiButton(2, cx - 75, startY + 116, 150, 20, "Boots"));
        this.buttonList.add(btnArrows = new GuiButton(8, cx - 75, startY + 138, 150, 20, "Arrows"));
        this.buttonList.add(btnIcons  = new GuiButton(9, cx - 75, startY + 160, 150, 20, "Icons"));
        this.buttonList.add(btnHideAll = new GuiButton(6, cx - 75, startY + 185, 72, 20, "§cHide All"));
        this.buttonList.add(btnShowAll = new GuiButton(7, cx + 3, startY + 185, 72, 20, "§aShow All"));

        updateButtons();
    }

    private void updateButtons() {
        String target = isMobMode ? "Mobs" : playerTargets.get(currentTargetIndex);
        boolean[] settings = ConfigHandler.getSettings(target);

        btnMode.displayString = isMobMode ? "Mode: §eMobs" : "Mode: §bPlayers";
        btnPrev.enabled = btnNext.enabled = !isMobMode;

        btnHelmet.displayString = (settings[3] ? "§c" : "§a") + "Helmet";
        btnChest.displayString  = (settings[2] ? "§c" : "§a") + "Chestplate";
        btnLegs.displayString   = (settings[1] ? "§c" : "§a") + "Leggings";
        btnBoots.displayString  = (settings[0] ? "§c" : "§a") + "Boots";
        btnArrows.displayString = (settings[4] ? "§c" : "§a") + "Arrows";
        btnIcons.displayString  = (settings[5] ? "§c" : "§a") + "Icons";

        if (isMobMode) {
            btnArrows.enabled = false;
            btnArrows.displayString = "§8Arrows";
        } else {
            btnArrows.enabled = true;
        }
    }

    private void playToggleSound(boolean isHiding) {
        if (isHiding) {
            Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1.0F, 1.5F);
        } else {
            Minecraft.getMinecraft().thePlayer.playSound("random.orb", 1.0F, 1.2F);
        }
    }

    private void spawnParticles(String target, boolean hidden) {
        EntityPlayer p = target.equals("Global") || target.equals("Mobs") ? 
                        Minecraft.getMinecraft().thePlayer : 
                        Minecraft.getMinecraft().theWorld.getPlayerEntityByName(target);

        if (p != null) {
            Random rand = new Random();
            for (int i = 0; i < 20; i++) {
                double x = p.posX + (rand.nextDouble() - 0.5D) * p.width;
                double y = p.posY + rand.nextDouble() * p.height;
                double z = p.posZ + (rand.nextDouble() - 0.5D) * p.width;
                EnumParticleTypes pt = hidden ? EnumParticleTypes.SPELL_WITCH : EnumParticleTypes.VILLAGER_HAPPY;
                Minecraft.getMinecraft().theWorld.spawnParticle(pt, x, y, z, 0, 0, 0);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0 || button.id == 1 || button.id == 10) {
            Minecraft.getMinecraft().thePlayer.playSound("random.click", 1.0F, 1.0F);
        }

        if (button.id == 10) {
            isMobMode = !isMobMode;
            updateButtons();
            return;
        }

        if (!isMobMode) {
            if (button.id == 0) {
                currentTargetIndex = (currentTargetIndex - 1 + playerTargets.size()) % playerTargets.size();
                updateButtons();
                return;
            } else if (button.id == 1) {
                currentTargetIndex = (currentTargetIndex + 1) % playerTargets.size();
                updateButtons();
                return;
            }
        }

        String target = isMobMode ? "Mobs" : playerTargets.get(currentTargetIndex);

        if ((button.id >= 2 && button.id <= 5) || button.id == 8 || button.id == 9) {
            int slot = button.id == 9 ? 5 : button.id == 8 ? 4 : button.id == 5 ? 3 : button.id == 4 ? 2 : button.id == 3 ? 1 : 0;
            boolean newValue = !ConfigHandler.getSettings(target)[slot];
            ConfigHandler.setSetting(target, slot, newValue);

            playToggleSound(newValue);
            
            spawnParticles(target, newValue);
            updateButtons();
        } else if (button.id == 6) {
            for (int i = 0; i < 5; i++) ConfigHandler.setSetting(target, i, true);
            ConfigHandler.setSetting(target, 5, false);
            playToggleSound(true);
            spawnParticles(target, true);
            updateButtons();
        } else if (button.id == 7) { 
            for (int i = 0; i < 5; i++) ConfigHandler.setSetting(target, i, false);
            ConfigHandler.setSetting(target, 5, true);
            playToggleSound(false);
            spawnParticles(target, false);
            updateButtons();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Hide Armor Settings", this.width / 2, 20, 0xFFFFFF);
        
        String targetText;
        if (isMobMode) {
            targetText = "Target: §eALL MOBS";
        } else {
            String name = playerTargets.get(currentTargetIndex);
            targetText = name.equals("Global") ? "Target: §eGLOBAL" : "Target: §b" + name;
        }
        this.drawCenteredString(this.fontRendererObj, targetText, this.width / 2, (this.height / 2) - 75, 0xFFFFFF);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}