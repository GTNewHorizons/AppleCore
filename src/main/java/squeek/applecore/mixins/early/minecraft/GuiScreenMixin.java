package squeek.applecore.mixins.early.minecraft;

import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import squeek.applecore.client.TooltipOverlayHandler;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {

    @Inject(
            at = @At(
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 0,
                    remap = true,
                    target = "Lnet/minecraft/client/gui/GuiScreen;zLevel:F",
                    value = "FIELD"),
            method = "drawHoveringText",
            remap = false)
    private void onDrawHoveringText(CallbackInfo ci, @Local(ordinal = 3) int x, @Local(ordinal = 4) int y,
            @Local(ordinal = 2) int w, @Local(ordinal = 5) int h) {
        TooltipOverlayHandler.toolTipX = x;
        TooltipOverlayHandler.toolTipY = y;
        TooltipOverlayHandler.toolTipW = w;
        TooltipOverlayHandler.toolTipH = h;
    }
}
