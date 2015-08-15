package com.coffeebland.cossinlette3.game.file;

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

    // Mapped by rows, then columns, with each tile being an array of longs; each long is a tile comprised of 4 16-bit numbers; type, index, x, y
    public long[][][] tiles;

    public TileLayerDef() {}

    public long[] getTiles(int x, int y) { return tiles[x][y]; }
    @SuppressWarnings("PointlessBitwiseExpression")
    public long getTile(int type, int typeIndex, int tileX, int tileY) {
        return (
                (long)type << TYPE_MASK_SHIFT |
                (long)typeIndex << INDEX_MASK_SHIFT |
                (long)tileX << TILE_X_MASK_SHIFT |
                (long)tileY << TILE_Y_MASK_SHIFT
        );
    }
    public void addTile(int x, int y, int type, int typeIndex, int tileX, int tileY) {
        long[] tileDefs = tiles[y][x];
        long[] newTiles = Arrays.copyOf(tileDefs, tileDefs.length + 1);
        newTiles[tileDefs.length] = getTile(type, typeIndex, tileX, tileY);
        tiles[y][x] = newTiles;
    }
    public void removeTile(int x, int y) {
        long[] tileDefs = tiles[y][x];
        tiles[x][y] = Arrays.copyOf(tileDefs, Math.max(tileDefs.length - 1, 0));
    }
    public void setTile(int x, int y, int type, int typeIndex, int tileX, int tileY) {
        tiles[x][y] = new long[] { getTile(type, typeIndex, tileX, tileY) };
    }
}
