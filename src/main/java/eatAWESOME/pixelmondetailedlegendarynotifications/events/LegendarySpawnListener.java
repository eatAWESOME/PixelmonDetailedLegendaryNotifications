package eatAWESOME.pixelmondetailedlegendarynotifications.events;

import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;;

public class LegendarySpawnListener {

	@SubscribeEvent
	public void onSpawn(LegendarySpawnEvent.DoSpawn event) {
		System.out.println("DoSpawn fired!");
		Species species = event.getLegendary();
		Mutable position = event.action.spawnLocation.location.pos;
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
		
		for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
			if (closestPlayer == null) {
				player.sendMessage(new StringTextComponent(species.getTranslatedName().getString() + " spawned at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + "!"), player.getUUID());
			} else if (player.equals(closestPlayer)) {
	            player.sendMessage(new StringTextComponent(species.getTranslatedName().getString() + " spawned near you at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + "!"), player.getUUID());		
			} else {
	            player.sendMessage(new StringTextComponent(species.getTranslatedName().getString() + " spawned near " + closestPlayer.getDisplayName().getString() + "!"), player.getUUID());
			}
        }
	}
}