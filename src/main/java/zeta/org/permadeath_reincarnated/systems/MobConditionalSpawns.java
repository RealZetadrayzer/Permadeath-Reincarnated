package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.demonFight.PermadeathDemonFightHandler;
import zeta.org.permadeath_reincarnated.entities.CustomCreeper;
import zeta.org.permadeath_reincarnated.entities.CustomGiant;
import zeta.org.permadeath_reincarnated.entities.CustomRavager;
import zeta.org.permadeath_reincarnated.entities.CustomSilverfish;
import zeta.org.permadeath_reincarnated.entities.CustomSnowGolem;
import zeta.org.permadeath_reincarnated.entities.CustomZombieHorse;

@EventBusSubscriber
public class MobConditionalSpawns {
   @SubscribeEvent
   public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
      Mob entity = event.getEntity();
      if (event.getSpawnType() == MobSpawnType.NATURAL) {
         int day = DayGlobalCount.CURRENT_DAY;
         PermadeathDemonFightHandler handler = PermadeathDemonFightHandler.get((ServerLevel)entity.level());
         if (entity.level().dimension() == Level.END) {
            if (day < 30) {
               if (entity instanceof Creeper) {
                  event.setSpawnCancelled(true);
               } else if (entity instanceof Ghast) {
                  event.setSpawnCancelled(true);
               }
            }

            if (day >= 30 && entity instanceof Ghast && handler.isFightOn() && !entity.getTags().contains("demonFight")) {
               event.setSpawnCancelled(true);
            }
         }

         if (entity.level().dimension() == Level.NETHER && day < 60 && entity instanceof Shulker) {
            event.setSpawnCancelled(true);
         }

         if (entity.level().dimension() == Level.OVERWORLD) {
            if (day < 40
               && entity.level().getBiome(entity.blockPosition()).is(Biomes.MUSHROOM_FIELDS)
               && (
                  entity instanceof Zombie
                     || entity instanceof Creeper
                     || entity instanceof Witch
                     || entity instanceof Skeleton
                     || entity instanceof Spider
                     || entity instanceof EnderMan
                     || entity instanceof ZombieVillager
                     || entity instanceof WitherSkeleton
               )) {
               event.setSpawnCancelled(true);
            }

            if (day < 50 && entity.level().getBiome(entity.blockPosition()).is(Biomes.PLAINS) && entity instanceof CustomGiant) {
               event.setSpawnCancelled(true);
            }
         }

         if (day < 40 && entity instanceof CustomRavager) {
            event.setSpawnCancelled(true);
         }

         if (day < 50 && entity instanceof CustomSilverfish) {
            event.setSpawnCancelled(true);
         }

         if (day < 60 && entity instanceof CustomSnowGolem) {
            event.setSpawnCancelled(true);
         }

         if (entity instanceof ZombifiedPiglin && day >= 60) {
            event.setSpawnCancelled(true);
         }

         if (entity instanceof CustomCreeper) {
            if (day < 60) {
               event.setSpawnCancelled(true);
            }

            if (!(Boolean)PermadeathConfig.MIKECRACK.get()) {
               event.setSpawnCancelled(true);
            }
         }

         if (entity instanceof CustomZombieHorse) {
            if (day < 10) {
               event.setSpawnCancelled(true);
            }

            if (!(Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
               event.setSpawnCancelled(true);
            }
         }
      }
   }
}
