package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Creeper.class)
public interface CreeperAccessor {
   @Accessor("maxSwell")
   int getMaxSwell();

   @Accessor("maxSwell")
   void setMaxSwell(int var1);
}
