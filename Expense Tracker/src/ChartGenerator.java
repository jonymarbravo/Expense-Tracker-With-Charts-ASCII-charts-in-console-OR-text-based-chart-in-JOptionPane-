import java.util.*;

/**
 * ASCII Chart Generator
 * Creates beautiful text-based charts for expense visualization
 */
public class ChartGenerator {

    private static final String BLOCK_FULL = "█";
    private static final String BLOCK_HALF = "▌";
    private static final int MAX_BAR_LENGTH = 50;
    private static final int CATEGORY_WIDTH = 15;

    /**
     * Generate horizontal bar chart by category
     */
    public static String generateCategoryChart(Map<String, Double> categoryTotals) {
        if (categoryTotals == null || categoryTotals.isEmpty()) {
            return "No data available for chart.";
        }

        StringBuilder chart = new StringBuilder();

        // Header
        chart.append("╔══════════════════════════════════════════════════════════════════════╗\n");
        chart.append("║                    EXPENSES BY CATEGORY                              ║\n");
        chart.append("╚══════════════════════════════════════════════════════════════════════╝\n\n");

        // Find maximum value for scaling
        double maxAmount = categoryTotals.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1.0);

        // Sort by amount (descending)
        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(categoryTotals.entrySet());
        sortedEntries.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        // Generate bars
        for (Map.Entry<String, Double> entry : sortedEntries) {
            String category = entry.getKey();
            double amount = entry.getValue();

            // Calculate bar length
            int barLength = (int) ((amount / maxAmount) * MAX_BAR_LENGTH);

            // Get emoji for category
            String emoji = ExpenseCategory.fromString(category).getEmoji();

            // Format category name with padding
            String paddedCategory = String.format("%-" + CATEGORY_WIDTH + "s", emoji + " " + category);

            // Create bar
            String bar = BLOCK_FULL.repeat(Math.max(0, barLength));

            // Format amount
            String amountStr = String.format("$%,.2f", amount);

            // Add to chart
            chart.append(paddedCategory)
                    .append(" | ")
                    .append(bar)
                    .append(" ")
                    .append(amountStr)
                    .append("\n");
        }

