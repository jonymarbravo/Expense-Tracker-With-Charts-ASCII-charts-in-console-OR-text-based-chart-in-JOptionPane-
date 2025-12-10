import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

/**
 * Expense Tracker - Main Application
 * Track expenses with ASCII charts and comprehensive analytics
 */
public class ExpenseTracker {

    private final ExpenseRepository repository;
    private static final String APP_TITLE = "💰 Expense Tracker";

    public ExpenseTracker() {
        this.repository = new ExpenseRepository();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default
        }

        ExpenseTracker tracker = new ExpenseTracker();
        tracker.run();
    }

    public void run() {
        showWelcome();
        mainMenu();
        showGoodbye();
    }

    private void showWelcome() {
        String message = "╔════════════════════════════════════════╗\n" +
                "║      💰 EXPENSE TRACKER v1.0 💰       ║\n" +
                "╚════════════════════════════════════════╝\n\n" +
                "Smart Expense Management System\n\n" +
                "Features:\n" +
                "✓ Track Daily Expenses\n" +
                "✓ ASCII Chart Visualizations\n" +
                "✓ Budget Management\n" +
                "✓ Category-Based Tracking\n" +
                "✓ Export to CSV\n" +
                "✓ Comprehensive Analytics";

        JOptionPane.showMessageDialog(null, message, APP_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    private void mainMenu() {
        boolean running = true;

        while (running) {
            String choice = showMainMenu();

            if (choice == null) {
                running = confirmExit();
                continue;
            }

            try {
                switch (choice) {
                    case "1":
                        addExpense();
                        break;
                    case "2":
                        viewAllExpenses();
                        break;
                    case "3":
                        viewByCategory();
                        break;
                    case "4":
                        updateExpense();
                        break;
                    case "5":
                        deleteExpense();
                        break;
                    case "6":
                        searchExpenses();
                        break;
                    case "7":
                        viewCharts();
                        break;
                    case "8":
                        viewSummary();
                        break;
                    case "9":
                        manageBudgets();
                        break;
                    case "10":
                        exportData();
                        break;
                    case "11":
                        viewMonthComparison();
                        break;
                    case "12":
                        running = confirmExit();
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid choice. Please select 1-12.",
                                APP_TITLE, JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String showMainMenu() {
        double currentMonthTotal = repository.calculateCurrentMonthTotal();

        String menu = "╔════════════════════════════════════════╗\n" +
                "║         EXPENSE TRACKER MENU           ║\n" +
                "╚════════════════════════════════════════╝\n\n" +
                "1.  ➕ Add New Expense\n" +
                "2.  📋 View All Expenses\n" +
                "3.  📁 View by Category\n" +
                "4.  ✏️  Update Expense\n" +
                "5.  🗑️  Delete Expense\n" +
                "6.  🔍 Search Expenses\n" +
                "7.  📊 View Charts\n" +
                "8.  📈 View Summary Statistics\n" +
                "9.  💵 Manage Budgets\n" +
                "10. 💾 Export Data\n" +
                "11. 🔄 Month Comparison\n" +
                "12. 🚪 Exit\n\n" +
                String.format("Total Expenses: %d\n", repository.getExpenseCount()) +
                String.format("This Month: $%,.2f", currentMonthTotal);

        return JOptionPane.showInputDialog(null, menu, APP_TITLE, JOptionPane.PLAIN_MESSAGE);
    }

    private void addExpense() {
        JTextField descField = new JTextField();
        JTextField amountField = new JTextField();
        JComboBox<String> categoryBox = new JComboBox<>(ExpenseCategory.getAllCategoryNames());
        JTextField dateField = new JTextField(LocalDate.now().toString());
        String[] paymentMethods = {"Cash", "Credit Card", "Debit Card", "Mobile Payment", "Bank Transfer", "Other"};
        JComboBox<String> paymentBox = new JComboBox<>(paymentMethods);
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        Object[] fields = {
                "Description:", descField,
                "Amount ($):", amountField,
                "Category:", categoryBox,
                "Date (YYYY-MM-DD):", dateField,
                "Payment Method:", paymentBox,
                "Notes (Optional):", notesScroll
        };

        boolean validInput = false;
        while (!validInput) {
            int result = JOptionPane.showConfirmDialog(null, fields, "Add New Expense",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                String description = descField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String categoryDisplay = (String) categoryBox.getSelectedItem();
                String category = ExpenseCategory.getCategoryNameFromDisplay(categoryDisplay);
                LocalDate date = LocalDate.parse(dateField.getText());
                String paymentMethod = (String) paymentBox.getSelectedItem();
                String notes = notesArea.getText();

                Expense expense = new Expense(description, amount, category, date, paymentMethod, notes);
                repository.addExpense(expense);

                // Check if over budget
                String warningMsg = "";
                if (repository.isOverBudget(category)) {
                    double budget = repository.getBudget(category);
                    double spent = repository.getTotalByCategory().get(category);
                    warningMsg = String.format("\n\n⚠️ WARNING: Over budget!\n%s Budget: $%.2f\nSpent: $%.2f",
                            category, budget, spent);
                }

                JOptionPane.showMessageDialog(null,
                        "✓ Expense added successfully!\n\n" +
                                expense.getDetailedInfo() + warningMsg,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                validInput = true;

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid amount format. Please enter a valid number.\n\nPlease try again.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid date format. Use YYYY-MM-DD (e.g., 2024-12-10).\n\nPlease try again.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null,
                        e.getMessage() + "\n\nPlease correct and try again.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewAllExpenses() {
        List<Expense> expenses = repository.getAllExpenses();

        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No expenses recorded yet.",
                    "Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] options = new String[expenses.size()];
        for (int i = 0; i < expenses.size(); i++) {
            options[i] = (i + 1) + ". " + expenses.get(i).getCompactDisplay();
        }

        String selection = (String) JOptionPane.showInputDialog(null,
                "Select expense to view details:\n\nTotal: " + expenses.size() + " expenses",
                "All Expenses",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (selection != null) {
            int index = Integer.parseInt(selection.split("\\.")[0]) - 1;
            showExpenseDetails(expenses.get(index));
        }
    }

    private void showExpenseDetails(Expense expense) {
        String details = "═══════════════════════════════════════\n" +
                "         EXPENSE DETAILS\n" +
                "═══════════════════════════════════════\n\n" +
                expense.getDetailedInfo();

        JTextArea textArea = new JTextArea(details);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JOptionPane.showMessageDialog(null, textArea, "Expense Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewByCategory() {
        Map<String, Double> categoryTotals = repository.getTotalByCategory();

        if (categoryTotals.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No expenses recorded yet.",
                    "Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] categories = categoryTotals.keySet().toArray(new String[0]);
        Arrays.sort(categories);

        String selected = (String) JOptionPane.showInputDialog(null,
                "Select a category:",
                "View by Category",
                JOptionPane.QUESTION_MESSAGE,
                null,
                categories,
                categories[0]);

        if (selected != null) {
            List<Expense> expenses = repository.getExpensesByCategory(selected);
            double total = categoryTotals.get(selected);

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Category: %s\n", selected));
            sb.append(String.format("Total: $%.2f\n", total));
            sb.append(String.format("Count: %d\n\n", expenses.size()));

            for (int i = 0; i < expenses.size(); i++) {
                sb.append((i + 1)).append(". ").append(expenses.get(i).getCompactDisplay()).append("\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));

            JOptionPane.showMessageDialog(null, scrollPane, "Category: " + selected,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateExpense() {
        List<Expense> expenses = repository.getAllExpenses();

        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No expenses to update.",
                    "Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] options = new String[expenses.size()];
        for (int i = 0; i < expenses.size(); i++) {
            options[i] = (i + 1) + ". " + expenses.get(i).getCompactDisplay();
        }

        String selection = (String) JOptionPane.showInputDialog(null,
                "Select expense to update:",
                "Update Expense",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (selection == null) return;

        int index = Integer.parseInt(selection.split("\\.")[0]) - 1;
        Expense expense = expenses.get(index);

        JTextField descField = new JTextField(expense.getDescription());
        JTextField amountField = new JTextField(String.valueOf(expense.getAmount()));
        JComboBox<String> categoryBox = new JComboBox<>(ExpenseCategory.getAllCategoryNames());
        categoryBox.setSelectedItem(ExpenseCategory.fromString(expense.getCategory()).getDisplayWithEmoji());
        JTextField dateField = new JTextField(expense.getDate().toString());
        String[] paymentMethods = {"Cash", "Credit Card", "Debit Card", "Mobile Payment", "Bank Transfer", "Other"};
        JComboBox<String> paymentBox = new JComboBox<>(paymentMethods);
        paymentBox.setSelectedItem(expense.getPaymentMethod());
        JTextArea notesArea = new JTextArea(expense.getNotes(), 3, 20);
        notesArea.setLineWrap(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        Object[] fields = {
                "Description:", descField,
                "Amount ($):", amountField,
                "Category:", categoryBox,
                "Date (YYYY-MM-DD):", dateField,
                "Payment Method:", paymentBox,
                "Notes:", notesScroll
        };

        boolean validInput = false;
        while (!validInput) {
            int result = JOptionPane.showConfirmDialog(null, fields, "Update Expense",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                String description = descField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String categoryDisplay = (String) categoryBox.getSelectedItem();
                String category = ExpenseCategory.getCategoryNameFromDisplay(categoryDisplay);
                LocalDate date = LocalDate.parse(dateField.getText());
                String paymentMethod = (String) paymentBox.getSelectedItem();
                String notes = notesArea.getText();

                Expense updatedExpense = new Expense(description, amount, category, date, paymentMethod, notes);
                repository.updateExpense(expense.getId(), updatedExpense);

                JOptionPane.showMessageDialog(null, "✓ Expense updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                validInput = true;

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid amount format.\n\nPlease try again.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid date format. Use YYYY-MM-DD.\n\nPlease try again.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null,
                        e.getMessage() + "\n\nPlease try again.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteExpense() {
        List<Expense> expenses = repository.getAllExpenses();

        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No expenses to delete.",
                    "Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] options = new String[expenses.size()];
        for (int i = 0; i < expenses.size(); i++) {
            options[i] = (i + 1) + ". " + expenses.get(i).getCompactDisplay();
        }

        String selection = (String) JOptionPane.showInputDialog(null,
                "Select expense to delete:",
                "Delete Expense",
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);

        if (selection == null) return;

        int index = Integer.parseInt(selection.split("\\.")[0]) - 1;
        Expense expense = expenses.get(index);

        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this expense?\n\n" +
                        expense.getCompactDisplay() + "\n\n" +
                        "⚠️ This action cannot be undone!",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            repository.deleteExpense(expense.getId());
            JOptionPane.showMessageDialog(null, "✓ Expense deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchExpenses() {
        String query = JOptionPane.showInputDialog(null,
                "Enter search term (description, category, or notes):",
                "Search Expenses",
                JOptionPane.QUESTION_MESSAGE);

        if (query != null && !query.trim().isEmpty()) {
            List<Expense> results = repository.searchExpenses(query);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No expenses found matching: \"" + query + "\"",
                        "No Results", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] options = new String[results.size()];
            for (int i = 0; i < results.size(); i++) {
                options[i] = (i + 1) + ". " + results.get(i).getCompactDisplay();
            }

            String selection = (String) JOptionPane.showInputDialog(null,
                    "Search Results for: \"" + query + "\"\n\nFound " + results.size() + " match(es):",
                    "Search Results",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (selection != null) {
                int idx = Integer.parseInt(selection.split("\\.")[0]) - 1;
                showExpenseDetails(results.get(idx));
            }
        }
    }

    private void viewCharts() {
        String[] chartOptions = {
                "📊 Category Bar Chart",
                "📈 Monthly Trend",
                "🥧 Percentage Distribution",
                "📅 Weekly Trend",
                "🔙 Back to Main Menu"
        };

        String choice = (String) JOptionPane.showInputDialog(null,
                "Select a chart to view:",
                "Charts Menu",
                JOptionPane.QUESTION_MESSAGE,
                null,
                chartOptions,
                chartOptions[0]);

        if (choice == null || choice.contains("Back")) return;

        String chart = "";

        if (choice.contains("Category")) {
            Map<String, Double> categoryTotals = repository.getTotalByCategory();
            chart = ChartGenerator.generateCategoryChart(categoryTotals);
        } else if (choice.contains("Monthly")) {
            Map<String, Double> monthlyTotals = repository.getTotalByMonth();
            chart = ChartGenerator.generateMonthlyChart(monthlyTotals);
        } else if (choice.contains("Percentage")) {
            Map<String, Double> categoryTotals = repository.getTotalByCategory();
            chart = ChartGenerator.generatePieChart(categoryTotals);
        } else if (choice.contains("Weekly")) {
            Map<Integer, Double> weeklyTotals = repository.getTotalByWeek();
            chart = ChartGenerator.generateWeeklyChart(weeklyTotals);
        }

        JTextArea textArea = new JTextArea(chart);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(null, scrollPane, "Chart View",
                JOptionPane.PLAIN_MESSAGE);
    }

    private void viewSummary() {
        Map<String, Object> stats = repository.getStatistics();
        String summary = ChartGenerator.generateSummaryBox(stats);

        JTextArea textArea = new JTextArea(summary);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JOptionPane.showMessageDialog(null, textArea, "Summary Statistics",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void manageBudgets() {
        String[] budgetOptions = {
                "Set Budget",
                "View Budget Status",
                "Back"
        };

        String choice = (String) JOptionPane.showInputDialog(null,
                "Budget Management:",
                "Manage Budgets",
                JOptionPane.QUESTION_MESSAGE,
                null,
                budgetOptions,
                budgetOptions[0]);

        if (choice == null || choice.equals("Back")) return;

        if (choice.equals("Set Budget")) {
            setBudget();
        } else if (choice.equals("View Budget Status")) {
            viewBudgetStatus();
        }
    }

    private void setBudget() {
        JComboBox<String> categoryBox = new JComboBox<>(ExpenseCategory.getAllCategoryNames());
        JTextField amountField = new JTextField();

        Object[] fields = {
                "Category:", categoryBox,
                "Budget Amount ($):", amountField
        };

        int result = JOptionPane.showConfirmDialog(null, fields, "Set Budget",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String categoryDisplay = (String) categoryBox.getSelectedItem();
                String category = ExpenseCategory.getCategoryNameFromDisplay(categoryDisplay);
                double amount = Double.parseDouble(amountField.getText());

                repository.setBudget(category, amount);

                JOptionPane.showMessageDialog(null,
                        String.format("✓ Budget set successfully!\n\n%s: $%.2f", category, amount),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid amount format.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewBudgetStatus() {
        Map<String, Map<String, Double>> status = repository.getBudgetStatus();

        if (status.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No budgets set yet.",
                    "Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    BUDGET STATUS                             ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════╝\n\n");

        for (Map.Entry<String, Map<String, Double>> entry : status.entrySet()) {
            String category = entry.getKey();
            Map<String, Double> details = entry.getValue();

            double budget = details.get("budget");
            double spent = details.get("spent");
            double remaining = details.get("remaining");
            double percentage = details.get("percentage");

            String emoji = ExpenseCategory.fromString(category).getEmoji();
            String status_icon = remaining >= 0 ? "✓" : "⚠️";

            sb.append(String.format("%s %s %s\n", status_icon, emoji, category));
            sb.append(String.format("   Budget:    $%,.2f\n", budget));
            sb.append(String.format("   Spent:     $%,.2f (%.1f%%)\n", spent, percentage));
            sb.append(String.format("   Remaining: $%,.2f\n\n", remaining));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(null, scrollPane, "Budget Status",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportData() {
        String[] exportOptions = {
                "Export to expenses.txt",
                "Export to CSV",
                "Cancel"
        };

        String choice = (String) JOptionPane.showInputDialog(null,
                "Select export format:",
                "Export Data",
                JOptionPane.QUESTION_MESSAGE,
                null,
                exportOptions,
                exportOptions[0]);

        if (choice == null || choice.equals("Cancel")) return;

        if (choice.contains("CSV")) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("expenses_export.csv"));

            int result = fileChooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                if (repository.exportToCSV(fileChooser.getSelectedFile().getAbsolutePath())) {
                    JOptionPane.showMessageDialog(null,
                            "✓ Data exported successfully!\n\n" +
                                    "Location: " + fileChooser.getSelectedFile().getAbsolutePath(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "✓ Data is automatically saved to expenses.txt",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewMonthComparison() {
        double thisMonth = repository.calculateCurrentMonthTotal();
        double lastMonth = repository.calculateLastMonthTotal();

        String chart = ChartGenerator.generateComparisonChart(thisMonth, lastMonth);

        JTextArea textArea = new JTextArea(chart);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JOptionPane.showMessageDialog(null, textArea, "Month Comparison",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to exit?\n\nAll data is saved to expenses.txt",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        return confirm != JOptionPane.YES_OPTION;
    }

    private void showGoodbye() {
        JOptionPane.showMessageDialog(null,
                "Thank you for using Expense Tracker!\n\n" +
                        "Your expenses are safely saved.\n" +
                        "Track smart, spend wisely! 💰",
                APP_TITLE,
                JOptionPane.INFORMATION_MESSAGE);
    }
}