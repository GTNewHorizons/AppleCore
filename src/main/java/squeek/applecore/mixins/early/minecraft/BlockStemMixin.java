package squeek.applecore.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockStem;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;

import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockStem.class)
public class BlockStemMixin extends BlockBush {

    @ModifyExpressionValue(
            method = "updateTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockLightValue(III)I"))
    private int redirectGetBlockLightValue(int original, World worldIn, int x, int y, int z, Random random) {
        return switch (AppleCoreAPI.dispatcher.validatePlantGrowth(this, worldIn, x, y, z, random)) {
            case ALLOW -> 9; // true
            case DEFAULT -> original;
            default -> 0; // DENY -> false

        };
    }

    @Inject(
            method = "updateTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I"))
    private void onGetBlockMetadata(World world, int blockX, int blockY, int blockZ, Random random,
            CallbackInfo callbackInfo, @Local(name = "l") int l,
            @Share("previousMetadata") LocalIntRef previousMetadata) {
        previousMetadata.set(l);
    }

    @Inject(
            method = "updateTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockMetadataWithNotify(IIIII)Z",
                    shift = At.Shift.AFTER))
    private void afterSetBlockMetadataWithNotify(World world, int blockX, int blockY, int blockZ, Random random,
            CallbackInfo callbackInfo, @Share("previousMetadata") LocalIntRef previousMetadata) {
        AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, previousMetadata.get());
    }
}
