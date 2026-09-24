package zeta.org.permadeath_reincarnated.systems.clientsync;

public enum PermadeathClientPlayerSynchedDataIds {
   CONSUMPTION(8);

   private final int id;

   PermadeathClientPlayerSynchedDataIds(int id) {
      this.id = id;
   }

   public int id() {
      return this.id;
   }
}
