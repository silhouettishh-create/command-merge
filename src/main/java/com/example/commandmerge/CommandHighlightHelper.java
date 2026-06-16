package com.example.commandmerge;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommandHighlightHelper {

    private static final Pattern MERGE_TOKEN = Pattern.compile("\\bMERGE\\b");
    private static final Pattern WAIT_TICK_TOKEN = Pattern.compile("WAIT_TICK:\\d+");
    private static final Style GOLD = Style.EMPTY.withColor(ChatFormatting.GOLD);

    private CommandHighlightHelper() {}

    public static FormattedCharSequence highlight(String text) {
        List<int[]> tokens = new ArrayList<>();
        collect(MERGE_TOKEN, text, tokens);
        collect(WAIT_TICK_TOKEN, text, tokens);
        if (tokens.isEmpty()) return null;

        tokens.sort(Comparator.comparingInt(a -> a[0]));

        MutableComponent result = Component.empty();
        int pos = 0;
        for (int[] token : tokens) {
            if (pos < token[0]) {
                result.append(Component.literal(text.substring(pos, token[0])));
            }
            result.append(Component.literal(text.substring(token[0], token[1])).withStyle(GOLD));
            pos = token[1];
        }
        if (pos < text.length()) {
            result.append(Component.literal(text.substring(pos)));
        }
        return result.getVisualOrderText();
    }

    private static void collect(Pattern pattern, String text, List<int[]> out) {
        Matcher m = pattern.matcher(text);
        while (m.find()) out.add(new int[]{m.start(), m.end()});
    }
}
