package zeta.org.permadeath_reincarnated;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class PermadeathConfig {
   private static final Builder BUILDER = new Builder();
   public static final BooleanValue DISABLE_KICK = BUILDER.comment("If this is 'True' players will not get Kicked on Death.").define("disable_kick", false);
   public static final BooleanValue DISABLE_SPECTATOR = BUILDER.comment("If this is 'True' players will not be in 'Game mode Spectator' on Death.")
      .define("disable_spectator", false);
   public static final BooleanValue DISABLE_REWORK = BUILDER.comment(
         "If this is 'True' the crafting/loot reworks made by zetadrayzer and KreKoh will not be applied."
      )
      .define("disable_rework", false);
   public static final BooleanValue DOUBLED_MOBS = BUILDER.comment("If this is 'True' the 'Monster Mob Cap' will be Modified by the value in the config below.")
      .define("doubled_mobs", true);
   public static final IntValue MOB_CAP_MONSTER = BUILDER.comment("This value defines the 'Monster Mob Cap' MAX Amount of Mobs.")
      .defineInRange("mob_cap_monster", 140, 0, Integer.MAX_VALUE);
   public static final IntValue SURVIVOR_MEDAL_TOTEMS_REQUIRED = BUILDER.comment(
         "This value Modifies the required amount of Totems of Undying that need to be burned to get the 'Survivor Medal'."
      )
      .defineInRange("required_totems_to_be_burned", 8, 1, Integer.MAX_VALUE);
   public static final BooleanValue MIKECRACK = BUILDER.comment("If this is 'True' the 'Mikecrack Creepers Everywhere' change will be active.")
      .define("mikecrack_change", false);
   public static final BooleanValue CUSTOM_CHANGES = BUILDER.comment(
         "If this is 'True' all of the custom new changes for newer versions made by zetadrayzer and KreKoh will be on."
      )
      .define("reincarnated_changes", false);
   static final ModConfigSpec SPEC = BUILDER.build();

   static {
      BUILDER.push("permadeath_reincarnated_debug_options");
      BUILDER.pop();
   }
}
