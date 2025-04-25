package eatAWESOME.pixelmonspawnalerts.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.Commands;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;

public class SpawnAlertTP {

	public SpawnAlertTP() {
    }
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("spawnalerttp")
			.then(Commands.argument("x", IntegerArgumentType.integer())
	        .then(Commands.argument("y", IntegerArgumentType.integer())
	        .then(Commands.argument("z", IntegerArgumentType.integer())
	            .executes(context -> {
	                ServerPlayerEntity player = context.getSource().getPlayerOrException();
	                int x = IntegerArgumentType.getInteger(context, "x");
	                int y = IntegerArgumentType.getInteger(context, "y");
	                int z = IntegerArgumentType.getInteger(context, "z");
	                
	                player.teleportTo(x + 0.5, y, z + 0.5);
	                
	                return Command.SINGLE_SUCCESS;
	            })))));
	}
}