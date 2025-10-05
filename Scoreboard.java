public class Scoreboard {
    private int score;
    private int level; // starts at 1
    private int totalLines;

    public Scoreboard() {
        reset();
    }

    public void reset() {
        score = 0;
        level = 1;
        totalLines = 0;
    }

    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getTotalLines() { return totalLines; }

    public void addLines(int count) {
        if (count <= 0) return;
        // Classic-like line scores: 1=100, 2=300, 3=500, 4=800
        int gained = 0;
        switch (count) {
            case 1: gained = 100; break;
            case 2: gained = 300; break;
            case 3: gained = 500; break;
            case 4: gained = 800; break;
            default: gained = 100 * count; // fallback
        }
        score += gained * level;
        totalLines += count;
        level = 1 + (totalLines / 10); // level up every 10 lines
    }

    public void addSoftDrop(int cells) {
        if (cells > 0) score += cells; // +1 per soft-dropped cell
    }

    public void addHardDrop(int cells) {
        if (cells > 0) score += 2 * cells; // +2 per hard-dropped cell
    }

    public int getFallDelayMs() {
        // Decrease with level. Cap minimum at 80ms for playability.
        int base = 700; // level 1
        int decrement = (level - 1) * 50;
        int delay = base - decrement;
        if (delay < 80) delay = 80;
        return delay;
    }
}
