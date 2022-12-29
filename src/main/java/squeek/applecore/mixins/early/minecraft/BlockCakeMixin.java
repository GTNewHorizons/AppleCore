package squeek.applecore.mixins.early.minecraft;

import net.minecraft.block.BlockCake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.applecore.api.AppleCoreAPI;
import squeek.applecore.api.food.FoodValues;
import squeek.applecore.api.food.IEdibleBlock;
import squeek.applecore.api.food.ItemFoodProxy;

@Mixin(BlockCake.class)
public class BlockCakeMixin implements IEdibleBlock {

    @Unique
    private World world;

    @Unique
    private EntityPlayer entityPlayer;

    @Unique
    private boolean AppleCore_isEdibleAtMaxHunger;

    @Inject(
            method = "func_150036_b",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;addStats(IF)V", shift = At.Shift.BEFORE))
    private void onAddStats(
            World world, int blockX, int blockY, int blockZ, EntityPlayer entityPlayer, CallbackInfo callbackInfo) {
        this.world = world;
        this.entityPlayer = entityPlayer;
    }

    @Redirect(
            method = "func_150036_b",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;addStats(IF)V"))
    private void replaceAddStats(FoodStats instance, int p_75122_1_, float p_75122_2_) {
        ItemStack itemStack = new ItemStack(AppleCoreAPI.registry.getItemFromEdibleBlock((BlockCake) (Object) this));
        new ItemFoodProxy(this).onEaten(itemStack, entityPlayer);
    }

    @Redirect(
            method = "func_150036_b",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;canEat(Z)Z"))
    private boolean replaceCanEat(EntityPlayer entityPlayer, boolean alwaysFalse) {
        return entityPlayer.canEat(AppleCore_isEdibleAtMaxHunger);
    }

    @Override
    public void setEdibleAtMaxHunger(boolean value) {
        AppleCore_isEdibleAtMaxHunger = value;
    }

    @Override
    public FoodValues getFoodValues(ItemStack itemStack) {
        return new FoodValues(2, 0.1f);
    }
}
