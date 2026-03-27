package com.gregtechceu.gtceu.api.machine.trait;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;

public abstract class MultiblockMachineTrait extends MachineTrait {

    public MultiblockMachineTrait() {}

    @Override
    public MultiblockControllerMachine getMachine() {
        return (MultiblockControllerMachine)super.getMachine();
    }

    @Override
    public void setMachine(MetaMachine machine) {
        if (!(machine instanceof MultiblockControllerMachine)) throw new IllegalArgumentException("Multiblock traits can only be attached to multiblock controllers");
        super.setMachine(machine);
    }

    public void onStructureFormed() {}

    public void onStructureInvalid() {}
}
