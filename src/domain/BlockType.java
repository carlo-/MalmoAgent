package domain;

/**
 * Created by Mart on 9.10.2017.
 */
public enum BlockType {
    Any,
    air,
    log,
    planks,
    stone,
    cobblestone,
    grass,
    dirt,
    UNKNOWN //Used by GSON library for types not defined here, to avoid NullPointerException
}
