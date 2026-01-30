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
