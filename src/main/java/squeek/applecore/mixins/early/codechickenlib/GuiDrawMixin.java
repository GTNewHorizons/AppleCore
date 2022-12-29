package squeek.applecore.mixins.early.codechickenlib;

import codechicken.lib.gui.GuiDraw;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.applecore.client.TooltipOverlayHandler;

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
