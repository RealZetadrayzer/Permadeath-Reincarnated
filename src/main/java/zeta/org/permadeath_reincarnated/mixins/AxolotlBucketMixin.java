package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Axolotl.class)
public abstract class AxolotlBucketMixin {
   @Inject(method = "saveToBucketTag", at = @At("TAIL"))
   private void permadeath$saveEffectsToBucket(ItemStack bucket, CallbackInfo ci) {
      Axolotl self = (Axolotl)(Object)this;
      CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, tag -> {
         tag.putBoolean("permadeath_bucketed", true);
         ListTag effects = new ListTag();

         for (MobEffectInstance effect : self.getActiveEffects()) {
            if (effect.save() instanceof CompoundTag compoundTag) {
               effects.add(compoundTag);
            }
         }

         tag.put("permadeath_effects", effects);
         tag.putBoolean("permadeath_isGuardian", self.getTags().contains("isGuardian"));
         tag.putBoolean("permadeath_protectorAxolotl", self.getTags().contains("protectorAxolotl"));
      });
   }

   @Inject(method = "loadFromBucketTag", at = @At("TAIL"))
   private void permadeath$loadEffectsFromBucket(CompoundTag tag, CallbackInfo ci) {
      Axolotl self = (Axolotl)(Object)this;
      if (tag.contains("permadeath_effects", 9)) {
         ListTag list = tag.getList("permadeath_effects", 10);

         for (int i = 0; i < list.size(); i++) {
            CompoundTag effectTag = list.getCompound(i);
            MobEffectInstance effect = MobEffectInstance.load(effectTag);
            if (effect != null) {
               self.addEffect(effect);
            }
         }
      }

      if (tag.getBoolean("permadeath_isGuardian")) {
         self.addTag("isGuardian");
      }

      if (tag.getBoolean("permadeath_protectorAxolotl")) {
         self.addTag("protectorAxolotl");
      }

      if (tag.getBoolean("permadeath_bucketed")) {
         self.addTag("permadeathBucketed");
      }
   }
}
