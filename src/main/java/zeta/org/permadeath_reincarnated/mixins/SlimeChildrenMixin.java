package zeta.org.permadeath_reincarnated.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slime.class)
public class SlimeChildrenMixin {
   @Inject(
      method = "remove",
      at = @At(
         value = "INVOKE",
         target = "Lnet/neoforged/neoforge/event/EventHooks;onMobSplit(Lnet/minecraft/world/entity/Mob;Ljava/util/List;)Lnet/neoforged/neoforge/event/entity/living/MobSplitEvent;"
      )
   )
   private void permadeath$copyParentTagsToChildren(RemovalReason reason, CallbackInfo ci, @Local ArrayList<Mob> children) {
      Slime parent = (Slime)(Object)this;
      Set<String> parentTags = parent.getTags();
      if (!parentTags.isEmpty()) {
         for (Mob child : children) {
            for (String tag : parentTags) {
               child.addTag(tag);
            }

            if (child instanceof Slime slimeChild) {
               slimeChild.setSize(slimeChild.getSize(), true);
            }
         }
      }
   }
}
