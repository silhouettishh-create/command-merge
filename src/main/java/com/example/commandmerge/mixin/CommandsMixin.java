package com.example.commandmerge.mixin;

import com.example.commandmerge.CommandChainProcessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Commands.class)
public class CommandsMixin {

	@Inject(method = "performPrefixedCommand", at = @At("HEAD"), cancellable = true)
	private void commandmerge$onPerformPrefixedCommand(CommandSourceStack source, String command,
														CallbackInfoReturnable<Integer> cir) {
		String stripped = command.startsWith("/") ? command.substring(1) : command;

		if (!CommandChainProcessor.needsProcessing(stripped)) {
			return;
		}

		CommandChainProcessor.process((Commands) (Object) this, source, stripped);

		cir.setReturnValue(1);
		cir.cancel();
	}
}
