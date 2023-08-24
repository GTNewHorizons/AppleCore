package squeek.applecore.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.eventhandler.Event;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockSapling.class)
public abstract class BlockSaplingMixin extends BlockBush {

    @Shadow
    public abstract void func_149879_c(World world, int blockX, int blockY, int blockZ, Random random);

    @Inject(
            method = "updateTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockBush;updateTick(Lnet/minecraft/world/World;IIILjava/util/Random;)V",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void afterSuperUpdateTick(World world, int blockX, int blockY, int blockZ, Random random,
            CallbackInfo callbackInfo) {
        Event.Result allowGrowthResult = AppleCoreAPI.dispatcher
                .validatePlantGrowth(this, world, blockX, blockY, blockZ, random);
        if (allowGrowthResult == Event.Result.ALLOW || (allowGrowthResult == Event.Result.DEFAULT
                && world.getBlockLightValue(blockX, blockY + 1, blockZ) >= 9
                && random.nextInt(7) == 0)) {
            func_149879_c(world, blockX, blockY, blockZ, random);
            AppleCoreAPI.dispatcher.announcePlantGrowthWithoutMetadataChange(this, world, blockX, blockY, blockZ);
            callbackInfo.cancel();
        }
    }
}
