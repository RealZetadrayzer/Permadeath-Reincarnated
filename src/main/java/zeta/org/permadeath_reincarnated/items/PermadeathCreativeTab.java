package zeta.org.permadeath_reincarnated.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PermadeathCreativeTab {
   public static final DeferredRegister<CreativeModeTab> ITEM_GROUPS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "permadeath_reincarnated");
   public static final DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> PERMADEATH_TAB = ITEM_GROUPS.register(
      "permadeath",
      () -> CreativeModeTab.builder()
         .icon(() -> ((ArmorItem)PermadeathItemsRegistry.PERMA_NETHERITE_CHESTPLATE.get()).getDefaultInstance())
         .displayItems(
            (parameters, output) -> PermadeathItemsRegistry.ITEMS.getEntries().forEach(entry -> output.accept(((Item)entry.get()).getDefaultInstance()))
         )
         .title(Component.translatable("itemGroups.permadeath_reincarnated"))
         .build()
   );

   public static void register(IEventBus eventBus) {
      ITEM_GROUPS.register(eventBus);
   }
}
