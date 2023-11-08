package squeek.applecore.mixins.late.harvestcraft;

import java.util.Random;

import net.minecraft.block.BlockFlower;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.pam.harvestcraft.BlockPamSapling;

import cpw.mods.fml.common.eventhandler.Event;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockPamSapling.class)
public abstract class BlockPamSaplingMixin extends BlockFlower {

    private BlockPamSaplingMixin() {
        super(0);
    }

    @Shadow(remap = false)
    public abstract void markOrGrowMarked(World world, int blockX, int blockY, int blockZ, Random random);

    /**
     * @author squeek592, SinTh0r4s
     * @reason AppleCore integration
     */
    @Overwrite
    public void updateTick(World world, int blockX, int blockY, int blockZ, Random random) {
        if (!world.isRemote) {
            super.updateTick(world, blockX, blockY, blockZ, random);
            Event.Result allowGrowthResult = AppleCoreAPI.dispatcher
                    .validatePlantGrowth(this, world, blockX, blockY, blockZ, random);
            if (allowGrowthResult == Event.Result.ALLOW || (allowGrowthResult == Event.Result.DEFAULT
                    && world.getBlockLightValue(blockX, blockY + 1, blockZ) >= 9
                    && random.nextInt(7) == 0)) {
                int previousMetadata = world.getBlockMetadata(blockX, blockY, blockZ);
                markOrGrowMarked(world, blockX, blockY, blockZ, random);
                AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, previousMetadata);
            }
        }
    }
}
