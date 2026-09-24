package zeta.org.permadeath_reincarnated.systems;

import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.timers.DeathTrainTimer;

public class PlayerDeathHandler {
   private static final Map<UUID, Integer> DEATH_TIMER = new HashMap<>();
   private static final Map<UUID, Integer> SOUND_DELAY = new HashMap<>();
   private static final Map<UUID, Integer> SOUND_DELAY_2 = new HashMap<>();
   private static final Map<UUID, GameProfile> PENDING_BAN_PROFILE = new HashMap<>();
   private static final Map<String, String> DIMENSION_NAMES = new HashMap<>();
   private static final Map<String, String> PLAYER_DEATH_MESSAGES = new HashMap<>();
   private static final String DEFAULT_DEATH_MESSAGE = "Ahora por fin descansa en paz...";

   private static String getDeathMessage(String playerName) {
      return PLAYER_DEATH_MESSAGES.getOrDefault(playerName, "Ahora por fin descansa en paz...");
   }

   @SubscribeEvent
   public void onPlayerDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (player.level() instanceof ServerLevel level) {
            MinecraftServer var25 = player.server;
            GameProfile profile = player.getGameProfile();
            LightningBolt lightningBolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(level);
            String[] deathWords = new String[]{"infinito", "eterno", "interminable", "perpetuo"};
            String randomWord = deathWords[ThreadLocalRandom.current().nextInt(deathWords.length)];
            String playerName = player.getName().getString();
            float yaw = player.getYHeadRot();
            double correctedYaw = ((yaw + 180.0F) % 360.0F + 360.0F) % 360.0F;
            int rotationIndex = (int)Math.floor(correctedYaw / 360.0 * 16.0 % 16.0);
            int x = (int)Math.floor(player.getX());
            int y = (int)Math.floor(player.getY());
            int z = (int)Math.floor(player.getZ());
            String dimId = level.dimension().location().toString();
            String dimensionName = DIMENSION_NAMES.getOrDefault(dimId, dimId);
            ResolvableProfile resolvable = new ResolvableProfile(profile);
            BlockPos bedrockPos;
            BlockPos fencePos;
            BlockPos headPos;
            if (y >= 0 || !dimId.equals("minecraft:the_end") && !dimId.equals("permadeath_reincarnated:the_beginning")) {
               bedrockPos = new BlockPos(x, y - 1, z);
               fencePos = new BlockPos(x, y, z);
               headPos = new BlockPos(x, y + 1, z);
            } else {
               bedrockPos = new BlockPos(x, 0, z);
               fencePos = new BlockPos(x, 1, z);
               headPos = new BlockPos(x, 2, z);
               y = 0;
            }

            ScheduleInTicks.schedule(() -> {
               level.destroyBlock(bedrockPos, true);
               level.setBlock(bedrockPos, Blocks.BEDROCK.defaultBlockState(), 3);
               level.destroyBlock(fencePos, true);
               level.setBlock(fencePos, Blocks.NETHER_BRICK_FENCE.defaultBlockState(), 3);
               level.destroyBlock(headPos, true);
               level.setBlock(headPos, (BlockState)Blocks.PLAYER_HEAD.defaultBlockState().setValue(SkullBlock.ROTATION, rotationIndex), 3);
               SkullBlockEntity skull = (SkullBlockEntity)level.getBlockEntity(headPos);
               if (skull != null) {
                  skull.setOwner(resolvable);
               }
            }, 2);
            level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 0.5, player.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            level.sendParticles(ParticleTypes.SMALL_FLAME, player.getX(), player.getY() + 0.5, player.getZ(), 40, 0.15, 0.15, 0.15, 0.1);
            if (lightningBolt != null) {
               lightningBolt.setPos(player.getX(), player.getY(), player.getZ());
               lightningBolt.setVisualOnly(true);
               level.addFreshEntity(lightningBolt);
            }

            String deathMessage = getDeathMessage(playerName);
            var25.getPlayerList()
               .broadcastSystemMessage(
                  Component.literal("§c§lEste es el comienzo del sufrimiento " + randomWord + " de §4§l" + playerName + "§c§l. §c§l¡HA SIDO PERMABANEADO!§r"),
                  false
               );
            var25.getPlayerList().broadcastSystemMessage(Component.literal(deathMessage).withStyle(ChatFormatting.GRAY), false);
            var25.getPlayerList()
               .broadcastSystemMessage(Component.literal("§8" + playerName + " ha muerto en " + dimensionName + " §8(" + x + ", " + y + ", " + z + ")"), false);
            var25.getPlayerList().getPlayers().forEach(p -> {
               p.connection.send(new ClientboundSetTitleTextPacket(Component.literal("§c¡Permadeath!")));
               p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(playerName + " ha muerto")));
               p.connection.send(new ClientboundSetTitlesAnimationPacket(10, 60, 10));
            });
            ((BooleanValue)var25.getGameRules().getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN)).set(true, var25);
            SOUND_DELAY.put(player.getUUID(), 2);
            SOUND_DELAY_2.put(player.getUUID(), 98);
            if (!(Boolean)PermadeathConfig.DISABLE_KICK.get()) {
               DEATH_TIMER.put(player.getUUID(), 100);
               PENDING_BAN_PROFILE.put(player.getUUID(), profile);
            }
         }
      }
   }

   @SubscribeEvent
   public void onServerTick(Post event) {
      MinecraftServer server = event.getServer();
      Iterator<Entry<UUID, Integer>> soundIt = SOUND_DELAY.entrySet().iterator();

      while (soundIt.hasNext()) {
         Entry<UUID, Integer> entry = soundIt.next();
         int ticksLeft = entry.getValue() - 1;
         if (ticksLeft <= 0) {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player != null && !(Boolean)PermadeathConfig.DISABLE_SPECTATOR.get()) {
               player.setGameMode(GameType.SPECTATOR);
            }

            server.getPlayerList().getPlayers().forEach(p -> playGlobalSound((ServerLevel)p.level(), SoundEvents.BLAZE_DEATH, SoundSource.MASTER, 4.0F, 0.5F));
            soundIt.remove();
         } else {
            entry.setValue(ticksLeft);
         }
      }

      Iterator<Entry<UUID, Integer>> soundItTwo = SOUND_DELAY_2.entrySet().iterator();

      while (soundItTwo.hasNext()) {
         Entry<UUID, Integer> entry = soundItTwo.next();
         int ticksLeft = entry.getValue() - 1;
         if (ticksLeft <= 0) {
            ServerLevel world = server.overworld();
            DeathTrainTimer data = DeathTrainTimer.get(world);
            int addedSeconds = DeathTrainTimer.addTime(world, data.day);
            String addedTime = DeathTrainTimer.formatAddedTime(addedSeconds);
            server.getPlayerList().getPlayers().forEach(p -> {
               p.sendSystemMessage(Component.literal("§c¡Comienza el Death Train con duración de " + addedTime + "!"));
               playGlobalSound((ServerLevel)p.level(), SoundEvents.SKELETON_HORSE_DEATH, SoundSource.MASTER, 4.0F, 1.0F);
            });
            soundItTwo.remove();
         } else {
            entry.setValue(ticksLeft);
         }
      }

      Iterator<Entry<UUID, Integer>> it = DEATH_TIMER.entrySet().iterator();

      while (it.hasNext()) {
         Entry<UUID, Integer> entry = it.next();
         int ticksLeft = entry.getValue() - 1;
         if (ticksLeft <= 0) {
            UUID id = entry.getKey();
            String reason = "§cHas sido PERMABANEADO";
            ServerPlayer online = server.getPlayerList().getPlayer(id);
            if (online != null) {
               online.connection.disconnect(Component.literal(reason));
            }

            GameProfile prof = PENDING_BAN_PROFILE.remove(id);
            if (prof != null) {
               server.getPlayerList().getBans().add(new UserBanListEntry(prof, new Date(), "Permadeath Reincarnated", null, reason));
            }

            it.remove();
         } else {
            entry.setValue(ticksLeft);
         }
      }
   }

   public static void playGlobalSound(ServerLevel level, SoundEvent sound, SoundSource source, float volume, float pitch) {
      long seed = level.getRandom().nextLong();
      level.getServer().getPlayerList().getPlayers().forEach(player -> {
         if (player.level() == level) {
            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();
            player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source, px, py, pz, volume, pitch, seed));
         }
      });
   }

   static {
      DIMENSION_NAMES.put("minecraft:overworld", "§aEl Overworld§r");
      DIMENSION_NAMES.put("minecraft:the_nether", "§cEl Nether§r");
      DIMENSION_NAMES.put("minecraft:the_end", "§dEl End§r");
      DIMENSION_NAMES.put("permadeath_reincarnated:the_beginning", "§6The Beginning§r");
      PLAYER_DEATH_MESSAGES.put("Lonchope666", "Lonchope666, Le faltó creer que podía creer en él mismo.");
      PLAYER_DEATH_MESSAGES.put("YaYant", "YaYant, Dead people don't talk.");
      PLAYER_DEATH_MESSAGES.put("Genericont", "Genericont, Es tu salida, saliste del videojuego.");
      PLAYER_DEATH_MESSAGES.put("NormanliumZz", "NormanliumZz, Asi nomás quedó.");
      PLAYER_DEATH_MESSAGES.put("iSykeda", "iSykeda, EzKeda no keda con vida.");
      PLAYER_DEATH_MESSAGES.put("MauLovesDaiqui", "MauLovesDaiqui, La muerte del papu.");
      PLAYER_DEATH_MESSAGES.put("Zer0_743", "Zer0_437, Ahora es un 0 a la izquierda.");
      PLAYER_DEATH_MESSAGES.put("ScottGmr13", "ScottGmr13, Ojalá fueras como antes.");
      PLAYER_DEATH_MESSAGES.put("Artemizg", "Artemizg, No pudo tamear la muerte.");
      PLAYER_DEATH_MESSAGES.put("_NeaK_", "_Neak_, Ahora si nos podemos olvidar de ti.");
      PLAYER_DEATH_MESSAGES.put("Astral302", "Astral302, Se le da mejor el BlockBench.");
      PLAYER_DEATH_MESSAGES.put("convecs_", "convecs_, Giga trolleada de manual.");
      PLAYER_DEATH_MESSAGES.put("ItsAncientMC", "ItsAncientMC, Lo siento mucho pequeño.");
      PLAYER_DEATH_MESSAGES.put("GGeoKiller", "GGeoKiller, Se acabó para ti, chihuahua.");
      PLAYER_DEATH_MESSAGES.put("ApexPrdtMx", "ApexPrdtMx, Sometimes you gotta close the door and open a window.");
      PLAYER_DEATH_MESSAGES.put("Dionix360", "Dionix360, El touch de oro ha perdido el toque.");
      PLAYER_DEATH_MESSAGES.put("Aiksgo", "Aiksgo, She didn't put enough effort in it.");
      PLAYER_DEATH_MESSAGES.put("knowingema", "knowingema, Fuiste demoteado a permabaneado.");
      PLAYER_DEATH_MESSAGES.put("iAests", "iAests, Ser staff lo ha hecho menos hábil.");
      PLAYER_DEATH_MESSAGES.put("im_grillo", "im_grillo, When mueres but no revives #5.");
      PLAYER_DEATH_MESSAGES.put("KreKoh", "KreKoh, Tan under que terminó enterrado.");
      PLAYER_DEATH_MESSAGES.put("zetadrayzer", "zetadrayzer, Ya estaba muy senil para seguir.");
      PLAYER_DEATH_MESSAGES.put("imDaGerz", "imDaGerz, No fue tan ágil como los linces.");
      PLAYER_DEATH_MESSAGES.put("Lapony3331x", "Lapony3331x, No estaba tan Locked In como decía.");
      PLAYER_DEATH_MESSAGES.put("Minerexking", "Minerexking, El dueño de HARDlands no sobrevive al HARDcore.");
      PLAYER_DEATH_MESSAGES.put("MisterYo484", "MisterYo484, El rey paloma asciende.");
      PLAYER_DEATH_MESSAGES.put("Pastelawas", "Pastelawas, Le dio miedo vivir.");
      PLAYER_DEATH_MESSAGES.put("Perrochongo_", "Perrochongo_, Por un pelito casi se salva.");
      PLAYER_DEATH_MESSAGES.put("Simon3OOO", "Simon3OOO, Una estampada más para la lista.");
      PLAYER_DEATH_MESSAGES.put("TarmacGraph", "TarmacGraph, Lo mandaron de vuelta a MCC Island.");
      PLAYER_DEATH_MESSAGES.put("zulvegaa", "zulvegaa, ¿Alguien más tiene un deja vú?");
      PLAYER_DEATH_MESSAGES.put("Gabgo09", "Gabgo09, Los patos no le salvaron de la muerte.");
      PLAYER_DEATH_MESSAGES.put("Gaol22", "Gaol22, Años después sigue estando chill.");
      PLAYER_DEATH_MESSAGES.put("Gyummiel", "Gyummiel, La ludopatía la terminó matando.");
      PLAYER_DEATH_MESSAGES.put("Dendo__", "Dendo__, No logró llegar a las farlands esta vez.");
      PLAYER_DEATH_MESSAGES.put("Prafa1111", "Prafa1111, Ya no podrás escalar más cerros.");
      PLAYER_DEATH_MESSAGES.put("blancolaseta", "blancolaseta, Estás muy lejos de bedrock.");
      PLAYER_DEATH_MESSAGES.put("DarkyCraft", "DarkyCraft, Imagina tener que hacer tu imagen de muerte.");
      PLAYER_DEATH_MESSAGES.put("MazkyBellako29", "MazkyBellako29, Ahora podras montar a VuelveCandyBPeroSinJinete una vez más.");
      PLAYER_DEATH_MESSAGES.put("DaxzFTW", "DaxzFTW, El Create no le ayudo contra la muerte.");
   }
}
