package com.example.clientboost;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/** All handlers are client-only (Dist.CLIENT) so this class never loads on a dedicated server. */
@Mod.EventBusSubscriber(modid = ClientBoostMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {

    // ---------- Entity culling ----------

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        if (!Config.entityCulling) return;

        LivingEntity e = event.getEntity();
        Minecraft mc = Minecraft.getInstance();

        if (e == mc.player || e == mc.getCameraEntity()) return;
        if (e instanceof Player && !Config.cullPlayers) return;
        if (e.getBbWidth() >= 3.0F) return;                 // big mobs pop in badly
        if (e.isCurrentlyGlowing()) return;                 // glowing is meant to be seen from afar
        if (mc.player != null && e.hasPassenger(mc.player)) return;

        var key = ForgeRegistries.ENTITY_TYPES.getKey(e.getType());
        if (key != null && Config.whitelist.contains(key.toString())) return;

        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        if (cam.distanceToSqr(e.getX(), e.getY(), e.getZ()) > Config.entityCullDistSq) {
            event.setCanceled(true);
        }
    }

    // ---------- Nametag culling ----------

    @SubscribeEvent
    public static void onNameTag(RenderNameTagEvent event) {
        if (!Config.nametagCulling) return;
        Entity e = event.getEntity();
        Minecraft mc = Minecraft.getInstance();
        if (e == mc.player) return;

        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        if (cam.distanceToSqr(e.getX(), e.getY(), e.getZ()) > Config.nametagDistSq) {
            event.setResult(Event.Result.DENY);
        }
    }

    // ---------- FPS measuring ----------

    private static long lastFpsNanos = System.nanoTime();
    private static int frames = 0;
    private static int fps = 0;

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        frames++;
        long now = System.nanoTime();
        if (now - lastFpsNanos >= 1_000_000_000L) {
            fps = frames;
            frames = 0;
            lastFpsNanos = now;
        }
    }

    // ---------- Unfocused limiter + dynamic render distance ----------

    private static boolean throttled = false;

    private static int rdTimer = 0;
    private static int ticksSinceLowered = 0;
    private static int lastApplied = -1;   // render distance we last set ourselves
    private static int userRd = -1;        // the player's own chosen maximum

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return;

        handleUnfocused(mc);
        handleDynamicRenderDistance(mc);
    }

    private static void handleUnfocused(Minecraft mc) {
        boolean active = mc.isWindowActive();
        if (Config.unfocusedLimit && !active) {
            if (!throttled) {
                mc.getWindow().setFramerateLimit(Config.unfocusedFps);
                throttled = true;
            }
        } else if (throttled) {
            mc.getWindow().setFramerateLimit(mc.options.framerateLimit().get());
            throttled = false;
        }
    }

    private static void handleDynamicRenderDistance(Minecraft mc) {
        if (!Config.dynamicRd || mc.level == null || !mc.isWindowActive()) {
            rdTimer = 0;
            return;
        }

        int current = mc.options.renderDistance().get();
        if (lastApplied == -1 || current != lastApplied) {
            // first run, or the player changed it manually: treat it as the new maximum
            userRd = current;
            lastApplied = current;
        }

        ticksSinceLowered++;
        if (++rdTimer < 200) return;   // evaluate every ~10 seconds
        rdTimer = 0;

        if (fps <= 0) return;

        if (fps < Config.targetFps && current > Config.minRd) {
            setRd(mc, current - 1);
            ticksSinceLowered = 0;
        } else if (fps >= Config.targetFps + 20 && current < userRd && ticksSinceLowered >= 1200) {
            setRd(mc, current + 1);
        }
    }

    private static void setRd(Minecraft mc, int value) {
        mc.options.renderDistance().set(value);
        lastApplied = value;
    }
}
