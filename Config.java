package com.example.clientboost;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ClientBoostMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENTITY_CULLING = B
            .comment("Skip rendering living entities that are farther than the distance below.")
            .define("entityCulling.enabled", true);
    private static final ForgeConfigSpec.IntValue ENTITY_CULL_DISTANCE = B
            .comment("Max render distance (blocks) for living entities.")
            .defineInRange("entityCulling.distance", 32, 8, 512);
    private static final ForgeConfigSpec.BooleanValue CULL_PLAYERS = B
            .comment("Also cull other players (off by default so you always see people).")
            .define("entityCulling.cullPlayers", false);
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> CULL_WHITELIST = B
            .comment("Entity IDs that are never culled (e.g. big bosses).")
            .defineList("entityCulling.whitelist",
                    List.of("minecraft:ender_dragon", "minecraft:wither", "minecraft:giant"),
                    o -> o instanceof String);

    private static final ForgeConfigSpec.BooleanValue NAMETAG_CULLING = B
            .comment("Skip rendering name tags beyond the distance below.")
            .define("nametagCulling.enabled", true);
    private static final ForgeConfigSpec.IntValue NAMETAG_DISTANCE = B
            .comment("Max distance (blocks) for name tags.")
            .defineInRange("nametagCulling.distance", 16, 4, 64);

    private static final ForgeConfigSpec.BooleanValue UNFOCUSED_LIMIT = B
            .comment("Lower the FPS cap while the game window is not focused.")
            .define("unfocusedLimiter.enabled", false);
    private static final ForgeConfigSpec.IntValue UNFOCUSED_FPS = B
            .comment("FPS cap used while unfocused.")
            .defineInRange("unfocusedLimiter.fps", 15, 1, 120);

    private static final ForgeConfigSpec.BooleanValue DYNAMIC_RD = B
            .comment("Automatically lower render distance when FPS is low and restore it when FPS recovers.",
                    "Your own render distance setting is treated as the maximum.")
            .define("dynamicRenderDistance.enabled", true);
    private static final ForgeConfigSpec.IntValue TARGET_FPS = B
            .comment("If FPS stays below this, render distance is lowered by 1 chunk.")
            .defineInRange("dynamicRenderDistance.targetFps", 30, 10, 240);
    private static final ForgeConfigSpec.IntValue MIN_RD = B
            .comment("Never go below this render distance (chunks).")
            .defineInRange("dynamicRenderDistance.minChunks", 4, 2, 32);

    public static final ForgeConfigSpec SPEC = B.build();

    // Cached values (read every frame, so avoid config lookups in hot paths)
    public static volatile boolean entityCulling = true;
    public static volatile double entityCullDistSq = 32 * 32;
    public static volatile boolean cullPlayers = false;
    public static volatile Set<String> whitelist = Set.of();
    public static volatile boolean nametagCulling = true;
    public static volatile double nametagDistSq = 16 * 16;
    public static volatile boolean unfocusedLimit = false;
    public static volatile int unfocusedFps = 15;
    public static volatile boolean dynamicRd = true;
    public static volatile int targetFps = 30;
    public static volatile int minRd = 4;

    @SubscribeEvent
    public static void onConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) return;
        entityCulling = ENTITY_CULLING.get();
        int d = ENTITY_CULL_DISTANCE.get();
        entityCullDistSq = (double) d * d;
        cullPlayers = CULL_PLAYERS.get();
        whitelist = new HashSet<>(CULL_WHITELIST.get());
        nametagCulling = NAMETAG_CULLING.get();
        int n = NAMETAG_DISTANCE.get();
        nametagDistSq = (double) n * n;
        unfocusedLimit = UNFOCUSED_LIMIT.get();
        unfocusedFps = UNFOCUSED_FPS.get();
        dynamicRd = DYNAMIC_RD.get();
        targetFps = TARGET_FPS.get();
        minRd = MIN_RD.get();
    }
}
