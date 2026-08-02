package fr.lostdev.talkarea.event;

import fr.lostdev.talkarea.ChatTypeList;
import fr.lostdev.talkarea.FontList;
import fr.lostdev.talkarea.TalkArea;
import fr.lostdev.talkarea.data.TalkAreaData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;

import java.util.UUID;

@EventBusSubscriber(modid = TalkArea.MODID, value = Dist.CLIENT)
public class ClientEvent {

    /**
     * Intercept the message received by the player if they have TalkArea enabled, and cancel it if they are too far away from the sender.
     * Also, if the message is impacted by talkarea, update the font and apply a tooltip to the message.
     */
    @SubscribeEvent
    public static void onMessageReceived(ClientChatReceivedEvent event){
        UUID senderUUID = event.getSender();
        assert Minecraft.getInstance().level != null;

        //we don't apply talkarea on /msg
        if (event.getBoundChatType() != null && (event.getBoundChatType().chatType().is(ChatType.MSG_COMMAND_INCOMING) || event.getBoundChatType().chatType().is(ChatType.MSG_COMMAND_OUTGOING))) {
            return;
        }

        Player sender = Minecraft.getInstance().level.getPlayerByUUID(senderUUID);
        Player receiver = Minecraft.getInstance().player;

        if (sender == null || receiver == null){
            return;
        }

        //trigger only on chat and emote message, and it's talkarea equivalent
        if (event.getBoundChatType() != null
                && (event.getBoundChatType().chatType().is(ChatType.CHAT) || event.getBoundChatType().chatType().is(ChatType.EMOTE_COMMAND)
                || event.getBoundChatType().chatType().is(ChatTypeList.TALKAREA_CHAT_TYPE) || event.getBoundChatType().chatType().is(ChatTypeList.TALKAREA_EMOTE_CHAT_TYPE))) {

            //if talkarea is toggle, we cancel the message if the receiver is too far away from the sender
            if (receiver.getData(TalkAreaData.TALKAREA_LISTEN_TOGGLE)) {
                int distance = receiver.getData(TalkAreaData.TALKAREA_DISTANCE);
                double distanceSquared = distance * distance;
                double actualDistanceSquared = sender.distanceToSqr(receiver);
                if (actualDistanceSquared > distanceSquared) {
                    event.setCanceled(true);
                    return;
                }
                //if the message is not a talkarea message, we add the icon at the start of the message and apply the tooltip
                else if (!event.getBoundChatType().chatType().is(ChatTypeList.TALKAREA_CHAT_TYPE) && !event.getBoundChatType().chatType().is(ChatTypeList.TALKAREA_EMOTE_CHAT_TYPE)) {
                    Component iconComponent = Component.literal("\uF351 ").withStyle(Style.EMPTY.withFont(FontList.FONT_ICONS));

                    Component tooltipText = Component.translatable("talkarea.chat.tooltip.talkarea", sender.getDisplayName(), sender.distanceTo(receiver)).withStyle(ChatFormatting.GRAY);
                    Style talkAreaStyle = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltipText));

                    Component modifiedMessage = Component.empty().append(iconComponent).append(event.getMessage()).withStyle(talkAreaStyle);
                    event.setMessage(modifiedMessage);
                    return;
                }
            }

            //if the receiver has talkarea listen toggle on, or if the message is a talkarea message, we apply the tooltip to the message
            if (event.getBoundChatType().chatType().is(ChatTypeList.TALKAREA_CHAT_TYPE) || event.getBoundChatType().chatType().is(ChatTypeList.TALKAREA_EMOTE_CHAT_TYPE)) {
                Component tooltipText = Component.translatable("talkarea.chat.tooltip.talkarea", sender.getDisplayName(), (int) sender.distanceTo(receiver)).withStyle(ChatFormatting.GRAY);
                Style talkAreaStyle = Style.EMPTY.withFont(FontList.FONT_ICONS).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltipText));

                event.setMessage(event.getMessage().copy().withStyle(talkAreaStyle));
            }
        }
    }
}
