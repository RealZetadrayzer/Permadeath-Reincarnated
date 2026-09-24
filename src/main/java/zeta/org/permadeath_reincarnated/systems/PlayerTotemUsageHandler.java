package zeta.org.permadeath_reincarnated.systems;

import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import org.slf4j.Logger;

@EventBusSubscriber
public class PlayerTotemUsageHandler {
   private static final Random RANDOM = new Random();
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<String, String> SOURCE_STRINGS = Map.ofEntries(
      Map.entry("anvil", "yunque"),
      Map.entry("arrow", "flecha"),
      Map.entry("badRespawnPoint", "explosión"),
      Map.entry("cactus", "cactus"),
      Map.entry("cramming", "aplastamiento"),
      Map.entry("dragonBreath", "aliento de dragón"),
      Map.entry("drown", "ahogamiento"),
      Map.entry("dryOut", "deshidratamiento"),
      Map.entry("explosion", "explosión"),
      Map.entry("explosion.player", "explosión"),
      Map.entry("explosion.crystal", "end crystal"),
      Map.entry("fall", "caída"),
      Map.entry("fallingBlock", "bloque cayendo"),
      Map.entry("fallingStalactite", "estalactita cayendo"),
      Map.entry("outOfWorld", "caerse al vacío"),
      Map.entry("fireball", "bola de fuego"),
      Map.entry("fireworks", "fuegos artificiales"),
      Map.entry("flyIntoWall", "chocarse contra una pared"),
      Map.entry("freeze", "congelación"),
      Map.entry("generic", "daño genérico"),
      Map.entry("genericKill", "daño genérico"),
      Map.entry("hotFloor", "suelo caliente"),
      Map.entry("inFire", "quemarse"),
      Map.entry("inWall", "sofocarse en bloques"),
      Map.entry("lava", "quemarse con lava"),
      Map.entry("lightningBolt", "rayo"),
      Map.entry("magic", "daño mágico"),
      Map.entry("mob", "mob"),
      Map.entry("mobProjectile", "proyectil"),
      Map.entry("noAggroMobAttack", "mob"),
      Map.entry("onFire", "quemarse"),
      Map.entry("outsideBorder", "borde del mundo"),
      Map.entry("player", "jugador"),
      Map.entry("sonic_boom", "explosión sónica"),
      Map.entry("stalagmite", "estalagmita"),
      Map.entry("starve", "hambre"),
      Map.entry("sting", "picadura de abeja"),
      Map.entry("sweetBerryBush", "arbusto de bayas"),
      Map.entry("thorns", "espinas"),
      Map.entry("trident", "tridente"),
      Map.entry("wither", "efecto wither"),
      Map.entry("witherSkull", "calavera de Wither")
   );

   @SubscribeEvent
   private static void onRespawn(PlayerRespawnEvent event) {
      if (!event.getEntity().level().isClientSide) {
         event.getEntity().removeTag("totem_failed");
      }
   }

