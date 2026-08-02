package fr.lostdev.talkarea.network;

import fr.lostdev.talkarea.TalkArea;
import fr.lostdev.talkarea.network.fromServer.TalkAreaDataChangedMessage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = TalkArea.MODID)
public class NetworkManager {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                TalkAreaDataChangedMessage.TYPE,
                TalkAreaDataChangedMessage.STREAM_CODEC,
                TalkAreaDataChangedMessage::handler
        );
    }
}
