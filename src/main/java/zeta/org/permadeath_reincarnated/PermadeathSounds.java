package zeta.org.permadeath_reincarnated;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PermadeathSounds {
   public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, "permadeath_reincarnated");
   public static final DeferredHolder<SoundEvent, SoundEvent> PARROT_BOMB_PLANTED = SOUND_EVENTS.register(
      "parrot_bomb_planted", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("permadeath_reincarnated", "parrot_bomb_planted"))
   );
}
