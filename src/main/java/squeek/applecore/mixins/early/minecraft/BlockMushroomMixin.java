package squeek.applecore.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockMushroom;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockMushroom.class)
public class BlockMushroomMixin extends BlockBush {

    @ModifyExpressionValue(
            method = "updateTick",
            at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0))
    private int onUpdateTick(int original, Random random, World worldIn, int x, int y, int z,
            @Share("executedCondition") LocalBooleanRef executedCondition) {
        switch (AppleCoreAPI.dispatcher.validatePlantGrowth(this, worldIn, x, y, z, random)) {
            case ALLOW:
                executedCondition.set(true);
                return 0;
            case DEFAULT:
                executedCondition.set(original == 0);
                return original;
            default: // DENY
                return -1;
        }
    }

    @Inject(method = "updateTick", at = @At("RETURN"))
    private void afterUpdateTick(World worldIn, int x, int y, int z, Random random, CallbackInfo ci,
            @Share("executedCondition") LocalBooleanRef executedCondition) {
        if (executedCondition.get()) {
            AppleCoreAPI.dispatcher.announcePlantGrowthWithoutMetadataChange(this, worldIn, x, y, z);
        }
    }
}
