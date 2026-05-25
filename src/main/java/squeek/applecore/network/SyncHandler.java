package squeek.applecore.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import squeek.applecore.ModConfig;
import squeek.applecore.ModInfo;
import squeek.applecore.api.AppleCoreAPI;

public class SyncHandler {

    public static final SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MODID);

    public static void init() {
        channel.registerMessage(MessageExhaustionSync.class, MessageExhaustionSync.class, 0, Side.CLIENT);
        channel.registerMessage(MessageSaturationSync.class, MessageSaturationSync.class, 1, Side.CLIENT);

        SyncHandler syncHandler = new SyncHandler();
        FMLCommonHandler.instance().bus().register(syncHandler);
        MinecraftForge.EVENT_BUS.register(syncHandler);
    }

    /*
     * Sync saturation (vanilla MC only syncs when it hits 0) Sync exhaustion (vanilla MC does not sync it at all) Sync
     * difficulty (vanilla MC does not sync it on servers)
     */
    private static final Map<UUID, Float> lastSaturationLevels = new HashMap<UUID, Float>();
    private static final Map<UUID, Float> lastExhaustionLevels = new HashMap<UUID, Float>();

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingUpdateEvent event) {
        if (!(event.entity instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.entity;
        Float lastSaturationLevel = lastSaturationLevels.get(player.getUniqueID());
        Float lastExhaustionLevel = lastExhaustionLevels.get(player.getUniqueID());

        if (lastSaturationLevel == null || lastSaturationLevel != player.getFoodStats().getSaturationLevel()) {
            channel.sendTo(new MessageSaturationSync(player.getFoodStats().getSaturationLevel()), player);
            lastSaturationLevels.put(player.getUniqueID(), player.getFoodStats().getSaturationLevel());
        }

        float exhaustionLevel = AppleCoreAPI.accessor.getExhaustion(player);
        if (lastExhaustionLevel == null
                || Math.abs(lastExhaustionLevel - exhaustionLevel) >= ModConfig.EXHAUSTION_SYNC_THRESHOLD) {
            channel.sendTo(new MessageExhaustionSync(exhaustionLevel), player);
            lastExhaustionLevels.put(player.getUniqueID(), exhaustionLevel);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;

        lastSaturationLevels.remove(event.player.getUniqueID());
        lastExhaustionLevels.remove(event.player.getUniqueID());
    }
}
