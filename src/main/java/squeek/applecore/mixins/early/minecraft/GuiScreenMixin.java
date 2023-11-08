package squeek.applecore.mixins.early.minecraft;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import squeek.applecore.client.TooltipOverlayHandler;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {

    @Inject(
            method = "drawHoveringText",
            at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", shift = At.Shift.BEFORE),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/GuiScreen;drawGradientRect(IIIIII)V",
                            ordinal = 0)),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void onDrawHoveringText(List textLines, int x, int y, FontRenderer font, CallbackInfo ci, int k,
            Iterator iterator, int j2, int k2, int i1, int j1, int k1, int l1, int i2) {
        TooltipOverlayHandler.toolTipX = j2;
        TooltipOverlayHandler.toolTipY = k2;
        TooltipOverlayHandler.toolTipW = k;
        TooltipOverlayHandler.toolTipH = i1;
    }
}
