package com.michaelgrenon.chargerks.ops;

import java.util.List;

public interface MultiCommand {
    public List<Command> toList();
}