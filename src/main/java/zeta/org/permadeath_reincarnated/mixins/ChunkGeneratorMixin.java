package zeta.org.permadeath_reincarnated.mixins;

import java.util.Set;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zeta.org.permadeath_reincarnated.PermadeathConfig;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
   @Unique
   private static final Set<ResourceLocation> BLOCKED_AFTER_DAY_40 = Set.of(
      ResourceLocation.withDefaultNamespace("village_plains"),
      ResourceLocation.withDefaultNamespace("village_desert"),
      ResourceLocation.withDefaultNamespace("village_savanna"),
      ResourceLocation.withDefaultNamespace("villages_snowy"),
      ResourceLocation.withDefaultNamespace("village_taiga"),
      ResourceLocation.withDefaultNamespace("desert_pyramid"),
      ResourceLocation.withDefaultNamespace("jungle_pyramid"),
      ResourceLocation.withDefaultNamespace("shipwreck"),
      ResourceLocation.withDefaultNamespace("shipwreck_beached"),
      ResourceLocation.withDefaultNamespace("monument")
   );
   @Unique
   private static final Set<ResourceLocation> BLOCKED_AFTER_DAY_40_CUSTOM = Set.of(
      ResourceLocation.withDefaultNamespace("bastion_remnant"),
      ResourceLocation.withDefaultNamespace("ancient_city"),
      ResourceLocation.withDefaultNamespace("trial_chambers")
   );

   @Inject(method = "tryGenerateStructure", at = @At("HEAD"), cancellable = true)
   private void permadeath$blockStructuresAfterDay40(
      StructureSelectionEntry entry,
      StructureManager structureManager,
      RegistryAccess registryAccess,
      RandomState randomState,
      StructureTemplateManager stm,
      long seed,
      ChunkAccess chunk,
      ChunkPos chunkPos,
      SectionPos sectionPos,
      CallbackInfoReturnable<Boolean> cir
   ) {
      if (DayGlobalCount.CURRENT_DAY >= 40) {
         Structure structure = (Structure)entry.structure().value();
         ResourceLocation id = registryAccess.registryOrThrow(Registries.STRUCTURE).getKey(structure);
         if (id != null && BLOCKED_AFTER_DAY_40.contains(id)) {
            cir.setReturnValue(false);
         }

         if (id != null && BLOCKED_AFTER_DAY_40_CUSTOM.contains(id) && (Boolean)PermadeathConfig.CUSTOM_CHANGES.get()) {
            cir.setReturnValue(false);
         }
      }
   }
}
