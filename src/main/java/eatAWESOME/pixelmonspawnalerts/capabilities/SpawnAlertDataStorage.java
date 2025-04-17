package eatAWESOME.pixelmonspawnalerts.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import javax.annotation.Nullable;

public class SpawnAlertDataStorage implements Capability.IStorage<ISpawnAlertData> {

    private static final String spawnAlertNBT = "spawnAlert";

    @Nullable
    @Override
    public INBT writeNBT(Capability<ISpawnAlertData> capability, ISpawnAlertData instance, Direction side) {
        final CompoundNBT nbt = new CompoundNBT();
        nbt.putString(spawnAlertNBT, instance.getSpawnAlert());
        return nbt;
    }

    @Override
    public void readNBT(Capability<ISpawnAlertData> capability, ISpawnAlertData instance, Direction side, INBT nbt) {
        if(nbt instanceof CompoundNBT) {
            CompoundNBT compoundNBT = (CompoundNBT) nbt;
            instance.setSpawnAlert(compoundNBT.getString(spawnAlertNBT));
        }
    }
}
