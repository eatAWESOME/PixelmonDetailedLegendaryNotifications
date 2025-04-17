package eatAWESOME.pixelmonspawnalerts.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpawnAlertDataProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(ISpawnAlertData.class)
    public static final Capability<ISpawnAlertData> spawnAlertLocation = null;

    private LazyOptional<ISpawnAlertData> instance = LazyOptional.of(spawnAlertLocation::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == spawnAlertLocation ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return spawnAlertLocation.getStorage().writeNBT(spawnAlertLocation, this.instance.orElse(null), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
    	spawnAlertLocation.getStorage().readNBT(spawnAlertLocation, this.instance.orElse(null), null, nbt);
    }
}