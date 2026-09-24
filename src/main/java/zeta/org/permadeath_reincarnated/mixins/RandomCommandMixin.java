package zeta.org.permadeath_reincarnated.mixins;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.commands.RandomCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RandomCommand.class)
public class RandomCommandMixin {
   @Redirect(
      method = "register",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;literal(Ljava/lang/String;)Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;")
   )
   private static LiteralArgumentBuilder<CommandSourceStack> gateRandomCommand(String name) {
      LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(name);
      if ("random".equals(name)) {
         builder.requires(src -> src.hasPermission(2));
      }

      return builder;
   }
}
