package com.example.hidearmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry; 
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = HideArmorMod.MODID, version = HideArmorMod.VERSION, acceptedMinecraftVersions = "[1.8.9]", acceptableRemoteVersions = "*")
public class HideArmorMod {
    public static final String MODID = "hidearmor";
    public static final String VERSION = "1.0";

    public static GuiScreen guiToOpen = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ArmorRenderHandler());
        
        MinecraftForge.EVENT_BUS.register(new ArmorIconRenderer());
        
        MinecraftForge.EVENT_BUS.register(new KeyHandler());
        ClientRegistry.registerKeyBinding(KeyHandler.openGuiKey);
        
        FMLCommonHandler.instance().bus().register(this);
        ClientCommandHandler.instance.registerCommand(new HideArmorCommand());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (guiToOpen != null && Minecraft.getMinecraft().currentScreen == null) {
            Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
            guiToOpen = null;
        }
    }
}