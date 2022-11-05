package squeek.applecore.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class NetworkHelper {
    public static EntityPlayer getSidedPlayer(MessageContext ctx) {
        return ctx.side == Side.SERVER
                ? ctx.getServerHandler().playerEntity
                : FMLClientHandler.instance().getClientPlayerEntity();
    }
}
