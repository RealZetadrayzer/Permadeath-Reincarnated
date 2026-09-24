package zeta.org.permadeath_reincarnated.dataGen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = "permadeath_reincarnated", bus = Bus.MOD)
public class DataGenerators {
   @SubscribeEvent
   public static void gatherData(GatherDataEvent event) {
      DataGenerator generator = event.getGenerator();
      PackOutput packOutput = generator.getPackOutput();
      ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
      generator.addProvider(event.includeClient(), new PermadeathItemModelProvider(packOutput, existingFileHelper));
   }
}
