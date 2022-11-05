package squeek.applecore.mixins.minecraft;

import cpw.mods.fml.common.eventhandler.Event;
import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockCrops.class)
public class BlockCropsMixin extends BlockBush {

    @Shadow
    private float func_149864_n(World world, int blockX, int blockY, int blockZ) {
        throw new IllegalStateException("Mixin was unable to shadow func_149864_n()!");
    }

    /**
     * @author squeek
     * @reason Check if crops can grow and announce that they have done so.
     */
    @Overwrite
    public void updateTick(World world, int blockX, int blockY, int blockZ, Random random) {
        super.updateTick(world, blockX, blockY, blockZ, random);

        Event.Result allowGrowthResult =
                AppleCoreAPI.dispatcher.validatePlantGrowth(this, world, blockX, blockY, blockZ, random);
        if (allowGrowthResult == Event.Result.ALLOW
                || (allowGrowthResult == Event.Result.DEFAULT
                        && world.getBlockLightValue(blockX, blockY + 1, blockZ) >= 9)) {
            int metadata = world.getBlockMetadata(blockX, blockY, blockZ);
            if (metadata < 7) {
                float f = this.func_149864_n(world, blockX, blockY, blockZ);

                if (allowGrowthResult == Event.Result.ALLOW
                        || (allowGrowthResult == Event.Result.DEFAULT && random.nextInt((int) (25.0F / f) + 1) == 0)) {
                    world.setBlockMetadataWithNotify(blockX, blockY, blockZ, metadata + 1, 2);
                    AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, metadata);
                }
            }
        }
    }
}
