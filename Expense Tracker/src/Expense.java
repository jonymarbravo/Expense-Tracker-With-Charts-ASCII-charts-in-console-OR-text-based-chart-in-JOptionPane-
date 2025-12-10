import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * Expense Model Class
 * Represents a single expense entry with validation
 */
public class Expense implements Serializable, Comparable<Expense> {
    private static final long serialVersionUID = 1L;

    private String id;
    private String description;
    private double amount;
    private String category;
    private LocalDate date;
    private String paymentMethod;
    private String notes;

    /**
     * Constructor for new expense
     */
    public Expense(String description, double amount, String category, LocalDate date,
                   String paymentMethod, String notes) {
        this.id = UUID.randomUUID().toString();
        setDescription(description);
        setAmount(amount);
        setCategory(category);
        setDate(date);
        setPaymentMethod(paymentMethod);
        setNotes(notes);
    }

    /**
     * Constructor for loading from file
     */
    public Expense(String id, String description, double amount, String category,
                   String date, String paymentMethod, String notes) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    // Setters with validation
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (description.trim().length() < 3) {
            throw new IllegalArgumentException("Description must be at least 3 characters");
        }
        this.description = description.trim();
    }

    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (amount > 1000000) {
            throw new IllegalArgumentException("Amount exceeds maximum limit (1,000,000)");
        }
        this.amount = amount;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        this.category = category.trim();
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }
        this.date = date;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = (paymentMethod == null || paymentMethod.trim().isEmpty())
                ? "Cash" : paymentMethod.trim();
    }

    public void setNotes(String notes) {
        this.notes = (notes == null) ? "" : notes.trim();
    }

    /**
     * Get year-month string (for grouping)
     */
    public String getYearMonth() {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    /**
     * Get week number
     */
    public int getWeekOfYear() {
        return date.getDayOfYear() / 7 + 1;
    }

    /**
     * Convert to file format
     */
    public String toFileFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return String.format("%s|%s|%.2f|%s|%s|%s|%s",
                id,
                description,
                amount,
                category,
                date.format(formatter),
                paymentMethod,
                notes.replace("|", "⎮")
        );
    }

    /**
     * Create expense from file format
     */
    public static Expense fromFileFormat(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid file line");
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid file format - expected 7 fields, got " + parts.length);
        }

        try {
            return new Expense(
                    parts[0], // id
                    parts[1], // description
                    Double.parseDouble(parts[2]), // amount
                    parts[3], // category
                    parts[4], // date
                    parts[5], // paymentMethod
                    parts[6].replace("⎮", "|") // notes
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + parts[2]);
        }
    }

    /**
     * Get formatted display string
     */
    public String getDisplayString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return String.format("%-12s | %-20s | $%-10.2f | %-15s | %s",
                date.format(formatter),
                truncate(description, 20),
                amount,
                category,
                paymentMethod
        );
    }

    /**
     * Get detailed info
     */
    public String getDetailedInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        return String.format(
                "ID: %s\n" +
                        "Description: %s\n" +
                        "Amount: $%.2f\n" +
                        "Category: %s\n" +
                        "Date: %s\n" +
                        "Payment Method: %s\n" +
                        "Notes: %s",
                id.substring(0, 8) + "...",
                description,
                amount,
                category,
                date.format(formatter),
                paymentMethod,
                notes.isEmpty() ? "None" : notes
        );
    }

    /**
     * Get compact display for lists
     */
    public String getCompactDisplay() {
        return String.format("$%.2f - %s (%s)", amount, description, category);
    }

    /**
     * Truncate string to specified length
     */
    private String truncate(String str, int length) {
        if (str.length() <= length) {
            return str;
        }
        return str.substring(0, length - 3) + "...";
    }

    @Override
    public String toString() {
        return getCompactDisplay();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Expense expense = (Expense) obj;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Expense other) {
        // Sort by date (newest first), then by amount (highest first)
        int dateCompare = other.date.compareTo(this.date);
        if (dateCompare != 0) {
            return dateCompare;
        }
        return Double.compare(other.amount, this.amount);
    }
}