package zeta.org.permadeath_reincarnated.mixins;

import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BaseSpawner.class)
public interface BaseSpawnerAccessor {
   @Accessor("nextSpawnData")
   SpawnData getNextSpawnData();

   @Accessor("nextSpawnData")
   void setNextSpawnData(SpawnData var1);

   @Accessor("spawnCount")
   int getSpawnCount();

   @Accessor("spawnCount")
   void setSpawnCount(int var1);

   @Accessor("spawnRange")
   int getSpawnRange();

   @Accessor("spawnRange")
   void setSpawnRange(int var1);

   @Accessor("minSpawnDelay")
   int getMinSpawnDelay();

   @Accessor("minSpawnDelay")
   void setMinSpawnDelay(int var1);

   @Accessor("maxSpawnDelay")
   int getMaxSpawnDelay();

   @Accessor("maxSpawnDelay")
   void setMaxSpawnDelay(int var1);
}
