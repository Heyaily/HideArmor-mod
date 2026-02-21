package com.example.hidearmor;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyHandler {
    public static KeyBinding openGuiKey = new KeyBinding("key.hidearmor.opengui", Keyboard.KEY_H, "key.categories.hidearmor");

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openGuiKey.isPressed()) {
            HideArmorMod.guiToOpen = new GuiHideArmor();
        }
    }
}