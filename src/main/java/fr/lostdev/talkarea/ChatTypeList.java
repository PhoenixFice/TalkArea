package fr.lostdev.talkarea;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ChatTypeList {

    public static final ResourceKey<ChatType> TALKAREA_CHAT_TYPE = ResourceKey.create(
            Registries.CHAT_TYPE,
            ResourceLocation.fromNamespaceAndPath(TalkArea.MODID, "talkarea_chat_type")
    );

    public static final ResourceKey<ChatType> TALKAREA_EMOTE_CHAT_TYPE = ResourceKey.create(
            Registries.CHAT_TYPE,
            ResourceLocation.fromNamespaceAndPath(TalkArea.MODID, "talkarea_emote_chat_type")
    );
}
