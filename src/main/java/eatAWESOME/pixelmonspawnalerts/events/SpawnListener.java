package eatAWESOME.pixelmonspawnalerts.events;

import com.pixelmonmod.pixelmon.api.events.spawning.PixelmonSpawnerEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import eatAWESOME.pixelmonspawnalerts.PixelmonSpawnAlerts;
import eatAWESOME.pixelmonspawnalerts.utils.SpawnAlertTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;;

public class SpawnListener {
	
	@SubscribeEvent
	public void onSpawn(SpawnEvent event) {
		Entity entity = event.action.getOrCreateEntity();
		if (entity instanceof PixelmonEntity) {
			PixelmonEntity pixelmonEntity = (PixelmonEntity) entity;
			Mutable position = event.action.spawnLocation.location.pos;
			processSpawn(pixelmonEntity, position);
		}
	}
	
	@SubscribeEvent
	public void onSpawnerSpawn(PixelmonSpawnerEvent.Post event) {
		PixelmonEntity pixelmonEntity = event.getEntity();
		Mutable position = event.getSpawnPosition().mutable();
		processSpawn(pixelmonEntity, position);
	}
	
	public void processSpawn(PixelmonEntity pixelmonEntity, Mutable position) {
		if (!pixelmonEntity.isBossPokemon()) {
			Pokemon pokemon = pixelmonEntity.getPokemon();
			String speciesName = pokemon.getSpecies().getStrippedName();
			if (SpawnAlertTracker.getAllSpawnAlerts().values().stream().anyMatch(targetName -> targetName.equals(speciesName))) {
				ServerPlayerEntity closestPlayer = getClosestPlayer(position);
				if (SpawnAlertTracker.getSpawnAlert(closestPlayer.getUUID()).equals(speciesName)) {
					sendAlerts(pokemon, position, closestPlayer);
				}
			}
			if (pokemon.isShiny() || pokemon.isLegendary() || pokemon.isMythical() || pokemon.isUltraBeast()) {
					sendMessages(pokemon, position, getClosestPlayer(position));
			}
		}
	}
	
	public void sendMessages(Pokemon pokemon, Mutable position, ServerPlayerEntity closestPlayer) {
		TextFormatting textFormatting = getTextFormatting(pokemon);
		IFormattableTextComponent formattedPrefix = getPrefix(pokemon, textFormatting);
		
		String coordsMessage = position.getX() + ", " + position.getY() + ", " + position.getZ();
		String coordsCommand = "/spawnalerttp " + position.getX() + " " + position.getY() + " " + position.getZ();
		Style coordsCommandStyle = new StringTextComponent("").withStyle(textFormatting).withStyle(TextFormatting.UNDERLINE).getStyle()
				.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, coordsCommand))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Teleport to " + coordsMessage + "!").withStyle(textFormatting)));
		
		IFormattableTextComponent nullMessage = formattedPrefix.copy();
		IFormattableTextComponent closeMessage = formattedPrefix.copy();
		IFormattableTextComponent otherMessage = formattedPrefix.copy();
		nullMessage.append(new StringTextComponent(" spawned at ").withStyle(textFormatting))
				.append(new StringTextComponent(coordsMessage).withStyle(coordsCommandStyle))
				.append(new StringTextComponent("!").withStyle(textFormatting));
		closeMessage.append(new StringTextComponent(" spawned near you at ").withStyle(textFormatting))
				.append(new StringTextComponent(coordsMessage).withStyle(coordsCommandStyle))
				.append(new StringTextComponent("!").withStyle(textFormatting));
		otherMessage.append(new StringTextComponent(" spawned near " + closestPlayer.getName().getString() + "!").withStyle(textFormatting));
		
		for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
			if (closestPlayer == null) {
				player.sendMessage(nullMessage, player.getUUID());
				sendSound(player, pokemon);
			} else if (player.equals(closestPlayer)) {
				player.sendMessage(closeMessage, player.getUUID());
	            sendSound(player, pokemon);
			} else {
	            player.sendMessage(otherMessage, player.getUUID());
			}
        }
		PixelmonSpawnAlerts.LOGGER.info("[PixelmonSpawnAlerts]: " + nullMessage.getString().replace("at", "near " + closestPlayer.getName().getString() + " at"));
	}
	
	public void sendAlerts(Pokemon pokemon, Mutable position, ServerPlayerEntity player) {
		TextFormatting textFormatting = getTextFormatting(pokemon);
		
		String coordsMessage = position.getX() + ", " + position.getY() + ", " + position.getZ();
		String coordsCommand = "/spawnalerttp " + position.getX() + " " + position.getY() + " " + position.getZ();
		Style coordsCommandStyle = new StringTextComponent("").withStyle(textFormatting).withStyle(TextFormatting.UNDERLINE).getStyle()
				.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, coordsCommand))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Teleport to " + coordsMessage + "!").withStyle(textFormatting)));
		
		IFormattableTextComponent closeMessage = getPrefix(pokemon, textFormatting);
		closeMessage.append(new StringTextComponent(" spawned near you at ").withStyle(textFormatting))
				.append(new StringTextComponent(coordsMessage).withStyle(coordsCommandStyle))
				.append(new StringTextComponent("!").withStyle(textFormatting));
		
		PixelmonSpawnAlerts.LOGGER.info("[PixelmonSpawnAlerts]: " + closeMessage.getString().replace("you", player.getName().getString()));
		player.sendMessage(closeMessage, player.getUUID());
		sendSound(player, pokemon);
	}
	
	public ServerPlayerEntity getClosestPlayer(Mutable position) {
		ServerPlayerEntity closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;
		for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            double distance = player.distanceToSqr(position.getX(), position.getY(), position.getZ());
            if (distance < closestDistance) {
                closestPlayer = player;
                closestDistance = distance;
            }
        }
		return closestPlayer;
	}
	
	public TextFormatting getTextFormatting(Pokemon pokemon) {
		Species species = pokemon.getSpecies();
		TextFormatting textFormatting = TextFormatting.WHITE;
		if (species.isLegendary()) {
			textFormatting = TextFormatting.GREEN;
		} else if (species.isMythical()) {
			textFormatting = TextFormatting.LIGHT_PURPLE;
		} else if (species.isUltraBeast()) {
			textFormatting = TextFormatting.DARK_GRAY;
		} else if (pokemon.isShiny()) {
			textFormatting = TextFormatting.GOLD;
		}
		return textFormatting;
	}
	
	public IFormattableTextComponent getPrefix(Pokemon pokemon, TextFormatting textFormatting) {
		Species species = pokemon.getSpecies();
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
		return formattedPrefix;
	}
	
	public void sendSound(ServerPlayerEntity player, Pokemon pokemon) {
		Species species = pokemon.getSpecies();
		SoundEvent sound = SoundEvents.FIREWORK_ROCKET_LAUNCH;
		if (species.isLegendary()) {
			sound = SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;
		} else if (species.isMythical()) {
			sound = SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;
		} else if (species.isUltraBeast()) {
			sound = SoundEvents.ENDER_DRAGON_GROWL;
		} else if (pokemon.isShiny()) {
			sound = SoundEvents.PLAYER_LEVELUP;
		}
		player.playSound(sound, 2.0F, 1.0F);
		player.connection.send(new SPlaySoundEffectPacket(sound, SoundCategory.PLAYERS, player.getX(), player.getY(), player.getZ(), 2.0F, 1.0F));
	}
}