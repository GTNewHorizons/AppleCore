package squeek.applecore.mixins.early.minecraft;

import net.minecraft.block.BlockCake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;

import squeek.applecore.api.AppleCoreAPI;
import squeek.applecore.api.food.FoodValues;
import squeek.applecore.api.food.IEdibleBlock;
import squeek.applecore.api.food.ItemFoodProxy;

@Mixin(BlockCake.class)
public class BlockCakeMixin implements IEdibleBlock {

    @Unique
    private boolean AppleCore$isEdibleAtMaxHunger;

    @Redirect(
            method = "func_150036_b",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;addStats(IF)V"))
    private void replaceAddStats(FoodStats instance, int p_75122_1_, float p_75122_2_, @Local EntityPlayer p_150036_5_) {
        ItemStack itemStack = new ItemStack(AppleCoreAPI.registry.getItemFromEdibleBlock((BlockCake) (Object) this));
        new ItemFoodProxy(this).onEaten(itemStack, p_150036_5_);
    }

    @Redirect(
            method = "func_150036_b",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;canEat(Z)Z"))
    private boolean replaceCanEat(EntityPlayer entityPlayer, boolean alwaysFalse) {
        return entityPlayer.canEat(AppleCore$isEdibleAtMaxHunger);
    }

    @Override
    public void setEdibleAtMaxHunger(boolean value) {
        AppleCore$isEdibleAtMaxHunger = value;
    }

    @Override
    public FoodValues getFoodValues(ItemStack itemStack) {
        return new FoodValues(2, 0.1f);
    }
}
