package com.gregtechceu.gtceu.integration.ae2.machine.trait;

import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTraitType;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.gregtechceu.gtceu.integration.ae2.utils.SerializableManagedGridNode;

import net.minecraft.core.Direction;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;

import appeng.api.networking.GridFlags;
import appeng.me.helpers.BlockEntityNodeListener;
import appeng.me.helpers.IGridConnectedBlockEntity;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * A MachineTrait that is only used for hosting grid node and does not provide grid node capability.
 * Because IGridConnectedMachine has already extended IInWorldGridNodeHost.
 */
public class GridNodeHolder extends MachineTrait {

    public static final MachineTraitType<GridNodeHolder> TYPE = new MachineTraitType<>(GridNodeHolder.class);

    @Override
    public MachineTraitType<GridNodeHolder> getTraitType() {
        return TYPE;
    }

    @SaveField
    protected @Nullable SerializableManagedGridNode mainNode;

    public GridNodeHolder() {}

    protected SerializableManagedGridNode createManagedNode() {
        var machine = getMachine();
        return (SerializableManagedGridNode) new SerializableManagedGridNode((IGridConnectedBlockEntity) machine,
                BlockEntityNodeListener.INSTANCE)
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setVisualRepresentation(machine.getDefinition().getItem())
                .setIdlePowerUsage(ConfigHolder.INSTANCE.compat.ae2.meHatchEnergyUsage)
                .setInWorldNode(true)
                .setExposedOnSides(
                        machine.hasFrontFacing() ? EnumSet.of(machine.getFrontFacing()) :
                                EnumSet.allOf(Direction.class))
                .setTagName("proxy");
    }


    protected void createMainNode() {
        getMainNode().create(getLevel(), getBlockPos());
    }

    public SerializableManagedGridNode getMainNode() {
        if (mainNode == null) throw new IllegalStateException("Tried to get main node before BE fully loaded");
        return mainNode;
    }

    @Override
    public void onMachineLoad() {
        super.onMachineLoad();
        mainNode = createManagedNode();
        if (getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, this::createMainNode));
        }
    }

    @Override
    public void onMachineUnload() {
        super.onMachineUnload();
        getMainNode().destroy();
    }
}
