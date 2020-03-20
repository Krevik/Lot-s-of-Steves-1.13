package krevik.github.io.networking;

import krevik.github.io.networking.messages.farmer.ClientOpenFarmerGui;
import krevik.github.io.networking.messages.farmer.ClientUpdateFarmerGeneralAllowedItems;
import krevik.github.io.networking.messages.farmer.ServerUpdateFarmerGeneralAllowedItems;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * thank you @williewillus
 */
public final class PacketsHandler
{
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation("pep", "channel_pep_"+PROTOCOL_VERSION))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void register()
    {
        int id = 0;
        HANDLER.registerMessage(id++, ClientOpenFarmerGui.class, ClientOpenFarmerGui::encode, ClientOpenFarmerGui::decode, ClientOpenFarmerGui.Handler::handle);
        HANDLER.registerMessage(id++, ServerUpdateFarmerGeneralAllowedItems.class, ServerUpdateFarmerGeneralAllowedItems::encode, ServerUpdateFarmerGeneralAllowedItems::decode, ServerUpdateFarmerGeneralAllowedItems.Handler::handle);
        HANDLER.registerMessage(id++, ClientUpdateFarmerGeneralAllowedItems.class, ClientUpdateFarmerGeneralAllowedItems::encode, ClientUpdateFarmerGeneralAllowedItems::decode, ClientUpdateFarmerGeneralAllowedItems.Handler::handle);

    }

    public static void sendToServer(Object msg)
    {
        HANDLER.sendToServer(msg);
    }

    public static void sendTo(Object msg, ServerPlayerEntity player)
    {
        if (!(player instanceof FakePlayer))
        {
            HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void sendToAll(Object msg){
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
        {
            sendTo(msg,player);
        }
    }
}