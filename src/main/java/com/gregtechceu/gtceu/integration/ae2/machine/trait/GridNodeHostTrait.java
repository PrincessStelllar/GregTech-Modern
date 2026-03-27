package com.gregtechceu.gtceu.integration.ae2.machine.trait;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTraitType;

import net.minecraft.core.Direction;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AECableType;
import appeng.me.helpers.BlockEntityNodeListener;
import appeng.me.helpers.IGridConnectedBlockEntity;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class GridNodeHostTrait extends MachineTrait implements IGridConnectedBlockEntity {

    public static final MachineTraitType<GridNodeHostTrait> TYPE = new MachineTraitType<>(GridNodeHostTrait.class);

    @Override
    public MachineTraitType<GridNodeHostTrait> getTraitType() {
        return TYPE;
    }

    private @Nullable IManagedGridNode proxy;

    public GridNodeHostTrait() {}

    @Override
    public void onMachineLoad() {
        this.proxy = GridHelper.createManagedNode(this, BlockEntityNodeListener.INSTANCE)
                .setInWorldNode(true)
                .setVisualRepresentation(getMachine().getDefinition().getItem());

        if (getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, this::init));
        }
    }

    @Override
    public void onMachineUnload() {
        getMainNode().destroy();
    }

    public void init() {
        if (this.proxy != null) this.proxy.create(getLevel(), getBlockPos());
    }

    @Override
    public IManagedGridNode getMainNode() {
        if (proxy == null) throw new IllegalStateException("Tried to get ae2 grid node before BE fully loaded");
        return proxy;
    }

    @Override
    public void saveChanges() {}

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }
}
