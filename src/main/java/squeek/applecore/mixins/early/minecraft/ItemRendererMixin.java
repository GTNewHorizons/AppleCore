package squeek.applecore.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import squeek.applecore.mixinplugin.IAppleCoreEatingEntity;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow
    private Minecraft mc;

    @Redirect(
            method = "renderItemInFirstPerson",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxItemUseDuration()I", ordinal = 0),
            slice =
                    @Slice(
                            from =
                                    @At(
                                            value = "INVOKE",
                                            target =
                                                    "Lnet/minecraft/client/gui/MapItemRenderer;func_148250_a(Lnet/minecraft/world/storage/MapData;Z)V")))
    private int onRenderItemInFirstPerson(ItemStack instance) {
        return ((IAppleCoreEatingEntity) mc.thePlayer).getItemInUseMaxDuration();
    }
}
