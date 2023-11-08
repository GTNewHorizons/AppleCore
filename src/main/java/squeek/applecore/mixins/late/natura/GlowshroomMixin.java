package squeek.applecore.mixins.late.natura;

import java.util.Random;

import net.minecraft.block.BlockMushroom;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import mods.natura.blocks.crops.Glowshroom;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(Glowshroom.class)
public class GlowshroomMixin extends BlockMushroom {

    @ModifyExpressionValue(method = "updateTick", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int redirectNextInt(int original, World world, int x, int y, int z, Random random) {
        return switch (AppleCoreAPI.dispatcher.validatePlantGrowth(this, world, x, y, z, random)) {
            case ALLOW -> 0; // true
            case DEFAULT -> original;
            default -> -1; // DENY -> false
        };
    }

    @Inject(
            method = "updateTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z",
                    shift = At.Shift.AFTER))
    private void afterSetBlock(World world, int x, int y, int z, Random random, CallbackInfo ci, @Local(name = "meta") int meta) {
        AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, x, y, z, meta);
    }
}
