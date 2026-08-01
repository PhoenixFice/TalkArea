package fr.lostdev.talkarea;

import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to split a FormattedCharSequence into segments of icons and text.
 * This is used to render the chat line with icons without shadow, while keeping
 * the text with shadow. The result is cached for performance.
 */
public final class ChatIconRenderHelper {

    private static final int TALKAREA_ICON_CODEPOINT = 0xF351;

    private static final int MAX_CACHE_SIZE = 200;
    private static final Map<FormattedCharSequence, SplitResult> CACHE =
            new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<FormattedCharSequence, SplitResult> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            };

    public static SplitResult getOrCompute(FormattedCharSequence text) {
        SplitResult result = CACHE.get(text);
        if (result == null) {
            result = computeSplit(text);
            CACHE.put(text, result);
        }
        return result;
    }

    private static SplitResult computeSplit(FormattedCharSequence text) {
        //for each caractere, we say if it's a icon
        List<Boolean> iconFlags = new ArrayList<>();
        text.accept((index, style, codePoint) -> {
            iconFlags.add(codePoint == TALKAREA_ICON_CODEPOINT);
            return true;
        });

        //if message is empty, we return a empty answer
        int totalLength = iconFlags.size();
        if (totalLength == 0) {
            return new SplitResult(List.of());
        }

        boolean anyIcon = false;
        for (boolean flag : iconFlags) {
            if (flag) {
                anyIcon = true;
                break;
            }
        }

        //if there are no icon, we just return the original text as a single segment
        if (!anyIcon) {
            return new SplitResult(List.of(new SplitResult.Segment(text, false)));
        }

        //We split the text into segments, each segment is either a block of icons or a block of normal text
        List<SplitResult.Segment> segments = new ArrayList<>();
        int start = 0;
        boolean previousFlag = iconFlags.getFirst();
        for (int i = 1; i < totalLength; i++) {
            boolean currentFlag = iconFlags.get(i);
            if (currentFlag != previousFlag) {
                segments.add(new SplitResult.Segment(subSequence(text, start, i), previousFlag));
                start = i;
                previousFlag = currentFlag;
            }
        }
        segments.add(new SplitResult.Segment(subSequence(text, start, totalLength), previousFlag));

        return new SplitResult(segments);
    }

    /**
     * Returns a sub-sequence of the given FormattedCharSequence, from start (inclusive) to end (exclusive).
     * This is used to create segments of text for rendering.
     */
    private static FormattedCharSequence subSequence(FormattedCharSequence text, int start, int end) {
        return sink -> {
            int[] pos = {0};
            return text.accept((index, style, codePoint) -> {
                int current = pos[0]++;
                if (current < start) return true;   // pas encore arrivé au segment
                if (current >= end) return false;   // segment terminé, on arrête l'itération
                return sink.accept(current - start, style, codePoint);
            });
        };
    }

    /**
     * Result of splitting a FormattedCharSequence into segments of icons and text.
     * Each segment is either a block of icons (without shadow) or a block of normal text (with shadow).
     */
    public record SplitResult(List<Segment> segments) {

        public boolean hasIcon() {
            for (Segment segment : segments) {
                if (segment.icon()) return true;
            }
            return false;
        }

        /**
         * A segment of a FormattedCharSequence, either a block of icons or a block of normal text.
         * The icon flag indicates whether the segment is a block of icons (true) or normal text (false).
         */
        public record Segment(FormattedCharSequence sequence, boolean icon) {}
    }
}