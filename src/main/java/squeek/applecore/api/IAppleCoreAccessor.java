package squeek.applecore.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import squeek.applecore.api.food.FoodValues;

public interface IAppleCoreAccessor {

    /**
     * Check whether or not the given ItemStack is an edible food.
     *
     * Any ItemStack that gives a return of true in this method will also return valid FoodValues from the
     * {@link #getFoodValues}/{@link #getFoodValuesForPlayer} methods.<br>
     * <br>
     * This method should be preferred when doing something like determining whether or not to show food values in an
     * item's tooltip, as it is more inclusive than a simple {@code instanceof ItemFood} check.
     */
    public boolean isFood(ItemStack food);

    /**
     * Get player-agnostic food values.
     *
     * @return The food values, or {@code null} if none were found.
     */
    public FoodValues getFoodValues(ItemStack food);

    /**
     * Get player-specific food values.
     *
     * @return The food values, or {@code null} if none were found.
     */
    public FoodValues getFoodValuesForPlayer(ItemStack food, EntityPlayer player);

    /**
     * Get unmodified (vanilla) food values.
     *
     * @return The food values, or {@code null} if none were found.
     */
    public FoodValues getUnmodifiedFoodValues(ItemStack food);

    /**
     * @return The current exhaustion level of the {@code player}.
     */
    public float getExhaustion(EntityPlayer player);

    /**
     * @return The maximum exhaustion level of the {@code player}.<br>
     *         <br>
     *         Note: Maximum exhaustion refers to the amount of exhaustion that will trigger
     *         {@link squeek.applecore.api.hunger.ExhaustionEvent.Exhausted} events; exhaustion can exceed the maximum
     *         exhaustion value.
     */
    public float getMaxExhaustion(EntityPlayer player);

    /**
     * @return The number of ticks between health being regenerated by the {@code player}.
     */
    public int getHealthRegenTickPeriod(EntityPlayer player);

    /**
     * @return The number of ticks between starvation damage being dealt to the {@code player}.
     */
    public int getStarveDamageTickPeriod(EntityPlayer player);
}
