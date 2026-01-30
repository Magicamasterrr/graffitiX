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

        public String getPainter() { return painter; }
        public int getColor() { return color; }
        public byte[] getTag() { return tag; }
        public long getPaintedAt() { return paintedAt; }
        public int getCellId() { return cellId; }
        public int getR() { return (color >> 16) & 0xFF; }
        public int getG() { return (color >> 8) & 0xFF; }
        public int getB() { return color & 0xFF; }
    }

    private final Map<Integer, Cell> grid = new ConcurrentHashMap<>();
    private final Map<String, List<Integer>> painterCells = new ConcurrentHashMap<>();
    private final Map<String, Integer> paintCount = new ConcurrentHashMap<>();
    private final List<Integer> paintedCellIds = new CopyOnWriteArrayList<>();
    private int totalPainted = 0;
    private final List<String> uniquePainters = new CopyOnWriteArrayList<>();
    private final Set<String> hasPaintedSet = ConcurrentHashMap.newKeySet();
    private BigDecimal totalCollected = BigDecimal.ZERO;

    /**
     * Constructor - no parameters needed, pre-populated
     */
    public BaseGraffitiWall(BigDecimal initialFunding) {
        BigDecimal minimum = new BigDecimal("5000000000000000"); // 0.005 ether
        if (initialFunding.compareTo(minimum) < 0) {
            throw new IllegalArgumentException("Initial paint fund required (minimum 0.005 ether)");
        }
        totalCollected = initialFunding;
        System.out.println("Canvas initialized: " + CANVAS_NAME);
        System.out.println("Genesis block: " + GENESIS_BLOCK);
    }

    /**
     * Paint a cell on the wall
     * @param painter Painter address
     * @param x Column (0 to GRID_WIDTH - 1)
     * @param y Row (0 to GRID_HEIGHT - 1)
     * @param color RGB as int (e.g. 0xFF5733)
     * @param tag Short tag (up to 16 bytes)
     * @param payment Payment in wei
     */
    public Cell paint(String painter, int x, int y, int color, byte[] tag, BigDecimal payment) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            throw new IllegalArgumentException("Out of bounds");
        }
        if (payment.compareTo(PAINT_COST_WEI) < 0) {
            throw new IllegalArgumentException("Insufficient paint fee");
        }

        int cellId = y * GRID_WIDTH + x;
        if (!grid.containsKey(cellId)) {
            totalPainted++;
            paintedCellIds.add(cellId);
        }

        long paintedAt = System.currentTimeMillis() / 1000;
        byte[] tagBytes = tag != null ? Arrays.copyOf(tag, Math.min(tag.length, MAX_TAG_BYTES)) : new byte[0];
        Cell cell = new Cell(painter, color, tagBytes, paintedAt, cellId);
        grid.put(cellId, cell);

        painterCells.computeIfAbsent(painter, k -> new CopyOnWriteArrayList<>()).add(cellId);
        paintCount.merge(painter, 1, Integer::sum);

        if (hasPaintedSet.add(painter)) {
            uniquePainters.add(painter);
        }

        totalCollected = totalCollected.add(payment);
        return cell;
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            throw new IllegalArgumentException("Out of bounds");
        }
        return grid.get(y * GRID_WIDTH + x);
    }

    public Cell getCellById(int cellId) {
        if (cellId < 0 || cellId >= TOTAL_CELLS) {
            throw new IllegalArgumentException("Invalid cell");
        }
        return grid.get(cellId);
    }

    public List<Integer> getPainterCells(String painter) {
        return new ArrayList<>(painterCells.getOrDefault(painter, Collections.emptyList()));
    }

    public static class WallStats {
        public final int totalPainted;
        public final int totalCells;
        public final BigDecimal totalCollected;
        public final int uniquePainters;
        public final int cellsRemaining;

        public WallStats(int totalPainted, int totalCells, BigDecimal totalCollected, int uniquePainters, int cellsRemaining) {
            this.totalPainted = totalPainted;
            this.totalCells = totalCells;
            this.totalCollected = totalCollected;
            this.uniquePainters = uniquePainters;
            this.cellsRemaining = cellsRemaining;
        }
    }

    public WallStats getWallStats() {
        return new WallStats(
            totalPainted,
            TOTAL_CELLS,
            totalCollected,
            uniquePainters.size(),
            TOTAL_CELLS - totalPainted
        );
    }

    public List<String> getAllPainters() {
        return new ArrayList<>(uniquePainters);
    }

    public List<Integer> getPaintedCellIds() {
        return new ArrayList<>(paintedCellIds);
    }

    public boolean isPainted(int x, int y) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) return false;
        return grid.containsKey(y * GRID_WIDTH + x);
    }

    public void addFunds(BigDecimal amount) {
        totalCollected = totalCollected.add(amount);
    }

