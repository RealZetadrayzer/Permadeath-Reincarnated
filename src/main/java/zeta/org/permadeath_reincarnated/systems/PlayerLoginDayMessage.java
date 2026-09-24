package zeta.org.permadeath_reincarnated.systems;

import java.util.Map;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import zeta.org.permadeath_reincarnated.demonFight.PermadeathDemonFightHandler;

@EventBusSubscriber
public class PlayerLoginDayMessage {
   private static final Map<Integer, String> SPECIAL_EVENT_LIST = Map.of(
      39,
      "[X2 de drop en Shulker Shells]",
      50,
      "[Apertura de The Beginning]",
      55,
      "[Entrega de Medallas del Superviviente]",
      59,
      "[Loot libre en The Beginning]"
   );
   private static final Set<Integer> DIFFICULTY_DAYS = Set.of(10, 20, 40, 60, 70, 80, 90, 100, 110, 120);
   private static final int SURPRISE_DAY = 25;
   private static final int DEMON_FIGHT_DAY = 30;

   @SubscribeEvent
   public static void onLogin(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            ScheduleInTicks.schedule(
               () -> {
                  int dia = DayGlobalCount.CURRENT_DAY;
                  if (dia == 30) {
                     String specialMessage = getDay30Message(player);
                     player.sendSystemMessage(
                        Component.literal("Estamos en el ")
                           .withStyle(ChatFormatting.LIGHT_PURPLE)
                           .append(Component.literal("Día " + dia).withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD}))
                           .append(Component.literal(".\nEvento especial: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                           .append(Component.literal(specialMessage).withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD}))
                           .append(Component.literal(".").withStyle(ChatFormatting.LIGHT_PURPLE))
                     );
                     playLoginSound(player, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.MASTER, 1.0F, 2.0F);
                  } else if (SPECIAL_EVENT_LIST.containsKey(dia)) {
                     String specialMessage = SPECIAL_EVENT_LIST.get(dia);
                     player.sendSystemMessage(
                        Component.literal("Estamos en el ")
                           .withStyle(ChatFormatting.LIGHT_PURPLE)
                           .append(Component.literal("Día " + dia).withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD}))
                           .append(Component.literal(".\nEvento especial: ").withStyle(ChatFormatting.LIGHT_PURPLE))
                           .append(Component.literal(specialMessage).withStyle(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD}))
                           .append(Component.literal(".").withStyle(ChatFormatting.LIGHT_PURPLE))
                     );
                     playLoginSound(player, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.MASTER, 1.0F, 2.0F);
                  } else if (dia == 25) {
                     player.sendSystemMessage(
                        Component.literal("Estamos en el ")
                           .withStyle(ChatFormatting.YELLOW)
                           .append(Component.literal("Día " + dia).withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}))
                           .append(Component.literal(".\nRecuerda revisar el ").withStyle(ChatFormatting.YELLOW))
                           .append(Component.literal("Discord").withStyle(new ChatFormatting[]{ChatFormatting.BLUE, ChatFormatting.BOLD}))
                           .append(Component.literal(" para estar al tanto de los cambios de dificultad sorpresa.").withStyle(ChatFormatting.YELLOW))
                     );
                     playLoginSound(player, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.MASTER, 0.6F, 1.0F);
                  } else if (DIFFICULTY_DAYS.contains(dia)) {
                     player.sendSystemMessage(
                        Component.literal("Estamos en el ")
                           .withStyle(ChatFormatting.RED)
                           .append(Component.literal("Día " + dia).withStyle(new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD}))
                           .append(Component.literal(".\nRecuerda revisar el ").withStyle(ChatFormatting.RED))
                           .append(Component.literal("Discord").withStyle(new ChatFormatting[]{ChatFormatting.BLUE, ChatFormatting.BOLD}))
                           .append(Component.literal(" para estar al tanto de los cambios de dificultad.").withStyle(ChatFormatting.RED))
                     );
                     playLoginSound(player, SoundEvents.BELL_RESONATE, SoundSource.MASTER, 1.0F, 1.5F);
                  } else {
                     player.sendSystemMessage(
                        Component.literal("Estamos en el ")
                           .withStyle(ChatFormatting.GREEN)
                           .append(Component.literal("Día " + dia).withStyle(new ChatFormatting[]{ChatFormatting.DARK_GREEN, ChatFormatting.BOLD}))
                           .append(Component.literal(".").withStyle(ChatFormatting.GREEN))
                     );
                     playLoginSound(player, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.MASTER, 0.6F, 2.0F);
                  }
               },
               60
            );
         }
      }
   }

   private static String getDay30Message(ServerPlayer player) {
      ServerLevel endLevel = player.server.getLevel(Level.END);
      if (endLevel == null) {
         return "[Batalla contra el Permadeath Demon]";
      }

      PermadeathDemonFightHandler fightData = PermadeathDemonFightHandler.get(endLevel);
      return fightData.wasPreviouslyKilled() ? "[Apertura del End]" : "[Batalla contra el Permadeath Demon]";
   }

   private static void playLoginSound(ServerPlayer player, SoundEvent sound, SoundSource source, float volume, float pitch) {
      long seed = player.getRandom().nextLong();
      player.connection
         .send(
            new ClientboundSoundPacket(
               BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source, player.getX(), player.getY(), player.getZ(), volume, pitch, seed
            )
         );
   }
}
