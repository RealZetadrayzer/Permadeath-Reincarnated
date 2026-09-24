package zeta.org.permadeath_reincarnated.systems;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber
public class Startup {
   @SubscribeEvent
   public static void onServerStarted(ServerStartedEvent event) {
      MinecraftServer server = event.getServer();
      ServerLevel level = server.overworld();
      StartupData startupData = StartupData.get(level);
      if (!startupData.isStartupRunned()) {
         server.getAllLevels().forEach(levels -> StartupData.get(levels).setStartupRunned(true));
         Scoreboard scoreboard = level.getScoreboard();
         ((BooleanValue)server.getGameRules().getRule(GameRules.RULE_SENDCOMMANDFEEDBACK)).set(false, server);
         server.setDifficulty(Difficulty.HARD, false);
         Objective totem = getOrCreateStatObjective(scoreboard, "totem", "minecraft.used:minecraft.totem_of_undying", "Totems");
         Objective health = getOrCreateObjective(scoreboard, "health", ObjectiveCriteria.HEALTH, "❤");
         scoreboard.setDisplayObjective(DisplaySlot.LIST, health);
         scoreboard.setDisplayObjective(DisplaySlot.BELOW_NAME, health);
         createTeam(scoreboard, "admin", "[ADMIN] ", ChatFormatting.RED, true);
         createTeam(scoreboard, "miembro", "[MIEMBRO] ", ChatFormatting.GREEN, true);
         createTeam(scoreboard, "invitado", "[INVITADO] ", ChatFormatting.YELLOW, true);
         createBossBar(server, "life_orb_timer");
         createBossBar(server, "double_shulker_shells_timer");
      }
   }

   @SubscribeEvent
   public static void onPlayerChat(ServerChatEvent event) {
      ServerPlayer player = event.getPlayer();
      if (!player.level().isClientSide) {
         String playerName = player.getName().getString();
         Scoreboard scoreboard = player.level().getScoreboard();
         int totemScore = 0;
         Objective totemObjective = scoreboard.getObjective("totem");
         if (totemObjective != null) {
            totemScore = scoreboard.getOrCreatePlayerScore(player, totemObjective).get();
         }

         PlayerTeam team = scoreboard.getPlayersTeam(playerName);
         Component prefix = (Component)(team != null ? team.getPlayerPrefix() : Component.empty());
         Component message = Component.literal("")
            .append(Component.literal("§7<§e" + totemScore + " "))
            .append(Component.literal("§e\ue000"))
            .append(Component.literal("§r §8|§r "))
            .append(prefix)
            .append(Component.literal(playerName))
            .append(Component.literal("§7>§r "))
            .append(event.getMessage());
         Objects.requireNonNull(player.getServer()).getPlayerList().broadcastSystemMessage(message, false);
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onPlayerJoin(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      if (!player.level().isClientSide) {
         if (player.level() instanceof ServerLevel level) {
            Scoreboard scoreboard = level.getScoreboard();
            PlayerTeam team = scoreboard.getPlayerTeam("miembro");
            if (team != null) {
               if (!team.getPlayers().contains(player.getScoreboardName()) && !player.getTags().contains("PlayerLoggedInFirstTime")) {
                  player.addTag("PlayerLoggedInFirstTime");
                  scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
               }
            }
         }
      }
   }

   private static Objective getOrCreateObjective(Scoreboard scoreboard, String name, ObjectiveCriteria criteria, String display) {
      Objective obj = scoreboard.getObjective(name);
      if (obj == null) {
         obj = scoreboard.addObjective(name, criteria, Component.literal(display), RenderType.HEARTS, true, null);
      }

      return obj;
   }

   private static Objective getOrCreateStatObjective(Scoreboard scoreboard, String name, String statName, String display) {
      Objective obj = scoreboard.getObjective(name);
      if (obj == null) {
         ObjectiveCriteria criteria = ObjectiveCriteria.byName(statName).orElse(ObjectiveCriteria.DUMMY);
         obj = scoreboard.addObjective(name, criteria, Component.literal(display), RenderType.INTEGER, true, null);
      }

      return obj;
   }

   public static void createTeam(Scoreboard scoreboard, String name, String prefix, ChatFormatting color, boolean bold) {
      PlayerTeam team = scoreboard.getPlayerTeam(name);
      if (team == null) {
         team = scoreboard.addPlayerTeam(name);
      }

      team.setPlayerPrefix(Component.literal(prefix).withStyle(style -> style.withColor(color).withBold(bold)));
   }

   private static void createBossBar(MinecraftServer server, String id) {
      server.getCustomBossEvents().create(rl(id), Component.literal(" ")).setColor(BossBarColor.PURPLE);
   }

   private static ResourceLocation rl(String path) {
      return ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", path);
   }
}
