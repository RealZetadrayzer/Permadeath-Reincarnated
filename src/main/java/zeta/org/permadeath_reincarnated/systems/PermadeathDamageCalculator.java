package zeta.org.permadeath_reincarnated.systems;

import java.lang.reflect.Field;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.jetbrains.annotations.NotNull;

public class PermadeathDamageCalculator extends ExplosionDamageCalculator {
   public float getEntityDamageAmount(@NotNull Explosion explosion, @NotNull Entity entity) {
      return 0.0F;
   }

   public static Field getField(Class<?> clazz, String name) {
      try {
         Field field = clazz.getDeclaredField(name);
         field.setAccessible(true);
         return field;
      } catch (NoSuchFieldException e) {
         throw new RuntimeException(e);
      }
   }
}
