package squeek.applecore.mixins.early.minecraft;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.applecore.api.AppleCoreAPI;
import squeek.applecore.api.food.FoodEvent;
import squeek.applecore.api.food.FoodValues;
import squeek.applecore.api.hunger.ExhaustionEvent;
import squeek.applecore.api.hunger.HealthRegenEvent;
import squeek.applecore.api.hunger.StarvationEvent;
import squeek.applecore.mixinplugin.ducks.FoodStatsExt;

@Mixin(FoodStats.class)
public abstract class FoodStatsMixin implements FoodStatsExt {

    @Unique
    private EntityPlayer entityPlayer;

    private int starveTimer;

    @Override
    public void setStarveTimer(int starveTimer) {
        this.starveTimer = starveTimer;
    }

    @Shadow
    private int foodLevel;

    @Shadow
    private int foodTimer;

    @Shadow
    private float foodExhaustionLevel;

    @Shadow
    private int prevFoodLevel;

    @Shadow
    private float foodSaturationLevel;

    @Override
    public void setPlayer(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }

    @Shadow
    public abstract void addStats(int p_75122_1_, float p_75122_2_);

    @Shadow
    public abstract void addExhaustion(float p_75113_1_);

    @Inject(method = "addStats", at = @At("HEAD"), cancellable = true)
    private void onAddStats(int hunger, float saturationModifier, CallbackInfo callbackInfo) {
        FoodEvent.FoodStatsAddition event =
                new FoodEvent.FoodStatsAddition(entityPlayer, new FoodValues(hunger, saturationModifier));
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCancelable() && event.isCanceled()) {
            callbackInfo.cancel();
        }
    }

    /**
     * @author squeek
     * @reason Customize the way food stats are applied when eaten and launch {@link FoodEvent.FoodEaten}
     */
    @Overwrite
    public void func_151686_a(ItemFood itemFood, ItemStack itemStack) {
        FoodValues modifiedFoodValues = AppleCoreAPI.accessor.getFoodValuesForPlayer(itemStack, entityPlayer);
        int prevFoodLevel = foodLevel;
        float prevSaturationLevel = foodSaturationLevel;

        addStats(modifiedFoodValues.hunger, modifiedFoodValues.saturationModifier);

        MinecraftForge.EVENT_BUS.post(new FoodEvent.FoodEaten(
                entityPlayer,
                itemStack,
                modifiedFoodValues,
                foodLevel - prevFoodLevel,
                foodSaturationLevel - prevSaturationLevel));
    }

    /**
     * @author squeek
     * @reason Overwrite how exhaustion, starvation, and health regeneration work and fire events for each for other mods to act upon.
     */
    @Overwrite
    public void onUpdate(EntityPlayer player) {
        this.prevFoodLevel = foodLevel;

        ExhaustionEvent.AllowExhaustion allowExhaustionEvent = new ExhaustionEvent.AllowExhaustion(player);
        MinecraftForge.EVENT_BUS.post(new ExhaustionEvent.AllowExhaustion(player));
        Event.Result allowExhaustionResult = allowExhaustionEvent.getResult();
        float maxExhaustion = AppleCoreAPI.accessor.getMaxExhaustion(player);
        if (allowExhaustionResult == Event.Result.ALLOW
                || (allowExhaustionResult == Event.Result.DEFAULT && foodExhaustionLevel >= maxExhaustion)) {
            ExhaustionEvent.Exhausted exhaustedEvent =
                    new ExhaustionEvent.Exhausted(player, maxExhaustion, foodExhaustionLevel);
            MinecraftForge.EVENT_BUS.post(exhaustedEvent);

            this.foodExhaustionLevel += exhaustedEvent.deltaExhaustion;
            if (!exhaustedEvent.isCanceled()) {
                foodSaturationLevel = Math.max(foodSaturationLevel + exhaustedEvent.deltaSaturation, 0.0F);
                foodLevel = Math.max(foodLevel + exhaustedEvent.deltaHunger, 0);
            }
        }

        HealthRegenEvent.AllowRegen allowRegenEvent = new HealthRegenEvent.AllowRegen(player);
        MinecraftForge.EVENT_BUS.post(allowRegenEvent);
        if (allowRegenEvent.getResult() == Event.Result.ALLOW
                || (allowRegenEvent.getResult() == Event.Result.DEFAULT
                        && player.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration")
                        && this.foodLevel >= 18
                        && player.shouldHeal())) {
            ++this.foodTimer;

            if (this.foodTimer >= AppleCoreAPI.accessor.getHealthRegenTickPeriod(player)) {
                HealthRegenEvent.Regen regenEvent = new HealthRegenEvent.Regen(player);
                MinecraftForge.EVENT_BUS.post(regenEvent);
                if (!regenEvent.isCanceled()) {
                    player.heal(regenEvent.deltaHealth);
                    addExhaustion(regenEvent.deltaExhaustion);
                }
                foodTimer = 0;
            }
        } else {
            foodTimer = 0;
        }

        StarvationEvent.AllowStarvation allowStarvationEvent = new StarvationEvent.AllowStarvation(player);
        MinecraftForge.EVENT_BUS.post(allowStarvationEvent);
        if (allowStarvationEvent.getResult() == Event.Result.ALLOW
                || (allowStarvationEvent.getResult() == Event.Result.DEFAULT && foodLevel <= 0)) {
            ++starveTimer;

            if (starveTimer >= AppleCoreAPI.accessor.getStarveDamageTickPeriod(player)) {
                StarvationEvent.Starve starveEvent = new StarvationEvent.Starve(player);
                MinecraftForge.EVENT_BUS.post(starveEvent);
                if (!starveEvent.isCanceled()) {
                    player.attackEntityFrom(DamageSource.starve, starveEvent.starveDamage);
                }
                starveTimer = 0;
            }
        } else {
            starveTimer = 0;
        }
    }
}
