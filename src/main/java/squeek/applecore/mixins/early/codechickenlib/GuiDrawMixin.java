package squeek.applecore.mixins.early.codechickenlib;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import squeek.applecore.client.TooltipOverlayHandler;
import codechicken.lib.gui.GuiDraw;

@Mixin(GuiDraw.class)
public class GuiDrawMixin {

    @Inject(method = "drawTooltipBox", at = @At("HEAD"), remap = false)
    private static void onDrawTooltipBox(int x, int y, int w, int h, CallbackInfo callbackInfo) {
        TooltipOverlayHandler.toolTipX = x;
        TooltipOverlayHandler.toolTipY = y;
        TooltipOverlayHandler.toolTipW = w;
        TooltipOverlayHandler.toolTipH = h;
    }
}
