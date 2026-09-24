package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(TrialSpawner.class)
public class TrialSpawnerConfigSwapMixin {
   @Unique
   private static final ResourceKey<LootTable> PERMADEATH_OMINOUS_CONSUMABLES = ResourceKey.create(
      Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "spawners/trial_chamber/permadeath_ominous_consumables")
   );
   @Shadow
   @Final
   @Mutable
   private TrialSpawnerConfig ominousConfig;
   @Unique
   private boolean permadeath$swapped;

   @Inject(method = "tickServer", at = @At("HEAD"))
   private void permadeath$swapOminousLootConfig(ServerLevel level, BlockPos pos, boolean isOminous, CallbackInfo ci) {
      if (isOminous) {
         if ((Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
            if (DayGlobalCount.CURRENT_DAY >= 20) {
               if (!this.permadeath$swapped) {
                  TrialSpawnerConfig old = this.ominousConfig;
                  SimpleWeightedRandomList<ResourceKey<LootTable>> newLootTables = SimpleWeightedRandomList.<ResourceKey<LootTable>>builder().add(PERMADEATH_OMINOUS_CONSUMABLES).add(BuiltInLootTables.SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY).build();
                  this.ominousConfig = new TrialSpawnerConfig(old.spawnRange(), old.totalMobs(), old.simultaneousMobs(), old.totalMobsAddedPerPlayer(), old.simultaneousMobsAddedPerPlayer(), old.ticksBetweenSpawn(), old.spawnPotentialsDefinition(), newLootTables, old.itemsToDropWhenOminous());
                  this.permadeath$swapped = true;
               }
            }
         }
      }
   }
}
