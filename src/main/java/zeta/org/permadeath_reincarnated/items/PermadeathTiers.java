package zeta.org.permadeath_reincarnated.items;

import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public enum PermadeathTiers implements Tier {
   NETHERITE(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 10.0F, 5.0F, 18, () -> Ingredient.of(new ItemLike[]{Items.AIR}));

   private final TagKey<Block> incorrectBlocks;
   private final int uses;
   private final float speed;
   private final float damage;
   private final int enchantment;
   private final Supplier<Ingredient> repair;

   PermadeathTiers(TagKey<Block> incorrectBlocks, int uses, float speed, float damage, int enchantment, Supplier<Ingredient> repair) {
      this.incorrectBlocks = incorrectBlocks;
      this.uses = uses;
      this.speed = speed;
      this.damage = damage;
      this.enchantment = enchantment;
      this.repair = repair;
   }

   public int getUses() {
      return this.uses;
   }

   public float getSpeed() {
      return this.speed;
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   public int getEnchantmentValue() {
      return this.enchantment;
   }

   public Ingredient getRepairIngredient() {
      return this.repair.get();
   }

   public TagKey<Block> getIncorrectBlocksForDrops() {
      return this.incorrectBlocks;
   }
}
