package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class ItemDamageImmunities {
   @SubscribeEvent
   public static void onHeadItemSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (entity.level() instanceof ServerLevel level) {
         if (!entity.level().isClientSide) {
            if (entity instanceof ItemEntity itemEntity) {
               ItemStack stack = itemEntity.getItem();
               if (stack.is(Items.PLAYER_HEAD)) {
                  Scoreboard scoreboard = level.getScoreboard();
                  PlayerTeam team = scoreboard.getPlayerTeam("playerHeadGlowingColor");
                  if (team == null) {
                     team = scoreboard.addPlayerTeam("playerHeadGlowingColor");
                     team.setColor(ChatFormatting.AQUA);
                  }

                  scoreboard.addPlayerToTeam(itemEntity.getStringUUID(), team);
                  itemEntity.setInvulnerable(true);
                  itemEntity.setGlowingTag(true);
                  itemEntity.setUnlimitedLifetime();
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onSurvivorMedalItemSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      if (entity.level() instanceof ServerLevel level) {
         if (!entity.level().isClientSide) {
            if (entity instanceof ItemEntity itemEntity) {
               ItemStack stack = itemEntity.getItem();
               if (stack.is(Items.TOTEM_OF_UNDYING)) {
                  if (stack.getComponents().toString().contains("medalla")) {
                     Scoreboard scoreboard = level.getScoreboard();
                     PlayerTeam team = scoreboard.getPlayerTeam("survivorMedalGlowingColor");
                     if (team == null) {
                        team = scoreboard.addPlayerTeam("survivorMedalGlowingColor");
                        team.setColor(ChatFormatting.GOLD);
                     }

                     scoreboard.addPlayerToTeam(itemEntity.getStringUUID(), team);
                     itemEntity.setInvulnerable(true);
                     itemEntity.setGlowingTag(true);
                     itemEntity.setUnlimitedLifetime();
                  }
               }
            }
         }
      }
   }
}
