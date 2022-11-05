package squeek.applecore.mixins.minecraft;

import cpw.mods.fml.common.eventhandler.Event;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockReed;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockReed.class)
public class BlockReedMixin extends Block {

    @Unique
    private boolean wasAllowedToGrow = false;

    @Unique
    private int previousMetadata = 0;

    protected BlockReedMixin() {
        super(null);
    }

    @Inject(
            method = "updateTick",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/World;getBlockMetadata(III)I",
                            shift = At.Shift.BEFORE),
            cancellable = true)
    private void beforeGetBlockMetadata(
            World world, int blockX, int blockY, int blockZ, Random random, CallbackInfo callbackInfo) {
        if (AppleCoreAPI.dispatcher.validatePlantGrowth(this, world, blockX, blockY, blockZ, random)
                == Event.Result.DENY) {
            wasAllowedToGrow = true;
            previousMetadata = world.getBlockMetadata(blockX, blockY, blockZ);
            callbackInfo.cancel();
        }
    }

    @Inject(method = "updateTick", at = @At("RETURN"))
    private void afterUpdateTick(
            World world, int blockX, int blockY, int blockZ, Random random, CallbackInfo callbackInfo) {
        if (wasAllowedToGrow) {
            wasAllowedToGrow = false;
            AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, previousMetadata);
        }
    }
}
