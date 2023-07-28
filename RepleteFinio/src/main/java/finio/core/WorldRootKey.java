package finio.core;

// Represents a key that is conceptually not the key
// that corresponds to any value in a map.

public class WorldRootKey {

    private static WorldRootKey key = new WorldRootKey();

    public static WorldRootKey get() {
        return key;
    }

    private WorldRootKey() {

    }

}
