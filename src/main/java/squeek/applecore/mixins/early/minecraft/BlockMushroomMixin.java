package squeek.applecore.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockMushroom;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.eventhandler.Event;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(BlockMushroom.class)
public class BlockMushroomMixin extends BlockBush {

    @Unique
    private Event.Result allowGrowthResult;

    @Unique
    private boolean executedCondition = false;

    @Inject(method = "updateTick", at = @At("HEAD"))
    private void beforeUpdateTick(World world, int blockX, int blockY, int blockZ, Random random,
            CallbackInfo callbackInfo) {
        allowGrowthResult = AppleCoreAPI.dispatcher.validatePlantGrowth(this, world, blockX, blockY, blockZ, random);
    }

    @Redirect(method = "updateTick", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0))
    private int onUpdateTick(Random random, int const25) {
        if (allowGrowthResult == Event.Result.ALLOW) {
            executedCondition = true;
            return 0;
        }
        if (allowGrowthResult == Event.Result.DEFAULT) {
            final int i = random.nextInt(25);
            executedCondition = i == 0;
            return i;
        }
        executedCondition = false;
        return -1;
    }

    @Inject(method = "updateTick", at = @At("RETURN"))
    private void afterUpdateTick(World world, int blockX, int blockY, int blockZ, Random random,
            CallbackInfo callbackInfo) {
        if (executedCondition) {
            AppleCoreAPI.dispatcher.announcePlantGrowthWithoutMetadataChange(this, world, blockX, blockY, blockZ);
            executedCondition = false;
        }
    }
}
