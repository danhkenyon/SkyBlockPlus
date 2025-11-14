package uk.ac.bsfc.sbp.utils.schematic;

import java.util.ArrayList;
import java.util.List;

/**
 * The Clipboard class provides utility to manage a collection of {@link BlockSet} objects.
 * It acts as a container for storing, retrieving, and manipulating `BlockSet` instances,
 * allowing for easy access to the last added entry.
 */
public class Clipboard {
    private final List<BlockSet> blockSets;

    private Clipboard() {
        blockSets = new ArrayList<>();
    }
    public static Clipboard create() {
        return new Clipboard();
    }

    public void add(BlockSet blockSet) {
        blockSets.add(blockSet);
    }
    public BlockSet getLast() {
        if (blockSets.isEmpty()) {
            return null;
        }
        return blockSets.getLast();
    }

    @Override
    public String toString() {
        return "Clipboard[" +
                "blockSets=" + blockSets +
                ']';
    }
}
