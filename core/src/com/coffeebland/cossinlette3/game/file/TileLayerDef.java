package com.coffeebland.cossinlette3.game.file;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class TileLayerDef extends ActorDef {

    // There are 64 bits in a long
    // We define the tile as 4 16 bits numbers; (65k possibilities; plenty for our uses)
    public static final int
            TYPE_MASK_SHIFT =   48,
            INDEX_MASK_SHIFT =  32,
            TILE_X_MASK_SHIFT = 16,
            TILE_Y_MASK_SHIFT = 0;

    public static final long
            TYPE_MASK =     0xFFFF_0000_0000_0000l,
            INDEX_MASK =    0x0000_FFFF_0000_0000l,
            TILE_X_MASK =   0x0000_0000_FFFF_0000l,
            TILE_Y_MASK =   0x0000_0000_0000_FFFFl;

    public static final int NO_TILE = -1;

    // Mapped by rows, then columns, with each tile being an array of longs; each long is a tile comprised of 4 16-bit numbers; type, index, x, y
    public long[][][] tiles;

    public TileLayerDef() {}
    public TileLayerDef(@NotNull WorldDef def, float priority) {
        tiles = new long[def.height][def.width][0];
        this.priority = priority;
    }

    @Nullable
    public long[] getTiles(int x, int y) { return tiles[y][x]; }
    @SuppressWarnings("PointlessBitwiseExpression")
    public long getTile(int type, int typeIndex, int tileX, int tileY) {
        return (
                (long)type << TYPE_MASK_SHIFT |
                (long)typeIndex << INDEX_MASK_SHIFT |
                (long)tileX << TILE_X_MASK_SHIFT |
                (long)tileY << TILE_Y_MASK_SHIFT
        );
    }
    public void addTile(int x, int y, int type, int typeIndex, int tileX, int tileY, boolean fromTop) {
        addTile(x, y, getTile(type, typeIndex, tileX, tileY), fromTop);
    }
    public void addTile(int x, int y, long tile, boolean fromTop) {
        long[] tileDefs = tiles[y][x];
        long[] newTiles = Arrays.copyOf(tileDefs, tileDefs.length + 1);
        if (fromTop) {
            newTiles[tileDefs.length] = tile;
        } else {
            for (int i = newTiles.length - 2; i >= 0; i--) newTiles[i + 1] = newTiles[i];
            newTiles[0] = tile;
        }
        tiles[y][x] = newTiles;
    }

    /**
     * Removes the requested tile and returns whatever tile was removed.
     * @NO_TILE if no tile could be removed
     */
    public long removeTile(int x, int y, boolean fromTop) {
        long[] tileDefs = tiles[y][x];
        if (tileDefs.length == 0) return NO_TILE;
        if (tileDefs.length == 1) {
            tiles[y][x] = new long[0];
            return tileDefs[0];
        }
        if (fromTop) {
            tiles[y][x] = Arrays.copyOf(tileDefs, tileDefs.length - 1);
            return tileDefs[tileDefs.length - 1];
        } else {
            tiles[y][x] = Arrays.copyOfRange(tileDefs, 1, tileDefs.length);
            return tileDefs[0];
        }
    }
    @Nullable
    public long[] setTile(int x, int y, int type, int typeIndex, int tileX, int tileY) {
        long[] oldTiles = tiles[y][x];
        tiles[y][x] = new long[] { getTile(type, typeIndex, tileX, tileY) };
        return oldTiles;
    }
    @Nullable
    public long[] setTiles(int x, int y, @Nullable long[] newTiles) {
        long[] oldTiles = tiles[y][x];
        tiles[y][x] = newTiles;
        return oldTiles;
    }
}
