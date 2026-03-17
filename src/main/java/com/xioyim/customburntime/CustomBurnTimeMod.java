package com.xioyim.customburntime;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CustomBurnTimeMod.MOD_ID)
public class CustomBurnTimeMod {

    public static final String MOD_ID = "customburntime";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public CustomBurnTimeMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(new BurnTimeEventHandler());
    }

    private void setup(final FMLCommonSetupEvent event) {
        BurnTimeConfig.load();
    }
}
