package squeek.applecore.mixins.early.minecraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import squeek.applecore.api.hunger.HealthRegenEvent;
import squeek.applecore.mixinplugin.ducks.EntityPlayerExt;
import squeek.applecore.mixinplugin.ducks.FoodStatsExt;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase implements EntityPlayerExt {

    @Unique
    private int itemInUseMaxDuration;

    @Shadow
    protected FoodStats foodStats;

    private EntityPlayerMixin() {
        super(null);
    }

    @Shadow
    @SideOnly(Side.CLIENT)
    public abstract ItemStack getItemInUse();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void redirectNewFoodStats(World world, GameProfile gameProfile, CallbackInfo callbackInfo) {
        ((FoodStatsExt) foodStats).setPlayer((EntityPlayer) (Object) this);
    }

    @Inject(
            method = "setItemInUse",
            at = @At(
                    value = "FIELD",
                    target = "net/minecraft/entity/player/EntityPlayer.itemInUseCount : I",
                    shift = At.Shift.AFTER))
    private void onSetItemInUse(ItemStack itemStack, int maxItemUseDuration, CallbackInfo callbackInfo) {
        this.itemInUseMaxDuration = maxItemUseDuration;
    }

    @Redirect(
            method = "getItemInUseDuration",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxItemUseDuration()I"))
    @SideOnly(Side.CLIENT)
    private int redirect(ItemStack instance) {
        EnumAction useAction = getItemInUse().getItemUseAction();
        if (useAction == EnumAction.eat || useAction == EnumAction.drink) return itemInUseMaxDuration;
        else return getItemInUse().getMaxItemUseDuration();
    }

    @Override
    public int getItemInUseMaxDuration() {
        return itemInUseMaxDuration;
    }

    @Redirect(
            method = "onLivingUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;heal (F)V"))
    private void redirectHeal(EntityPlayer thiz, float value) {
        HealthRegenEvent.PeacefulRegen peacefulRegenEvent = new HealthRegenEvent.PeacefulRegen(
                (EntityPlayer) (Object) this);
        MinecraftForge.EVENT_BUS.post(peacefulRegenEvent);
        if (!peacefulRegenEvent.isCanceled()) {
            heal(peacefulRegenEvent.deltaHealth);
        }
    }
}
