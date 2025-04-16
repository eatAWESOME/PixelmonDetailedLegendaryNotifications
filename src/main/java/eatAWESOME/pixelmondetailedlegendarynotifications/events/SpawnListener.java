package eatAWESOME.pixelmondetailedlegendarynotifications.events;

//import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;;

public class SpawnListener {

	/*
	@SubscribeEvent
	public void onLegendarySpawn(LegendarySpawnEvent.DoSpawn event) {
		Entity entity = event.action.getOrCreateEntity();
		if (entity instanceof PixelmonEntity) {
			PixelmonEntity pixelmonEntity = (PixelmonEntity) entity;
			Pokemon pokemon = pixelmonEntity.getPokemon();
			Mutable position = event.action.spawnLocation.location.pos;
			send_messages(pokemon, position);
		}
	}
	*/
	
	@SubscribeEvent
	public void onSpawn(SpawnEvent event) {
		Entity entity = event.action.getOrCreateEntity();
		if (entity instanceof PixelmonEntity) {
			PixelmonEntity pixelmonEntity = (PixelmonEntity) entity;
			Pokemon pokemon = pixelmonEntity.getPokemon();
			if (pokemon.isShiny() || pokemon.isLegendary() || pokemon.isMythical() || pokemon.isUltraBeast()) {
				Mutable position = event.action.spawnLocation.location.pos;
				send_messages(pokemon, position);
			}
		}
	}
	
	public void send_messages(Pokemon pokemon, Mutable position) {
		Species species = pokemon.getSpecies();
		ServerPlayerEntity closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;
		
		for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            double distance = player.distanceToSqr(position.getX(), position.getY(), position.getZ());
            
            // If this player is closer, update closest player
            if (distance < closestDistance) {
                closestPlayer = player;
                closestDistance = distance;
            }
        }
		
		TextFormatting textFormatting = null;
		if (species.isLegendary()) {
			textFormatting = TextFormatting.GREEN;
		} else if (species.isMythical()) {
			textFormatting = TextFormatting.LIGHT_PURPLE;
		} else if (species.isUltraBeast()) {
			textFormatting = TextFormatting.DARK_GRAY;
		} else if (pokemon.isShiny()) {
			textFormatting = TextFormatting.GOLD;
		}
		IFormattableTextComponent formattedPrefix = null;
		if (pokemon.isShiny()) {
			formattedPrefix = new StringTextComponent("A shiny " + species.getTranslatedName().getString()).withStyle(TextFormatting.GOLD);
		} else {
			char firstChar = species.getTranslatedName().getString().toLowerCase().charAt(0);
			if (firstChar == 'a' || firstChar == 'e' || firstChar == 'i' || firstChar == 'o' || firstChar == 'u' || firstChar == 'y') {
				formattedPrefix = new StringTextComponent("An " + species.getTranslatedName().getString()).withStyle(textFormatting);
			} else {
				formattedPrefix = new StringTextComponent("A " + species.getTranslatedName().getString()).withStyle(textFormatting);
			}
		}
		
		IFormattableTextComponent nullMessage = formattedPrefix.copy();
		IFormattableTextComponent closeMessage = formattedPrefix.copy();
		IFormattableTextComponent otherMessage = formattedPrefix.copy();
		nullMessage.append(new StringTextComponent(" spawned at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + "!").withStyle(textFormatting));
		closeMessage.append(new StringTextComponent(" spawned near you at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + "!").withStyle(textFormatting));
		otherMessage.append(new StringTextComponent(" spawned near " + closestPlayer.getDisplayName().getString() + "!").withStyle(textFormatting));
		
		for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
			if (closestPlayer == null) {
				player.sendMessage(nullMessage, player.getUUID());
			} else if (player.equals(closestPlayer)) {
	            player.sendMessage(closeMessage, player.getUUID());		
			} else {
	            player.sendMessage(otherMessage, player.getUUID());
			}
        }
	}
}