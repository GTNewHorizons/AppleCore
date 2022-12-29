package squeek.applecore.mixins.early.minecraft;

import cpw.mods.fml.common.eventhandler.Event;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockCactus.class)
public abstract class BlockCactusMixin extends Block {

    protected BlockCactusMixin() {
        super(null);
    }

    @Shadow
    public abstract void onNeighborBlockChange(
            World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_);

    /**
     * @author squeek
     * @reason Check if cactus can grow and announce that it has done so
     */
    @Overwrite
    public void updateTick(World world, int blockX, int blockY, int blockZ, Random random) {
        if (world.isAirBlock(blockX, blockY + 1, blockZ)) {
            int l;

            for (l = 1; world.getBlock(blockX, blockY - l, blockZ) == this; ++l) {
                ;
            }

            if (l < 3
                    && AppleCoreAPI.dispatcher.validatePlantGrowth(this, world, blockX, blockY, blockZ, random)
                            != Event.Result.DENY) {
                int metadata = world.getBlockMetadata(blockX, blockY, blockZ);

                if (metadata == 15) {
                    world.setBlock(blockX, blockY + 1, blockZ, this);
                    world.setBlockMetadataWithNotify(blockX, blockY, blockZ, 0, 4);
                    onNeighborBlockChange(world, blockX, blockY + 1, blockZ, this);
                } else {
                    world.setBlockMetadataWithNotify(blockX, blockY, blockZ, metadata + 1, 4);
                }
                AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, metadata);
            }
        }
    }
}
