package zeta.org.permadeath_reincarnated.systems;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;

public class MobPreventEquipmentDrops {
   public static void preventAllEquipmentDrop(Mob mob) {
      mob.setDropChance(EquipmentSlot.HEAD, 0.0F);
      mob.setDropChance(EquipmentSlot.CHEST, 0.0F);
      mob.setDropChance(EquipmentSlot.LEGS, 0.0F);
      mob.setDropChance(EquipmentSlot.FEET, 0.0F);
      mob.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
      mob.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
   }

   public static void preventEquipmentDrop(Mob mob) {
      mob.setDropChance(EquipmentSlot.HEAD, 0.0F);
      mob.setDropChance(EquipmentSlot.CHEST, 0.0F);
      mob.setDropChance(EquipmentSlot.LEGS, 0.0F);
      mob.setDropChance(EquipmentSlot.FEET, 0.0F);
   }

   public static void preventHelmetDrop(Mob mob) {
      mob.setDropChance(EquipmentSlot.HEAD, 0.0F);
   }

   public static void preventChestplateDrop(Mob mob) {
      mob.setDropChance(EquipmentSlot.CHEST, 0.0F);
   }

   public static void preventLeggingsDrop(Mob mob) {
      mob.setDropChance(EquipmentSlot.LEGS, 0.0F);
   }

   public static void preventBootsDrop(Mob mob) {
      mob.setDropChance(EquipmentSlot.FEET, 0.0F);
   }

   public static void preventAllHandsDrop(Mob mob) {
      mob.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
      mob.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
   }

   public static void preventMainhandDrop(Mob mob) {
      mob.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
   }

   public static void preventOffhandDrop(Mob mob) {
      mob.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
   }
}
