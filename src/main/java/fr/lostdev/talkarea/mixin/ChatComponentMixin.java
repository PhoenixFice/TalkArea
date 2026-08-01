package fr.lostdev.talkarea.mixin;

import fr.lostdev.talkarea.ChatIconRenderHelper;
import fr.lostdev.talkarea.ChatIconRenderHelper.SplitResult;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"
            )
    )
    private int talkarea$drawStringWithoutShadowOnIcon(GuiGraphics guiGraphics, Font font, FormattedCharSequence text,
                                                       int x, int y, int color) {

        // Toute la logique (cache + calcul) est déléguée à une classe
        // normale hors du package mixin.
        SplitResult result = ChatIconRenderHelper.getOrCompute(text);

        if (!result.hasIcon()) {
            return guiGraphics.drawString(font, text, x, y, color);
        }

        // On dessine chaque segment à la suite, en avançant x à chaque fois.
        // drawString renvoie la position x suivante, ce qui permet de chaîner
        // les segments sans recalculer manuellement leur largeur.
        int currentX = x;
        int lastX = currentX;

        for (SplitResult.Segment segment : result.segments()) {
            boolean withShadow = !segment.icon();
            lastX = guiGraphics.drawString(font, segment.sequence(), currentX, y, color, withShadow);
            currentX = lastX;
        }

        return lastX;
    }
}