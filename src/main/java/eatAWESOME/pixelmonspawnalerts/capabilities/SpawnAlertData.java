package eatAWESOME.pixelmonspawnalerts.capabilities;

public class SpawnAlertData implements ISpawnAlertData {
	private String pokedexRewardsLevel = "disable";

    @Override
    public String getSpawnAlert() {
        return this.pokedexRewardsLevel;
    }

    @Override
    public void setSpawnAlert(String name) {
        this.pokedexRewardsLevel = name;
    }
}