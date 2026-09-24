package zeta.org.permadeath_reincarnated.systems;

import java.util.concurrent.ConcurrentLinkedQueue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;

@EventBusSubscriber
public class ScheduleInTicks {
   private static final ConcurrentLinkedQueue<ScheduleInTicks.DelayedTask> TASKS = new ConcurrentLinkedQueue<>();

   public static void schedule(Runnable r, int delayTicks) {
      TASKS.add(new ScheduleInTicks.DelayedTask(r, delayTicks));
   }

   @SubscribeEvent
   public static void onServerTick(Post event) {
      TASKS.removeIf(task -> {
         task.ticksLeft--;
         if (task.ticksLeft <= 0) {
            task.runnable.run();
            return true;
         } else {
            return false;
         }
      });
   }

   private static class DelayedTask {
      Runnable runnable;
      int ticksLeft;

      DelayedTask(Runnable run, int delay) {
         this.runnable = run;
         this.ticksLeft = delay;
      }
   }
}
