package eatAWESOME.pixelmonspawnalerts.events;

import com.pixelmonmod.pixelmon.api.events.raids.RandomizeRaidEvent;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;

import eatAWESOME.pixelmonspawnalerts.utils.SpawnAlertTracker;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;;

public class RaidListener {

    @SubscribeEvent
    public void onRaidRandomize (RandomizeRaidEvent.ChooseSpecies event) {
    	Species species = event.getRaid().getSpecies();
    	String speciesName = species.getStrippedName();
    	Mutable position = event.den.blockPosition().mutable();
    	if (SpawnAlertTracker.getAllSpawnAlerts().values().stream().anyMatch(targetName -> targetName.equals(speciesName))) {
			ServerPlayerEntity closestPlayer = getClosestPlayer(position);
			if (SpawnAlertTracker.getSpawnAlert(closestPlayer.getUUID()).equals(speciesName)) {
				sendAlerts(species, position, closestPlayer);
			}
		}
    	if (species.isLegendary() || species.isMythical() || species.isUltraBeast()) {
	    	sendMessages(species, position, getClosestPlayer(position));
        }
    }
    
    public void sendMessages(Species species, Mutable position, ServerPlayerEntity closestPlayer) {
    	TextFormatting textFormatting = getTextFormatting(species);
		String prefix = getPrefix(species);
    	IFormattableTextComponent nullMessage = new StringTextComponent(prefix + species.getTranslatedName().getString() + " raid appeared at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + "!").withStyle(textFormatting);
		IFormattableTextComponent closeMessage = new StringTextComponent(prefix + species.getTranslatedName().getString() + " raid appeared near you at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + "!").withStyle(textFormatting);
		IFormattableTextComponent otherMessage = new StringTextComponent(prefix + species.getTranslatedName().getString() + " raid appeared near " + closestPlayer.getDisplayName().getString() + " at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + "!").withStyle(textFormatting);
		
		for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
			if (closestPlayer == null) {
				player.sendMessage(nullMessage, player.getUUID());
				sendSound(player, species);
			} else if (player.equals(closestPlayer)) {
	            player.sendMessage(closeMessage, player.getUUID());
	            sendSound(player, species);
			} else {
	            player.sendMessage(otherMessage, player.getUUID());
			}
    	}
	}
    
    public void sendAlerts(Species species, Mutable position, ServerPlayerEntity player) {
		TextFormatting textFormatting = getTextFormatting(species);
		String prefix = getPrefix(species);
		IFormattableTextComponent closeMessage = new StringTextComponent(prefix + species.getTranslatedName().getString() + " raid appeared near you at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + "!").withStyle(textFormatting);
		
		player.sendMessage(closeMessage, player.getUUID());
		sendSound(player, species);
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
    
    public TextFormatting getTextFormatting(Species species) {
    	TextFormatting textFormatting = TextFormatting.WHITE;
		if (species.isLegendary()) {
			textFormatting = TextFormatting.GREEN;
		} else if (species.isMythical()) {
			textFormatting = TextFormatting.LIGHT_PURPLE;
		} else if (species.isUltraBeast()) {
			textFormatting = TextFormatting.DARK_GRAY;
		}
		return textFormatting;
	}
	
	public String getPrefix(Species species) {
		String prefix = "";
		char firstChar = species.getTranslatedName().getString().toLowerCase().charAt(0);
		if (firstChar == 'a' || firstChar == 'e' || firstChar == 'i' || firstChar == 'o' || firstChar == 'u' || firstChar == 'y') {
			prefix = "An ";
		} else {
			prefix = "A ";
		}
		return prefix;
	}
	
	public void sendSound(ServerPlayerEntity player, Species species) {
		SoundEvent sound = SoundEvents.FIREWORK_ROCKET_LAUNCH;
		if (species.isLegendary()) {
			sound = SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;
		} else if (species.isMythical()) {
			sound = SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;
		} else if (species.isUltraBeast()) {
			sound = SoundEvents.ENDER_DRAGON_GROWL;
		}
		player.playSound(sound, 2.0F, 1.0F);
		player.connection.send(new SPlaySoundEffectPacket(sound, SoundCategory.PLAYERS, player.getX(), player.getY(), player.getZ(), 2.0F, 1.0F));
	}
}