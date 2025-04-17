package eatAWESOME.pixelmonspawnalerts.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pixelmonmod.pixelmon.api.pokemon.species.Pokedex;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;

import eatAWESOME.pixelmonspawnalerts.capabilities.ISpawnAlertData;
import eatAWESOME.pixelmonspawnalerts.capabilities.SpawnAlertDataProvider;
import eatAWESOME.pixelmonspawnalerts.utils.SpawnAlertTracker;
import net.minecraft.command.Commands;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import java.util.ArrayList;
import java.util.List;

public class SpawnAlert {

	public SpawnAlert() {
    }
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("spawnalert")
	            .executes(context -> {
	            	ServerPlayerEntity player = context.getSource().getPlayerOrException();
	            	LazyOptional<ISpawnAlertData> cap = player.getCapability(SpawnAlertDataProvider.spawnAlertLocation);
	                cap.ifPresent(data -> {
	                	String oldTargetName = data.getSpawnAlert();
	                	if (oldTargetName.equals("disable")) {
	                		context.getSource().sendSuccess(new StringTextComponent("No active spawn alerts."), false);
	                	} else {
	                		context.getSource().sendSuccess(new StringTextComponent("Current spawn alert: " + oldTargetName), false);
	                	}
	                });
	                return Command.SINGLE_SUCCESS;
	            })
            .then(Commands.argument("target", StringArgumentType.word())
        		.suggests((context, builder) -> {
                    List<String> suggestions = new ArrayList<>();
                    suggestions.add("disable");
                    for (Species pokedexSpecies : Pokedex.actualPokedex) {
                        suggestions.add(pokedexSpecies.getStrippedName());
                    }
                    return ISuggestionProvider.suggest(suggestions, builder);
                })
        		.executes(context -> {
            		ServerPlayerEntity player = context.getSource().getPlayerOrException();
	                String targetName = StringArgumentType.getString(context, "target");
	                
	                if (!targetName.equals("disable") && !validateSpecies(targetName)) {
	                	context.getSource().sendFailure(new StringTextComponent("Invalid target. Entry must be 'disable' or a pokémon species."));
	                } else {
	                	LazyOptional<ISpawnAlertData> cap = player.getCapability(SpawnAlertDataProvider.spawnAlertLocation);
		                cap.ifPresent(data -> {
		                	String oldTargetName = data.getSpawnAlert();
		                	data.setSpawnAlert(targetName);
		                	SpawnAlertTracker.updateSpawnAlert(player.getUUID(), targetName);
		                	
		                	if (targetName.equals("disable")) {
			                	if (oldTargetName.equals("disable")) {
			                		context.getSource().sendSuccess(new StringTextComponent("Spawn alerts already disabled."), false);
			                	} else {
				                	context.getSource().sendSuccess(new StringTextComponent("Spawn alerts disabled."), false);
			                	}
			                } else {
			                	if (oldTargetName.equals(targetName)) {
			                		context.getSource().sendSuccess(new StringTextComponent("Spawn alerts already enabled for " + targetName), false);
			                	} else if (oldTargetName.equals("disable")) {
			                		context.getSource().sendSuccess(new StringTextComponent("Spawn alerts enabled for " + targetName), false);
			                	} else {
			                		context.getSource().sendSuccess(new StringTextComponent("Spawn alerts disabled for " + oldTargetName), false);
			                		context.getSource().sendSuccess(new StringTextComponent("Spawn alerts enabled for " + targetName), false);
			                	}
			                }
		                });
		                if (!cap.isPresent()) {
		                	context.getSource().sendFailure(new StringTextComponent("Error: Missing Capability."));
		                }
	                }
	                return Command.SINGLE_SUCCESS;
            	})));
	}
	
	public static boolean validateSpecies(String targetName) {
		for (Species pokedexSpecies : Pokedex.actualPokedex) {
            if (pokedexSpecies.getStrippedName().equals(targetName)) {
            	return true;
            }
		}
        return false;
	}
}