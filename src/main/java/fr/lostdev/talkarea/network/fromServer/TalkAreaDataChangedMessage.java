package fr.lostdev.talkarea.network.fromServer;

import fr.lostdev.talkarea.TalkArea;
import fr.lostdev.talkarea.data.TalkAreaData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TalkAreaDataChangedMessage(boolean talkareaToggle, int distance, boolean talkareaListenToggle) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TalkAreaDataChangedMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TalkArea.MODID, "talk_area_data_changed"));

    public static final StreamCodec<ByteBuf, TalkAreaDataChangedMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, TalkAreaDataChangedMessage::talkareaToggle,
            ByteBufCodecs.VAR_INT, TalkAreaDataChangedMessage::distance,
            ByteBufCodecs.BOOL, TalkAreaDataChangedMessage::talkareaListenToggle,
            TalkAreaDataChangedMessage::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handler(final TalkAreaDataChangedMessage data, final IPayloadContext context){
        context.player().setData(TalkAreaData.TALKAREA_TOGGLE, data.talkareaToggle());
        context.player().setData(TalkAreaData.TALKAREA_DISTANCE, data.distance());
        context.player().setData(TalkAreaData.TALKAREA_LISTEN_TOGGLE, data.talkareaListenToggle());
    }
}
