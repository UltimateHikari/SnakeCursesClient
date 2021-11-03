package me.hikari.snakeclient.data;

class Spawner {
    private static final Coord INVALID_COORD = new Coord(-1, -1);
    private final boolean[] isInvalidated;
    private final Coord dims;

    Spawner(Coord dims) {
        this.dims = dims;
        isInvalidated = new boolean[dims.getX() * dims.getY()];
    }

    private void invalidate(Coord c) {
        isInvalidated[dims.getX() * c.getY() + c.getX()] = true;
    }

    // utterly ineffective, lots of Coord getting to construct
    // TODO optimisation - replicate withRelative logic here
    void putSnakeCell(Coord c) {
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                invalidate(c.withRelative(new Coord(i, j), dims));
            }
        }
    }

    static boolean isValid(Coord c) {
        return !INVALID_COORD.equals(c);
    }

    Coord find() {
        for (int i = 0; i < isInvalidated.length; i++) {
            if (!isInvalidated[i]) {
                return new Coord(i / dims.getX(), i % dims.getX());
            }
        }
        return INVALID_COORD;
    }
}
