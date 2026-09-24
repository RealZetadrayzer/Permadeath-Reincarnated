package zeta.org.permadeath_reincarnated.entities;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(bus = Bus.MOD)
public class PermadeathEntityRegistry {
   public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "permadeath_reincarnated");
   public static final DeferredHolder<EntityType<?>, EntityType<CustomSnowGolem>> CUSTOM_SNOWGOLEM = ENTITY_TYPE.register(
      "wild_snow_golem", () -> Builder.of(CustomSnowGolem::new, MobCategory.MONSTER).sized(0.7F, 1.9F).build("permadeath_reincarnatedwild_snow_golem")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CustomCreeper>> CUSTOM_CREEPER = ENTITY_TYPE.register(
      "creeper", () -> Builder.of(CustomCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).build("permadeath_reincarnatedcreeper")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CustomRavager>> CUSTOM_RAVAGER = ENTITY_TYPE.register(
      "wild_ravager", () -> Builder.of(CustomRavager::new, MobCategory.MONSTER).sized(1.95F, 2.2F).build("permadeath_reincarnatedwild_ravager")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CustomSilverfish>> CUSTOM_SILVERFISH = ENTITY_TYPE.register(
      "wild_silverfish", () -> Builder.of(CustomSilverfish::new, MobCategory.MONSTER).sized(0.4F, 0.3F).build("permadeath_reincarnatedwild_silverfish")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CustomGiant>> CUSTOM_GIANT = ENTITY_TYPE.register(
      "wild_giant", () -> Builder.of(CustomGiant::new, MobCategory.MONSTER).sized(3.6F, 12.0F).build("permadeath_reincarnatedwild_giant")
   );
   public static final DeferredHolder<EntityType<?>, EntityType<CustomZombieHorse>> CUSTOM_ZOMBIE_HORSE = ENTITY_TYPE.register(
      "wild_zombie_horse", () -> Builder.of(CustomZombieHorse::new, MobCategory.MONSTER).sized(1.3965F, 1.6F).build("permadeath_reincarnatedwild_zombie_horse")
   );

   @SubscribeEvent
   public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
      event.register(
         (EntityType)CUSTOM_SNOWGOLEM.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, CustomSnowGolem::canDaySpawn, Operation.REPLACE
      );
      event.register(
         (EntityType)CUSTOM_CREEPER.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Types.MOTION_BLOCKING, CustomCreeper::canSpawnAnywhere, Operation.REPLACE
      );
      event.register(
         (EntityType)CUSTOM_SILVERFISH.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, CustomSilverfish::canDaySpawn, Operation.REPLACE
      );
      event.register(
         (EntityType)CUSTOM_RAVAGER.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, CustomRavager::canDaySpawn, Operation.REPLACE
      );
      event.register(
         (EntityType)CUSTOM_GIANT.get(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, Operation.REPLACE
      );
      event.register(
         (EntityType)CUSTOM_ZOMBIE_HORSE.get(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         CustomZombieHorse::checkCustomZombieHorseSpawnRules,
         Operation.REPLACE
      );
   }

   @SubscribeEvent
   public static void initializeAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)CUSTOM_SNOWGOLEM.get(), CustomSnowGolem.createAttributes().build());
      event.put((EntityType)CUSTOM_CREEPER.get(), CustomCreeper.createAttributes().build());
      event.put((EntityType)CUSTOM_RAVAGER.get(), CustomRavager.createAttributes().build());
      event.put((EntityType)CUSTOM_SILVERFISH.get(), CustomSilverfish.createAttributes().build());
      event.put((EntityType)CUSTOM_GIANT.get(), CustomGiant.createAttributes().build());
      event.put((EntityType)CUSTOM_ZOMBIE_HORSE.get(), CustomZombieHorse.createAttributes().build());
   }
}
