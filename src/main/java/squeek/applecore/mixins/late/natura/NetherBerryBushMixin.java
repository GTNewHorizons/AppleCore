package squeek.applecore.mixins.late.natura;

import java.util.Random;

import net.minecraft.block.BlockLeavesBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.eventhandler.Event;
import mods.natura.blocks.crops.NetherBerryBush;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(NetherBerryBush.class)
public class NetherBerryBushMixin extends BlockLeavesBase {

    private NetherBerryBushMixin() {
        super(null, false);
    }

    // Identical to BerryBushMixin
    @Overwrite
    public void updateTick(World world, int blockX, int blockY, int blockZ, Random random) {
        if (!world.isRemote) {
            int height;
            for (height = 1; world.getBlock(blockX, blockY - height, blockZ) == this; ++height) {}

            Event.Result allowGrowthResult = AppleCoreAPI.dispatcher
                    .validatePlantGrowth(this, world, blockX, blockY, blockZ, random);
            if (allowGrowthResult == Event.Result.ALLOW
                    || (allowGrowthResult == Event.Result.DEFAULT && random.nextInt(20) == 0
                            && world.getBlockLightValue(blockX, blockY, blockZ) >= 8)) {
                int metadata = world.getBlockMetadata(blockX, blockY, blockZ);
                if (metadata < 12) {
                    world.setBlock(blockX, blockY, blockZ, this, metadata + 4, 3);
                    AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, metadata);
                }

                if (random.nextInt(3) == 0 && height < 3
                        && world.getBlock(blockX, blockY + 1, blockZ) == Blocks.air
                        && metadata >= 8) {
                    world.setBlock(blockX, blockY + 1, blockZ, this, metadata % 4, 3);
                }
            }
        }
    }
}
