package com.example.commandmerge.mixin;

import com.example.commandmerge.CommandHighlightHelper;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandSuggestions.class)
public class CommandSuggestionsMixin {

    @Inject(method = "formatChat", at = @At("RETURN"), cancellable = true)
    private void commandmerge$highlight(String text, int cursor,
                                         CallbackInfoReturnable<FormattedCharSequence> cir) {
        // Only process if MERGE or WAIT_TICK is in the command
        if (text.contains(" MERGE ") || text.contains("WAIT_TICK:")) {
            FormattedCharSequence highlighted = CommandHighlightHelper.highlight(text);
            if (highlighted != null) {
                cir.setReturnValue(highlighted);
            }
        }
    }
}
