package com.example.commandmerge.mixin;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(SharedSuggestionProvider.class)
public class SuggestionsMixin {

    @Inject(method = "suggest", at = @At("RETURN"), cancellable = true)
    private static void commandmerge$addMergeSuggestions(
            SuggestionsBuilder builder,
            CallbackInfoReturnable<CompletableFuture<Suggestions>> cir) {
        
        String remaining = builder.getRemaining();
        
        if (remaining.endsWith(" ") || remaining.isEmpty()) {
            builder.suggest("MERGE");
            builder.suggest("WAIT_TICK:");
        }
        
        cir.setReturnValue(builder.buildFuture());
    }
}
