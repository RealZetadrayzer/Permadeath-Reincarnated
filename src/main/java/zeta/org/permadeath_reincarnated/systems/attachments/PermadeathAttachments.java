package zeta.org.permadeath_reincarnated.systems.attachments;

import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PermadeathAttachments {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "permadeath_reincarnated");
   public static final Supplier<AttachmentType<BeginningBlessingAttachment>> BLESSING = ATTACHMENTS.register(
      "blessing", () -> AttachmentType.serializable(BeginningBlessingAttachment::new).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<BeginningCurseAttachment>> CURSE = ATTACHMENTS.register(
      "curse", () -> AttachmentType.serializable(BeginningCurseAttachment::new).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<GuardianAllayTotemAttachment>> GUARDIAN_COOLDOWN = ATTACHMENTS.register(
      "totem_cooldown", () -> AttachmentType.serializable(GuardianAllayTotemAttachment::new).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<WitherTimerAttachment>> WITHER_TIMER = ATTACHMENTS.register(
      "wither_timer", () -> AttachmentType.serializable(WitherTimerAttachment::new).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<BurnedTotemCountAttachment>> BURNED_TOTEM_COUNT = ATTACHMENTS.register(
      "burned_totem_count", () -> AttachmentType.serializable(BurnedTotemCountAttachment::new).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<NetherMobRainAttachment>> NETHER_MOB_RAIN_COOLDOWN = ATTACHMENTS.register(
      "nether_mob_rain_cooldown", () -> AttachmentType.serializable(NetherMobRainAttachment::new).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<MinutesPlayedCountAttachment>> MINUTES_PLAYED = ATTACHMENTS.register(
      "minutes_played", () -> AttachmentType.serializable(MinutesPlayedCountAttachment::new).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<TCNDCooldownAttachment>> TCND_COOLDOWN = ATTACHMENTS.register(
      "tcnd_cooldown", () -> AttachmentType.serializable(TCNDCooldownAttachment::new).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<DesperateMeasuresAttachment>> DESPERATE_MEASURES_TIMER = ATTACHMENTS.register(
      "desperate_measures_timer", () -> AttachmentType.serializable(DesperateMeasuresAttachment::new).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<EnhancedTCNDCooldownAttachment>> ENHANCED_TCND_COOLDOWN = ATTACHMENTS.register(
      "enhanced_tcnd_cooldown", () -> AttachmentType.serializable(EnhancedTCNDCooldownAttachment::new).copyOnDeath().build()
   );

   private PermadeathAttachments() {
   }
}
