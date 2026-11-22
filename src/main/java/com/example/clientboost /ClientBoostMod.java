package com.example.clientboost;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(ClientBoostMod.MODID)
public class ClientBoostMod {
    public static final String MODID = "clientboost";

    public ClientBoostMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }
}
