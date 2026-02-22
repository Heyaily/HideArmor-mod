package com.example.hidearmor;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class HideArmorCommand extends CommandBase {
    @Override
    public String getCommandName() { return "hidearmor"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/hidearmor"; }

    @Override
    public int getRequiredPermissionLevel() { return 0; } 

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        HideArmorMod.guiToOpen = new GuiHideArmor();
    }
}