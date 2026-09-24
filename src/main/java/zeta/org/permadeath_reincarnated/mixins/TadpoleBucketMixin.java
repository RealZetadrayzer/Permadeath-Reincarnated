package zeta.org.permadeath_reincarnated.mixins;

import java.util.Objects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Tadpole.class)
public abstract class TadpoleBucketMixin {
   @Inject(method = "saveToBucketTag", at = @At("TAIL"))
   private void permadeath$saveEffectsToBucket(ItemStack bucket, CallbackInfo ci) {
      Tadpole self = (Tadpole)(Object)this;
      CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, tag -> {
         tag.putBoolean("permadeath_bucketed", true);
         ListTag effects = new ListTag();

         for (MobEffectInstance effectInstance : self.getActiveEffects()) {
            if (effectInstance.save() instanceof CompoundTag ct) {
               effects.add(ct);
            }
         }

         tag.put("permadeath_effects", effects);
         tag.putBoolean("permadeath_deathTadpole", self.getTags().contains("deathTadpole"));
         tag.putBoolean("permadeath_fromBogged", self.getTags().contains("fromBogged"));
         tag.putFloat("permadeath_base_max_health", (float)Objects.requireNonNull(self.getAttribute(Attributes.MAX_HEALTH)).getBaseValue());
         tag.putFloat("permadeath_base_attack_damage", (float)Objects.requireNonNull(self.getAttribute(Attributes.ATTACK_DAMAGE)).getBaseValue());
      });
   }

   @Inject(method = "loadFromBucketTag", at = @At("TAIL"))
   private void permadeath$loadEffectsFromBucket(CompoundTag tag, CallbackInfo ci) {
      Tadpole self = (Tadpole)(Object)this;
      if (tag.contains("permadeath_effects", 9)) {
         ListTag list = tag.getList("permadeath_effects", 10);

         for (int i = 0; i < list.size(); i++) {
            CompoundTag effectTag = list.getCompound(i);
            MobEffectInstance effectInstance = MobEffectInstance.load(effectTag);
            if (effectInstance != null) {
               self.addEffect(effectInstance);
            }
         }
      }

      if (tag.getBoolean("permadeath_deathTadpole")) {
         self.addTag("deathTadpole");
      }

      if (tag.getBoolean("permadeath_fromBogged")) {
         self.addTag("fromBogged");
      }

      if (tag.getBoolean("permadeath_bucketed")) {
         self.addTag("permadeathBucketed");
      }

      if (tag.contains("permadeath_base_attack_damage")) {
         Objects.requireNonNull(self.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(tag.getFloat("permadeath_base_attack_damage"));
      }

      if (tag.contains("permadeath_base_max_health")) {
         Objects.requireNonNull(self.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(tag.getFloat("permadeath_base_max_health"));
         self.setHealth(self.getMaxHealth());
      }
   }
}
