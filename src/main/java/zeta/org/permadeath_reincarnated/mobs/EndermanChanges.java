package zeta.org.permadeath_reincarnated.mobs;

import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import zeta.org.permadeath_reincarnated.systems.DayGlobalCount;

@EventBusSubscriber
public class EndermanChanges {
   private static final Random RANDOM = new Random();

   @SubscribeEvent
   public static void onSpawn(EntityJoinLevelEvent event) {
      Entity entity = event.getEntity();
      int day = DayGlobalCount.CURRENT_DAY;
      if (entity instanceof EnderMan enderMan) {
         if (!event.loadedFromDisk()) {
            if (!enderMan.level().isClientSide) {
               if (day >= 30) {
                  enderMan.setCustomName(Component.literal("Super Enderman").withStyle(ChatFormatting.DARK_PURPLE));
                  enderMan.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, true));
               }

               if (day >= 40) {
                  int chance = 1 + RANDOM.nextInt(100);
                  if (chance <= 20) {
                     Player nearest = enderMan.level().getNearestPlayer(enderMan, 64.0);
                     if (nearest != null && !nearest.isCreative() && !nearest.isSpectator()) {
                        enderMan.setAggressive(true);
                        enderMan.setTarget(nearest);
                     }
                  }
               }

               if (day >= 60) {
                  enderMan.setCustomName(Component.literal("Mega Enderman").withStyle(ChatFormatting.DARK_PURPLE));
                  enderMan.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 9, false, true));
               }
            }
         }
      }
   }
}
