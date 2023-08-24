package squeek.applecore.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockDirectional;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.eventhandler.Event;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockCocoa.class)
public abstract class BlockCocoaMixin extends BlockDirectional {

    protected BlockCocoaMixin() {
        super(null);
    }

    /**
     * @author squeek
     * @reason Check if cocoa can grow and announce that it has done so.
     */
    @Overwrite
    public void updateTick(World world, int blockX, int blockY, int blockZ, Random random) {
        Event.Result allowGrowthResult = AppleCoreAPI.dispatcher
                .validatePlantGrowth(this, world, blockX, blockY, blockZ, random);

        if (!canBlockStay(world, blockX, blockY, blockZ)) {
            dropBlockAsItem(world, blockX, blockY, blockZ, world.getBlockMetadata(blockX, blockY, blockZ), 0);
            world.setBlock(blockX, blockY, blockZ, getBlockById(0), 0, 2);
        } else if (allowGrowthResult == Event.Result.ALLOW
                || (allowGrowthResult == Event.Result.DEFAULT && world.rand.nextInt(5) == 0)) {
                    int metadata = world.getBlockMetadata(blockX, blockY, blockZ);
                    int i1 = BlockCocoa.func_149987_c(metadata);

                    if (i1 < 2) {
                        ++i1;
                        world.setBlockMetadataWithNotify(blockX, blockY, blockZ, i1 << 2 | getDirection(metadata), 2);
                        AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, metadata);
                    }
                }
    }
}
