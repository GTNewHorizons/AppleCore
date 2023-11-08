package squeek.applecore.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockReed;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;

import cpw.mods.fml.common.eventhandler.Event;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockReed.class)
public class BlockReedMixin extends Block {

    private BlockReedMixin() {
        super(null);
    }

    @Inject(
            method = "updateTick",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I"),
            cancellable = true)
    private void afterGetBlockMetadata(World world, int blockX, int blockY, int blockZ, Random random,
            CallbackInfo callbackInfo, @Local(name = "i1") int i1,
            @Share("wasAllowedToGrow") LocalBooleanRef wasAllowedToGrow,
            @Share("previousMetadata") LocalIntRef previousMetadata) {
        if (AppleCoreAPI.dispatcher.validatePlantGrowth(this, world, blockX, blockY, blockZ, random)
                == Event.Result.DENY) {
            wasAllowedToGrow.set(true);
            previousMetadata.set(i1);
            callbackInfo.cancel();
        }
    }

    @Inject(method = "updateTick", at = @At("RETURN"))
    private void afterUpdateTick(World world, int blockX, int blockY, int blockZ, Random random,
            CallbackInfo callbackInfo, @Share("wasAllowedToGrow") LocalBooleanRef wasAllowedToGrow,
            @Share("previousMetadata") LocalIntRef previousMetadata) {
        if (wasAllowedToGrow.get()) {
            AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, previousMetadata.get());
        }
    }
}
