package com.xioyim.customburntime;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class BurnTimeEventHandler {

    @SubscribeEvent
    public void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        if (registryName == null) return;

        Integer burnTime = BurnTimeConfig.getBurnTime(registryName.toString());
        if (burnTime != null) {
            event.setBurnTime(burnTime);
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
            Commands.literal("customburntime")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("reload")
                    .executes(context -> {
                        BurnTimeConfig.load();
                        context.getSource().sendSuccess(
                            () -> Component.literal("[CustomBurnTime] Config reloaded successfully."),
                            true
                        );
                        return 1;
                    })
                )
        );
    }
}
