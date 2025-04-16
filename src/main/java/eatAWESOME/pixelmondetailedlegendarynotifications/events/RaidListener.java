package eatAWESOME.pixelmondetailedlegendarynotifications.events;

import com.pixelmonmod.pixelmon.api.events.raids.RandomizeRaidEvent;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;;

public class RaidListener {

    @SubscribeEvent
    public void onRaidRandomize (RandomizeRaidEvent.ChooseSpecies event) {
    	Species species = event.getRaid().getSpecies();
    	if (species.isLegendary() || species.isMythical() || species.isUltraBeast()) {
	    	BlockPos position = event.den.blockPosition();
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
			}
			String prefix = "";
			char firstChar = species.getTranslatedName().getString().toLowerCase().charAt(0);
			if (firstChar == 'a' || firstChar == 'e' || firstChar == 'i' || firstChar == 'o' || firstChar == 'u' || firstChar == 'y') {
				prefix = "An ";
			} else {
				prefix = "A ";
			}
			IFormattableTextComponent nullMessage = new StringTextComponent(prefix + species.getTranslatedName().getString() + " raid appeared at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + "!").withStyle(textFormatting);
			IFormattableTextComponent closeMessage = new StringTextComponent(prefix + species.getTranslatedName().getString() + " raid appeared near you at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + "!").withStyle(textFormatting);
			IFormattableTextComponent otherMessage = new StringTextComponent(prefix + species.getTranslatedName().getString() + " raid appeared near " + closestPlayer.getDisplayName().getString() + " at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + "!").withStyle(textFormatting);
			
			
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
}