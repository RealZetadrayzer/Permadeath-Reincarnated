package zeta.org.permadeath_reincarnated.items;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PermadeathDataComponents {
   public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.createDataComponents("permadeath_reincarnated");
   public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> LOCKED_SLOT_MARKER = DATA_COMPONENTS.register("locked_slot", () -> DataComponentType.<CompoundTag>builder().persistent(CompoundTag.CODEC).build());
}