        return chart.toString();
    }

    /**
     * Generate monthly comparison chart
     */
    public static String generateMonthlyChart(Map<String, Double> monthlyTotals) {
        if (monthlyTotals == null || monthlyTotals.isEmpty()) {
            return "No data available for monthly chart.";
        }

        StringBuilder chart = new StringBuilder();

        // Header
        chart.append("╔══════════════════════════════════════════════════════════════════════╗\n");
        chart.append("║                    MONTHLY EXPENSE TREND                             ║\n");
        chart.append("╚══════════════════════════════════════════════════════════════════════╝\n\n");

        // Find maximum value for scaling
        double maxAmount = monthlyTotals.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1.0);

        // Sort by month
        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(monthlyTotals.entrySet());
        sortedEntries.sort(Map.Entry.comparingByKey());

        // Generate bars
        for (Map.Entry<String, Double> entry : sortedEntries) {
            String month = entry.getKey();
            double amount = entry.getValue();

            // Calculate bar length
            int barLength = (int) ((amount / maxAmount) * MAX_BAR_LENGTH);

            // Format month
            String paddedMonth = String.format("%-12s", formatMonth(month));

            // Create bar
            String bar = BLOCK_FULL.repeat(Math.max(0, barLength));

            // Format amount
            String amountStr = String.format("$%,.2f", amount);

            // Add to chart
            chart.append(paddedMonth)
                    .append(" | ")
                    .append(bar)
                    .append(" ")
                    .append(amountStr)
                    .append("\n");
        }

        return chart.toString();
    }

    /**
     * Generate pie chart representation (text-based)
     */
    public static String generatePieChart(Map<String, Double> categoryTotals) {
        if (categoryTotals == null || categoryTotals.isEmpty()) {
            return "No data available for pie chart.";
        }

        StringBuilder chart = new StringBuilder();

        // Header
        chart.append("╔══════════════════════════════════════════════════════════════════════╗\n");
        chart.append("║                  EXPENSE DISTRIBUTION (%)                            ║\n");
        chart.append("╚══════════════════════════════════════════════════════════════════════╝\n\n");

        // Calculate total
        double total = categoryTotals.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // Sort by amount (descending)
        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(categoryTotals.entrySet());
        sortedEntries.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        // Generate percentage bars
        for (Map.Entry<String, Double> entry : sortedEntries) {
            String category = entry.getKey();
            double amount = entry.getValue();
            double percentage = (amount / total) * 100;

            // Get emoji
            String emoji = ExpenseCategory.fromString(category).getEmoji();

            // Format category name with padding
            String paddedCategory = String.format("%-" + CATEGORY_WIDTH + "s", emoji + " " + category);

            // Calculate bar length (out of 50)
            int barLength = (int) ((percentage / 100.0) * MAX_BAR_LENGTH);
            String bar = BLOCK_FULL.repeat(Math.max(0, barLength));

            // Format percentage and amount
            String percentStr = String.format("%5.1f%%", percentage);
            String amountStr = String.format("$%,.2f", amount);

            // Add to chart
            chart.append(paddedCategory)
                    .append(" | ")
                    .append(bar)
                    .append(" ")
                    .append(percentStr)
                    .append(" (")
                    .append(amountStr)
                    .append(")\n");
        }

        chart.append("\nTotal: $").append(String.format("%,.2f", total));

        return chart.toString();
    }

    /**
     * Generate summary statistics box
     */
    public static String generateSummaryBox(Map<String, Object> stats) {
        StringBuilder box = new StringBuilder();

        box.append("╔══════════════════════════════════════════════════════════════════════╗\n");
        box.append("║                        EXPENSE SUMMARY                               ║\n");
        box.append("╚══════════════════════════════════════════════════════════════════════╝\n\n");

        box.append(String.format("  Total Expenses:        $%,.2f\n", stats.get("total")));
        box.append(String.format("  Number of Entries:     %d\n", stats.get("count")));
        box.append(String.format("  Average Expense:       $%,.2f\n", stats.get("average")));
        box.append(String.format("  Highest Expense:       $%,.2f\n", stats.get("max")));
        box.append(String.format("  Lowest Expense:        $%,.2f\n", stats.get("min")));
        box.append(String.format("  Most Used Category:    %s\n", stats.get("topCategory")));
        box.append(String.format("  Most Expensive Day:    %s\n", stats.get("maxDay")));

        return box.toString();
    }

    /**
     * Generate comparison chart (This Month vs Last Month)
     */
    public static String generateComparisonChart(double thisMonth, double lastMonth) {
        StringBuilder chart = new StringBuilder();

        chart.append("╔══════════════════════════════════════════════════════════════════════╗\n");
        chart.append("║                    MONTH-TO-MONTH COMPARISON                         ║\n");
        chart.append("╚══════════════════════════════════════════════════════════════════════╝\n\n");

        double maxAmount = Math.max(thisMonth, lastMonth);

        // This month
        int thisMonthBar = maxAmount > 0 ? (int) ((thisMonth / maxAmount) * MAX_BAR_LENGTH) : 0;
        chart.append(String.format("This Month   | %s $%,.2f\n",
                BLOCK_FULL.repeat(Math.max(0, thisMonthBar)), thisMonth));

        // Last month
        int lastMonthBar = maxAmount > 0 ? (int) ((lastMonth / maxAmount) * MAX_BAR_LENGTH) : 0;
        chart.append(String.format("Last Month   | %s $%,.2f\n",
                BLOCK_FULL.repeat(Math.max(0, lastMonthBar)), lastMonth));

        // Calculate difference
        double difference = thisMonth - lastMonth;
        double percentChange = lastMonth > 0 ? (difference / lastMonth) * 100 : 0;

        chart.append("\n");
        if (difference > 0) {
            chart.append(String.format("↑ Increase: $%,.2f (%.1f%%)\n", difference, percentChange));
        } else if (difference < 0) {
            chart.append(String.format("↓ Decrease: $%,.2f (%.1f%%)\n", Math.abs(difference), Math.abs(percentChange)));
        } else {
            chart.append("→ No change\n");
        }

        return chart.toString();
    }

    /**
     * Generate weekly chart
     */
    public static String generateWeeklyChart(Map<Integer, Double> weeklyTotals) {
        if (weeklyTotals == null || weeklyTotals.isEmpty()) {
            return "No data available for weekly chart.";
        }

        StringBuilder chart = new StringBuilder();

        chart.append("╔══════════════════════════════════════════════════════════════════════╗\n");
        chart.append("║                      WEEKLY EXPENSE TREND                            ║\n");
        chart.append("╚══════════════════════════════════════════════════════════════════════╝\n\n");

        double maxAmount = weeklyTotals.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1.0);

        List<Map.Entry<Integer, Double>> sortedEntries = new ArrayList<>(weeklyTotals.entrySet());
        sortedEntries.sort(Map.Entry.comparingByKey());

        for (Map.Entry<Integer, Double> entry : sortedEntries) {
            int week = entry.getKey();
            double amount = entry.getValue();

            int barLength = (int) ((amount / maxAmount) * MAX_BAR_LENGTH);
            String paddedWeek = String.format("Week %-7d", week);
            String bar = BLOCK_FULL.repeat(Math.max(0, barLength));
            String amountStr = String.format("$%,.2f", amount);

            chart.append(paddedWeek)
                    .append(" | ")
                    .append(bar)
                    .append(" ")
                    .append(amountStr)
                    .append("\n");
        }

        return chart.toString();
    }

    /**
     * Format month string (yyyy-MM to Month Year)
     */
    private static String formatMonth(String yearMonth) {
        try {
            String[] parts = yearMonth.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            return monthNames[month - 1] + " " + year;
        } catch (Exception e) {
            return yearMonth;
        }
    }
}