package squeek.applecore.mixins.natura;

import cpw.mods.fml.common.eventhandler.Event;
import java.util.Random;
import mods.natura.blocks.crops.CropBlock;
import net.minecraft.block.BlockBush;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends BlockBush {

    @Shadow(remap = false)
    public abstract int getMaxGrowth(int metadata);

    @Shadow(remap = false)
    protected abstract float getGrowthRate(World world, int blockX, int blockY, int blockZ, int metadata, int light);

    @Override
    public void updateTick(World world, int blockX, int blockY, int blockZ, Random random) {
        checkAndDropBlock(world, blockX, blockY, blockZ);
        int light = world.getBlockLightValue(blockX, blockY, blockZ);
        Event.Result allowGrowthResult =
                AppleCoreAPI.dispatcher.validatePlantGrowth(this, world, blockX, blockY, blockZ, random);
        if (allowGrowthResult == Event.Result.ALLOW || (allowGrowthResult == Event.Result.DEFAULT && light >= 8)) {
            int metadata = world.getBlockMetadata(blockX, blockY, blockZ);
            if (getMaxGrowth(metadata) != metadata) {
                float grow = getGrowthRate(world, blockX, blockY, blockZ, metadata, light);
                if (random.nextInt((int) (60.0F / grow) + 1) == 0) {
                    world.setBlockMetadataWithNotify(blockX, blockY, blockZ, metadata + 1, 2);
                    AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, metadata);
                }
            }
        }
    }
}
