package squeek.applecore.mixins.late.natura;

import cpw.mods.fml.common.eventhandler.Event;
import java.util.Random;
import mods.natura.blocks.crops.Glowshroom;
import net.minecraft.block.BlockMushroom;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.applecore.api.AppleCoreAPI;

@Mixin(Glowshroom.class)
public class GlowshroomMixin extends BlockMushroom {

    @Unique
    private int previousMetadata;

    @Unique
    private Event.Result allowGrowthResult;

    @Inject(method = "updateTick", at = @At("HEAD"))
    private void beforeUpdateTick(
            World world, int blockX, int blockY, int blockZ, Random random, CallbackInfo callbackInfo) {
        allowGrowthResult = AppleCoreAPI.dispatcher.validatePlantGrowth(this, world, blockX, blockY, blockZ, random);
        previousMetadata = world.getBlockMetadata(blockX, blockY, blockZ);
    }

    @Redirect(method = "updateTick", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int redirectNextInt(Random random, int limit25) {
        if (allowGrowthResult == Event.Result.ALLOW) {
            return 0; // true
        }
        if (allowGrowthResult == Event.Result.DEFAULT) {
            return random.nextInt(limit25);
        }
        return -1; // false
    }

    @Inject(
            method = "updateTick",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z",
                            shift = At.Shift.AFTER))
    private void afterSetBlock(
            World world, int blockX, int blockY, int blockZ, Random random, CallbackInfo callbackInfo) {
        AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, blockX, blockY, blockZ, previousMetadata);
    }
}
