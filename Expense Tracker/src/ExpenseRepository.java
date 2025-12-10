import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository class for managing expense data persistence
 */
public class ExpenseRepository {

    private static final String EXPENSE_FILE = "expenses.txt";
    private static final String BACKUP_FILE = "expenses_backup.txt";
    private static final String BUDGET_FILE = "budget.dat";

    private final Path expensePath;
    private final Path backupPath;
    private final Path budgetPath;

    private List<Expense> expenses;
    private Map<String, Double> budgets; // Category -> Budget limit

    public ExpenseRepository() {
        this.expensePath = Paths.get(EXPENSE_FILE);
        this.backupPath = Paths.get(BACKUP_FILE);
        this.budgetPath = Paths.get(BUDGET_FILE);
        this.expenses = new ArrayList<>();
        this.budgets = new HashMap<>();

        loadExpenses();
        loadBudgets();
    }

    /**
     * Load expenses from file
     */
    private void loadExpenses() {
        if (!Files.exists(expensePath)) {
            System.out.println("No expense file found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(expensePath)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    Expense expense = Expense.fromFileFormat(line);
                    expenses.add(expense);
                } catch (Exception e) {
                    System.err.println("Error loading line " + lineNumber + ": " + e.getMessage());
                }
            }

            System.out.println("Loaded " + expenses.size() + " expenses.");
        } catch (IOException e) {
            System.err.println("Error reading expense file: " + e.getMessage());
        }
    }

    /**
     * Save expenses to file
     */
    private boolean saveExpenses() {
        try {
            // Create backup
            if (Files.exists(expensePath)) {
                Files.copy(expensePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Write to file
            try (BufferedWriter writer = Files.newBufferedWriter(expensePath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                writer.write("# Expense Tracker Data File\n");
                writer.write("# Format: ID|Description|Amount|Category|Date|PaymentMethod|Notes\n");
                writer.write("# Last updated: " + new Date() + "\n");

                for (Expense expense : expenses) {
                    writer.write(expense.toFileFormat());
                    writer.newLine();
                }
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error saving expenses: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load budgets from file
     */
    private void loadBudgets() {
        if (!Files.exists(budgetPath)) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(budgetPath.toFile()))) {
            budgets = (Map<String, Double>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error loading budgets: " + e.getMessage());
            budgets = new HashMap<>();
        }
    }

    /**
     * Save budgets to file
     */
    private boolean saveBudgets() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(budgetPath.toFile()))) {
            oos.writeObject(budgets);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving budgets: " + e.getMessage());
            return false;
        }
    }

    /**
     * Add new expense
     */
    public boolean addExpense(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be null");
        }

        expenses.add(expense);
        return saveExpenses();
    }

    /**
     * Update expense
     */
    public boolean updateExpense(String id, Expense updatedExpense) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId().equals(id)) {
                expenses.set(i, updatedExpense);
                return saveExpenses();
            }
        }
        return false;
    }

    /**
     * Delete expense
     */
    public boolean deleteExpense(String id) {
        boolean removed = expenses.removeIf(e -> e.getId().equals(id));
        if (removed) {
            saveExpenses();
        }
        return removed;
    }

    /**
     * Find expense by ID
     */
    public Expense findById(String id) {
        return expenses.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get all expenses
     */
    public List<Expense> getAllExpenses() {
        List<Expense> sorted = new ArrayList<>(expenses);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Get expenses by date range
     */
    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenses.stream()
                .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get expenses by category
     */
    public List<Expense> getExpensesByCategory(String category) {
        return expenses.stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get expenses for current month
     */
    public List<Expense> getCurrentMonthExpenses() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();
        return getExpensesByDateRange(startDate, endDate);
    }

    /**
     * Get expenses for last month
     */
    public List<Expense> getLastMonthExpenses() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDate startDate = lastMonth.atDay(1);
        LocalDate endDate = lastMonth.atEndOfMonth();
        return getExpensesByDateRange(startDate, endDate);
    }

    /**
     * Get expenses for current week
     */
    public List<Expense> getCurrentWeekExpenses() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        return getExpensesByDateRange(startOfWeek, today);
    }

    /**
     * Get total by category
     */
    public Map<String, Double> getTotalByCategory() {
        Map<String, Double> totals = new HashMap<>();

        for (Expense expense : expenses) {
            String category = expense.getCategory();
            totals.put(category, totals.getOrDefault(category, 0.0) + expense.getAmount());
        }

        return totals;
    }

    /**
     * Get total by month
     */
    public Map<String, Double> getTotalByMonth() {
        Map<String, Double> totals = new HashMap<>();

        for (Expense expense : expenses) {
            String month = expense.getYearMonth();
            totals.put(month, totals.getOrDefault(month, 0.0) + expense.getAmount());
        }

        return totals;
    }

    /**
     * Get total by week
     */
    public Map<Integer, Double> getTotalByWeek() {
        Map<Integer, Double> totals = new HashMap<>();

        for (Expense expense : expenses) {
            int week = expense.getWeekOfYear();
            totals.put(week, totals.getOrDefault(week, 0.0) + expense.getAmount());
        }

        return totals;
    }

    /**
     * Calculate total expenses
     */
    public double calculateTotal() {
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Calculate total for current month
     */
    public double calculateCurrentMonthTotal() {
        return getCurrentMonthExpenses().stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Calculate total for last month
     */
    public double calculateLastMonthTotal() {
        return getLastMonthExpenses().stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Get statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        if (expenses.isEmpty()) {
            stats.put("total", 0.0);
            stats.put("count", 0);
            stats.put("average", 0.0);
            stats.put("max", 0.0);
            stats.put("min", 0.0);
            stats.put("topCategory", "N/A");
            stats.put("maxDay", "N/A");
            return stats;
        }

        double total = calculateTotal();
        int count = expenses.size();
        double average = total / count;

        double max = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .max()
                .orElse(0.0);

        double min = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .min()
                .orElse(0.0);

        // Find top category
        Map<String, Double> categoryTotals = getTotalByCategory();
        String topCategory = categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        // Find day with highest expenses
        Map<LocalDate, Double> dailyTotals = new HashMap<>();
        for (Expense expense : expenses) {
            LocalDate date = expense.getDate();
            dailyTotals.put(date, dailyTotals.getOrDefault(date, 0.0) + expense.getAmount());
        }

        String maxDay = dailyTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey().toString())
                .orElse("N/A");

        stats.put("total", total);
        stats.put("count", count);
        stats.put("average", average);
        stats.put("max", max);
        stats.put("min", min);
        stats.put("topCategory", topCategory);
        stats.put("maxDay", maxDay);

        return stats;
    }

    /**
     * Search expenses
     */
    public List<Expense> searchExpenses(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllExpenses();
        }

        String lowerQuery = query.toLowerCase();
        return expenses.stream()
                .filter(e -> e.getDescription().toLowerCase().contains(lowerQuery) ||
                        e.getCategory().toLowerCase().contains(lowerQuery) ||
                        e.getNotes().toLowerCase().contains(lowerQuery))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Set budget for category
     */
    public void setBudget(String category, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Budget amount cannot be negative");
        }
        budgets.put(category, amount);
        saveBudgets();
    }

    /**
     * Get budget for category
     */
    public double getBudget(String category) {
        return budgets.getOrDefault(category, 0.0);
    }

    /**
     * Get all budgets
     */
    public Map<String, Double> getAllBudgets() {
        return new HashMap<>(budgets);
    }

    /**
     * Check if category is over budget
     */
    public boolean isOverBudget(String category) {
        double budget = getBudget(category);
        if (budget == 0) return false;

        double spent = getTotalByCategory().getOrDefault(category, 0.0);
        return spent > budget;
    }

    /**
     * Get budget status for all categories
     */
    public Map<String, Map<String, Double>> getBudgetStatus() {
        Map<String, Map<String, Double>> status = new HashMap<>();
        Map<String, Double> categoryTotals = getTotalByCategory();

        for (Map.Entry<String, Double> entry : budgets.entrySet()) {
            String category = entry.getKey();
            double budget = entry.getValue();
            double spent = categoryTotals.getOrDefault(category, 0.0);
            double remaining = budget - spent;
            double percentage = (spent / budget) * 100;

            Map<String, Double> details = new HashMap<>();
            details.put("budget", budget);
            details.put("spent", spent);
            details.put("remaining", remaining);
            details.put("percentage", percentage);

            status.put(category, details);
        }

        return status;
    }

    /**
     * Export expenses to CSV
     */
    public boolean exportToCSV(String filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            // Write header
            writer.write("Date,Description,Amount,Category,Payment Method,Notes\n");

            // Write data
            for (Expense expense : getAllExpenses()) {
                writer.write(String.format("%s,\"%s\",%.2f,%s,%s,\"%s\"\n",
                        expense.getDate(),
                        expense.getDescription(),
                        expense.getAmount(),
                        expense.getCategory(),
                        expense.getPaymentMethod(),
                        expense.getNotes()
                ));
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get expense count
     */
    public int getExpenseCount() {
        return expenses.size();
    }
}