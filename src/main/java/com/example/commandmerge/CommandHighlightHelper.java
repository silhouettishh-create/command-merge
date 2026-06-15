package com.example.commandmerge;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommandHighlightHelper {

	private static final Pattern MERGE_TOKEN = Pattern.compile("MERGE");
	private static final Pattern WAIT_TICK_TOKEN = Pattern.compile("WAIT_TICK:\\d+");

	private CommandHighlightHelper() {
	}

	public static FormattedCharSequence highlight(String text, FormattedCharSequence original) {
		List<int[]> ranges = new ArrayList<>();
		collect(MERGE_TOKEN, text, ranges);
		collect(WAIT_TICK_TOKEN, text, ranges);

		if (ranges.isEmpty()) {
			return null;
		}

		return sink -> original.accept((index, style, codePoint) -> {
			Style effective = style;
			for (int[] range : ranges) {
				if (index >= range[0] && index < range[1]) {
					effective = style.withColor(ChatFormatting.GOLD);
					break;
				}
			}
			return sink.accept(index, effective, codePoint);
		});
	}

	private static void collect(Pattern pattern, String text, List<int[]> ranges) {
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			ranges.add(new int[]{matcher.start(), matcher.end()});
		}
	}
}