   @SubscribeEvent
   private static void onTotemUse(LivingUseTotemEvent event) {
      if (event.getEntity() instanceof Player player) {
         if (!player.level().isClientSide) {
            ItemStack totem = event.getTotem();
            boolean isMedalla = totem.getComponents().toString().contains("medalla");
            int day = DayGlobalCount.CURRENT_DAY;
            int roll = RANDOM.nextInt(100);
            String cause = resolveSource(event.getSource());
            if (player.getTags().contains("totem_failed")) {
               event.setCanceled(true);
            } else {
               int requiredTotems = 1;
               int consumedTotems = 1;
               int failThreshold = -1;
               if (day >= 60) {
                  requiredTotems = 3;
                  consumedTotems = isMedalla ? 1 : 3;
                  failThreshold = 93;
               } else if (day >= 50) {
                  requiredTotems = 2;
                  consumedTotems = isMedalla ? 1 : 2;
                  failThreshold = 95;
               } else if (day >= 40) {
                  requiredTotems = 2;
                  consumedTotems = isMedalla ? 1 : 2;
                  failThreshold = 97;
               } else if (day >= 30) {
                  failThreshold = 99;
               }

               int totalTotems = countTotems(player);
               if (totalTotems < requiredTotems) {
                  event.setCanceled(true);
                  player.addTag("totem_failed");
                  String msg = isMedalla
                     ? String.format(
                        "§7%s no pudo activar su §4§l[§r§c☠§4§l] §f§kℑ§r §6§lMedalla de Superviviente §r§f§kℑ §4§l[§r§c☠§4§l]§r §7por no tener suficientes tótem en su inventario.§r",
                        player.getName().getString()
                     )
                     : String.format(
                        "§7El tótem de %s falló por no tener suficientes tótem en su inventario. (Probabilidad: §c%d§7 >= %d)§r",
                        player.getName().getString(),
                        failThreshold,
                        failThreshold
                     );
                  broadcast(player, msg);
               } else if (!isMedalla && failThreshold != -1 && roll >= failThreshold) {
                  event.setCanceled(true);
                  player.addTag("totem_failed");
                  String msg = String.format(
                     "§7%s ha consumido %s por %s. (Probabilidad: §c%d§7 >= %d)§r",
                     player.getName().getString(),
                     formatTotemAmount(consumedTotems, false),
                     cause,
                     roll,
                     failThreshold
                  );
                  broadcast(player, msg);
               } else {
                  consumeExtraTotems(player, consumedTotems - 1, totem);
                  String successMsg;
                  if (isMedalla) {
                     successMsg = String.format("§7%s ha consumido %s por %s.§r", player.getName().getString(), formatTotemAmount(consumedTotems, true), cause);
                  } else if (failThreshold != -1) {
                     successMsg = String.format(
                        "§7%s ha consumido %s por %s. (Probabilidad: §a%d§7 < %d)§r",
                        player.getName().getString(),
                        formatTotemAmount(consumedTotems, false),
                        cause,
                        roll,
                        failThreshold
                     );
                  } else {
                     successMsg = String.format("§7%s ha consumido %s por %s.§r", player.getName().getString(), formatTotemAmount(consumedTotems, false), cause);
                  }

                  broadcast(player, successMsg);
               }
            }
         }
      }
   }

   private static String formatTotemAmount(int amount, boolean isMedalla) {
      if (isMedalla) {
         return "su §4§l[§r§c☠§4§l] §f§kℑ§r §6§lMedalla de Superviviente §r§f§kℑ §4§l[§r§c☠§4§l]§r§7";
      }

      return switch (amount) {
         case 1 -> "un tótem";
         case 2 -> "dos tótems";
         case 3 -> "tres tótems";
         default -> amount + " tótems";
      };
   }

   private static void broadcast(Player player, String message) {
      Objects.requireNonNull(player.getServer()).getPlayerList().getPlayers().forEach(p -> p.sendSystemMessage(Component.literal(message), false));
      LOGGER.info("[Permadeath Monitor] {}", sanitize(message));
   }

   private static int countTotems(Player player) {
      int count = 0;

      for (ItemStack stack : player.getInventory().items) {
         if (stack.is(Items.TOTEM_OF_UNDYING)) {
            count += stack.getCount();
         }
      }

      for (ItemStack stack : player.getInventory().offhand) {
         if (stack.is(Items.TOTEM_OF_UNDYING)) {
            count += stack.getCount();
         }
      }

      return count;
   }

   private static void consumeExtraTotems(Player player, int amount, ItemStack usedTotem) {
      if (amount > 0) {
         for (ItemStack stack : player.getInventory().items) {
            if (stack != usedTotem && stack.is(Items.TOTEM_OF_UNDYING)) {
               int remove = Math.min(stack.getCount(), amount);
               stack.shrink(remove);
               amount -= remove;
               if (amount <= 0) {
                  return;
               }
            }
         }

         for (ItemStack stack : player.getInventory().offhand) {
            if (stack != usedTotem && stack.is(Items.TOTEM_OF_UNDYING)) {
               int remove = Math.min(stack.getCount(), amount);
               stack.shrink(remove);
               amount -= remove;
               if (amount <= 0) {
                  return;
               }
            }
         }
      }
   }

   private static String resolveSource(DamageSource source) {
      Entity attacker = source.getEntity();
      return attacker != null ? sanitize(attacker.getDisplayName().getString()) : SOURCE_STRINGS.getOrDefault(source.getMsgId(), "daño desconocido");
   }

   private static String sanitize(String text) {
      return text.replaceAll("§.", "");
   }
}
