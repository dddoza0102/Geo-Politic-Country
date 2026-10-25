package dev.doza.politic.model.region.flag;

import dev.doza.politic.model.region.flag.type.FlagType;

public class Flag {
    private FlagType type;
    private boolean value;

    public Flag(FlagType type, boolean value) {
        this.type = type;
        this.value = value;
    }

    public FlagType getType() {
        return type;
    }

    public void setType(FlagType type) {
        this.type = type;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
