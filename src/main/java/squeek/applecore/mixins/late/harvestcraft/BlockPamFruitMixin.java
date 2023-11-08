package squeek.applecore.mixins.late.harvestcraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.pam.harvestcraft.BlockPamFruit;

import cpw.mods.fml.common.eventhandler.Event;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockPamFruit.class)
public class BlockPamFruitMixin extends Block {

    private BlockPamFruitMixin() {
        super(null);
    }

    /**
     * @author squeek592, SinTh0r4s
     * @reason AppleCore integration
     */
    @Overwrite
    public void updateTick(World world, int blockX, int blockY, int blockZ, Random random) {
        super.updateTick(world, blockX, blockY, blockZ, random);
        int metadata = world.getBlockMetadata(blockX, blockY, blockZ);
        Event.Result allowGrowthResult = AppleCoreAPI.dispatcher
                .validatePlantGrowth(this, world, blockX, blockY, blockZ, random);
        if (allowGrowthResult == Event.Result.ALLOW
                || (allowGrowthResult == Event.Result.DEFAULT && metadata < 2 && random.nextInt(30) == 0)) {
            world.setBlock(blockX, blockY, blockZ, this, metadata + 1, 2);
            AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, metadata);
        }
    }
}
