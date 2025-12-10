/**
 * Expense Categories Enum
 * Predefined categories with emojis for better visualization
 */
public enum ExpenseCategory {
    FOOD("Food", "🍔"),
    TRANSPORT("Transport", "🚗"),
    BILLS("Bills", "💡"),
    ENTERTAINMENT("Entertainment", "🎬"),
    SHOPPING("Shopping", "🛍️"),
    HEALTHCARE("Healthcare", "⚕️"),
    EDUCATION("Education", "📚"),
    HOUSING("Housing", "🏠"),
    SAVINGS("Savings", "💰"),
    PERSONAL("Personal", "👤"),
    OTHER("Other", "📦");

    private final String displayName;
    private final String emoji;

    ExpenseCategory(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDisplayWithEmoji() {
        return emoji + " " + displayName;
    }

    /**
     * Get category from string (case-insensitive)
     */
    public static ExpenseCategory fromString(String category) {
        if (category == null || category.trim().isEmpty()) {
            return OTHER;
        }

        for (ExpenseCategory cat : values()) {
            if (cat.displayName.equalsIgnoreCase(category.trim())) {
                return cat;
            }
        }
        return OTHER;
    }

    /**
     * Get all category names as array
     */
    public static String[] getAllCategoryNames() {
        ExpenseCategory[] categories = values();
        String[] names = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            names[i] = categories[i].getDisplayWithEmoji();
        }
        return names;
    }

    /**
     * Get category name without emoji
     */
    public static String getCategoryNameFromDisplay(String displayWithEmoji) {
        if (displayWithEmoji == null) return "Other";

        // Remove emoji and trim
        String cleaned = displayWithEmoji.replaceAll("[^a-zA-Z ]", "").trim();
        return cleaned.isEmpty() ? "Other" : cleaned;
    }

    @Override
    public String toString() {
        return displayName;
    }
}