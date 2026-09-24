package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobSpawnTypeAccessor {
   @Accessor("spawnType")
   void setSpawnType(MobSpawnType var1);
}
