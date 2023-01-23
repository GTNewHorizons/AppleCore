package squeek.applecore.api_impl;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import squeek.applecore.api.AppleCoreAPI;
import squeek.applecore.api.IAppleCoreAccessor;
import squeek.applecore.api.IAppleCoreMutator;
import squeek.applecore.api.food.FoodEvent;
import squeek.applecore.api.food.FoodValues;
import squeek.applecore.api.food.IEdible;
import squeek.applecore.api.hunger.ExhaustionEvent;
import squeek.applecore.api.hunger.HealthRegenEvent;
import squeek.applecore.api.hunger.StarvationEvent;
import squeek.applecore.mixinplugin.ducks.FoodStatsExt;
import squeek.applecore.mixins.early.minecraft.accessors.FoodStatsAccessor;

public enum AppleCoreAccessorMutatorImpl implements IAppleCoreAccessor, IAppleCoreMutator {
    INSTANCE;

    AppleCoreAccessorMutatorImpl() {
        AppleCoreAPI.accessor = this;
        AppleCoreAPI.mutator = this;
    }

    /*
     * IAppleCoreAccessor implementation
     */
    @Override
    public boolean isFood(ItemStack food) {
        return isEdible(food) && getUnmodifiedFoodValues(food) != null;
    }

    private boolean isEdible(ItemStack food) {
        if (food == null || food.getItem() == null) return false;

        EnumAction useAction = food.getItem().getItemUseAction(food);
        if (useAction == EnumAction.eat || useAction == EnumAction.drink) return true;

        // assume Block-based foods are edible
        return AppleCoreAPI.registry.getEdibleBlockFromItem(food.getItem()) != null;
    }

    @Override
    public FoodValues getUnmodifiedFoodValues(ItemStack food) {
        if (food != null && food.getItem() != null) {
            if (food.getItem() instanceof IEdible) {
                return ((IEdible) food.getItem()).getFoodValues(food);
            } else if (food.getItem() instanceof ItemFood) {
                return getItemFoodValues((ItemFood) food.getItem(), food);
            }
            Block block = AppleCoreAPI.registry.getEdibleBlockFromItem(food.getItem());
            if (block instanceof IEdible) {
                return ((IEdible) block).getFoodValues(food);
            }
        }
        return null;
    }

    private FoodValues getItemFoodValues(ItemFood itemFood, ItemStack itemStack) {
        return new FoodValues(itemFood.func_150905_g(itemStack), itemFood.func_150906_h(itemStack));
    }

    @Override
    public FoodValues getFoodValues(ItemStack food) {
        FoodValues foodValues = getUnmodifiedFoodValues(food);
        if (foodValues != null) {
            FoodEvent.GetFoodValues event = new FoodEvent.GetFoodValues(food, foodValues);
            MinecraftForge.EVENT_BUS.post(event);
            return event.foodValues;
        }
        return null;
    }

    @Override
    public FoodValues getFoodValuesForPlayer(ItemStack food, EntityPlayer player) {
        FoodValues foodValues = getFoodValues(food);
        if (foodValues != null) {
            FoodEvent.GetPlayerFoodValues event = new FoodEvent.GetPlayerFoodValues(player, food, foodValues);
            MinecraftForge.EVENT_BUS.post(event);
            return event.foodValues;
        }
        return null;
    }

    @Override
    public float getMaxExhaustion(EntityPlayer player) {
        ExhaustionEvent.GetMaxExhaustion event = new ExhaustionEvent.GetMaxExhaustion(player);
        MinecraftForge.EVENT_BUS.post(event);
        return event.maxExhaustionLevel;
    }

    @Override
    public int getHealthRegenTickPeriod(EntityPlayer player) {
        HealthRegenEvent.GetRegenTickPeriod event = new HealthRegenEvent.GetRegenTickPeriod(player);
        MinecraftForge.EVENT_BUS.post(event);
        return event.regenTickPeriod;
    }

    @Override
    public int getStarveDamageTickPeriod(EntityPlayer player) {
        StarvationEvent.GetStarveTickPeriod event = new StarvationEvent.GetStarveTickPeriod(player);
        MinecraftForge.EVENT_BUS.post(event);
        return event.starveTickPeriod;
    }

    @Override
    public float getExhaustion(EntityPlayer player) {
        return ((FoodStatsAccessor) player.getFoodStats()).getFoodExhaustionLevel();
    }

    /*
     * IAppleCoreMutator implementation
     */
    @Override
    public void setExhaustion(EntityPlayer player, float exhaustion) {
        ((FoodStatsAccessor) player.getFoodStats()).setFoodExhaustionLevel(exhaustion);
    }

    @Override
    public void setHunger(EntityPlayer player, int hunger) {
        ((FoodStatsAccessor) player.getFoodStats()).setFoodlevel(hunger);
    }

    @Override
    public void setSaturation(EntityPlayer player, float saturation) {
        ((FoodStatsAccessor) player.getFoodStats()).setFoodSaturationLevel(saturation);
    }

    @Override
    public void setHealthRegenTickCounter(EntityPlayer player, int tickCounter) {
        ((FoodStatsAccessor) player.getFoodStats()).setFoodTimer(tickCounter);
    }

    @Override
    public void setStarveDamageTickCounter(EntityPlayer player, int tickCounter) {
        ((FoodStatsExt) player.getFoodStats()).setStarveTimer(tickCounter);
    }
}
