package squeek.applecore.mixins.minecraft;

import cpw.mods.fml.common.eventhandler.Event;
import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockStem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockStem.class)
public class BlockStemMixin extends BlockBush {

    @Unique
    private Event.Result allowGrowthResult;

    @Unique
    private int previousMetadata;

    @Inject(
            method = "updateTick",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/World;getBlockLightValue(III)I",
                            shift = At.Shift.BEFORE))
    private void beforeGetBlockLightValue(
            World world, int blockX, int blockY, int blockZ, Random random, CallbackInfo callbackInfo) {
        allowGrowthResult = AppleCoreAPI.dispatcher.validatePlantGrowth(this, world, blockX, blockY, blockZ, random);
    }

    @Redirect(
            method = "updateTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockLightValue(III)I"))
    private int redirectGetBlockLightValue(World world, int blockX, int blockY, int blockZ) {
        if (allowGrowthResult == Event.Result.ALLOW) {
            return 9; // true
        }
        if (allowGrowthResult == Event.Result.DEFAULT) {
            return world.getBlockLightValue(blockX, blockY, blockZ);
        }
        return 0; // false
    }

    @Inject(
            method = "updateTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I"))
    private void onGetBlockMetadata(
            World world, int blockX, int blockY, int blockZ, Random random, CallbackInfo callbackInfo) {
        previousMetadata = world.getBlockMetadata(blockX, blockY, blockZ);
    }

    @Inject(
            method = "updateTick",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/World;setBlockMetadataWithNotify(IIIII)Z",
                            shift = At.Shift.AFTER))
    private void afterSetBlockMetadataWithNotify(
            World world, int blockX, int blockY, int blockZ, Random random, CallbackInfo callbackInfo) {
        AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, previousMetadata);
    }
}
