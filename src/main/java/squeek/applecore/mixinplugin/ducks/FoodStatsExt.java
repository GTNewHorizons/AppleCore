package squeek.applecore.mixinplugin.ducks;

import net.minecraft.entity.player.EntityPlayer;

public interface FoodStatsExt {

    void setPlayer(EntityPlayer entityPlayer);

    void setStarveTimer(int starveTimer);
}
