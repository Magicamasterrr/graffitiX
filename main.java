import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base Graffiti Wall - Java Implementation
 * Collaborative onchain pixel canvas — paint cells, leave your mark forever
 * Aligned with Base ethos: onchain art, community canvas, immutable graffiti
 */
public class BaseGraffitiWall {

    // Pre-populated configuration (unique values)
    public static final int GRID_WIDTH = 64;
    public static final int GRID_HEIGHT = 64;
    public static final int TOTAL_CELLS = 4096; // 64 * 64
    public static final BigDecimal PAINT_COST_WEI = new BigDecimal("310000000000000"); // 0.00031 ether
    public static final int MAX_TAG_BYTES = 16;
    public static final String CANVAS_SEED = "e2b91a4f7c3d8e6b9a1c5f0d2e4b7a9c3d6e8f1a4b7c0d3e6f9a2b5c8e1f4a7b";
    public static final String CANVAS_NAME = "Base Graffiti Wall #3194";
    public static final String CANVAS_SYMBOL = "BGW3194";
    public static final long GENESIS_BLOCK = 17382816L;

    public static class Cell {
        private final String painter;
        private final int color; // RGB packed: R << 16 | G << 8 | B
        private final byte[] tag;
        private final long paintedAt;
        private final int cellId;

        public Cell(String painter, int color, byte[] tag, long paintedAt, int cellId) {
            this.painter = painter;
            this.color = color;
            this.tag = tag != null ? Arrays.copyOf(tag, Math.min(tag.length, MAX_TAG_BYTES)) : new byte[0];
            this.paintedAt = paintedAt;
            this.cellId = cellId;
        }
