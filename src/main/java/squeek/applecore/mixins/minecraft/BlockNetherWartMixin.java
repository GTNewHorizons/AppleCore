package squeek.applecore.mixins.minecraft;

import cpw.mods.fml.common.eventhandler.Event;
import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockNetherWart.class)
public class BlockNetherWartMixin extends BlockBush {

    /**
     * @author squeek
     * @reason Check if nether wart can grow and announce that it has done so.
     */
    @Overwrite
    public void updateTick(World world, int blockX, int blockY, int blockZ, Random random) {
        Event.Result allowGrowthResult =
                AppleCoreAPI.dispatcher.validatePlantGrowth(this, world, blockX, blockY, blockZ, random);
        int metadata = world.getBlockMetadata(blockX, blockY, blockZ);
        if (metadata < 3
                && (allowGrowthResult == Event.Result.ALLOW
                        || (allowGrowthResult == Event.Result.DEFAULT && random.nextInt(10) == 0))) {
            world.setBlockMetadataWithNotify(blockX, blockY, blockZ, metadata + 1, 2);
            AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, metadata);
        }
        super.updateTick(world, blockX, blockY, blockZ, random);
    }
}
