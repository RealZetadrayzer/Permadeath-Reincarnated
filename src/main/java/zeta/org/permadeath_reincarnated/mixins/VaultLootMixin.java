package zeta.org.permadeath_reincarnated.mixins;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultConfig;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity.Server;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(Server.class)
public class VaultLootMixin {
   @Unique
   private static final ResourceKey<LootTable> VANILLA_OMINOUS_LOOT = ResourceKey.create(
      Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace("chests/trial_chambers/reward_ominous")
   );
   @Unique
   private static final ResourceKey<LootTable> PERMADEATH_OMINOUS_LOOT = ResourceKey.create(
      Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "chests/trial_chambers/permadeath_reward_ominous")
   );

   @Inject(method = "tick", at = @At("HEAD"))
   private static void permadeath$swapLootTable(
      ServerLevel level, BlockPos pos, BlockState state, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, CallbackInfo ci
   ) {
      if ((Boolean)state.getValue(VaultBlock.OMINOUS)) {
         boolean flag = (Boolean)PermadeathConfig.CUSTOM_CHANGES.get();
         int day = DayGlobalCount.CURRENT_DAY;
         ResourceKey<LootTable> target = day >= 20 && flag ? PERMADEATH_OMINOUS_LOOT : VANILLA_OMINOUS_LOOT;
         if (!config.lootTable().equals(target) || !config.overrideLootTableToDisplay().orElse(config.lootTable()).equals(target)) {
            if (level.getBlockEntity(pos) instanceof VaultBlockEntity vault) {
               VaultConfig old = vault.getConfig();
               VaultConfig replaced = new VaultConfig(
                  target, old.activationRange(), old.deactivationRange(), old.keyItem(), Optional.of(target), old.playerDetector(), old.entitySelector()
               );
               vault.setConfig(replaced);
               vault.setChanged();
               level.sendBlockUpdated(pos, state, state, 2);
            }
         }
      }
   }
}
