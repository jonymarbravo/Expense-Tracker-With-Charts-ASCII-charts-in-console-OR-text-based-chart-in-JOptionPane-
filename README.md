# 💰 Expense Tracker

A comprehensive expense management system with ASCII chart visualizations, budget tracking, and detailed analytics. Built with pure Java and no external frameworks - demonstrating strong data structures, file I/O, and creative visualization skills.

## 📋 Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [How to Run](#how-to-run)
- [Usage Guide](#usage-guide)
- [Project Structure](#project-structure)
- [Chart Visualizations](#chart-visualizations)
- [File Structure](#file-structure)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### Core Functionality
- ➕ **Add Expenses** - Track expenses with description, amount, category, date, payment method, and notes
- 📋 **View All Expenses** - Browse all expenses sorted by date and amount
- 📁 **Category Organization** - 11 predefined categories with emoji icons
- ✏️ **Update Expenses** - Modify existing expense entries
- 🗑️ **Delete Expenses** - Remove entries with confirmation
- 🔍 **Smart Search** - Find expenses by description, category, or notes

### Advanced Features
- 📊 **ASCII Chart Visualizations** - Beautiful text-based charts
  - Category bar chart
  - Monthly trend chart
  - Percentage distribution (pie chart)
  - Weekly expense trend
  - Month-to-month comparison
- 💵 **Budget Management** - Set budgets per category and track spending
- ⚠️ **Budget Alerts** - Automatic warnings when over budget
- 📈 **Comprehensive Analytics** - View statistics and summaries
- 💾 **Export Functionality** - Export to CSV or TXT format
- 🔄 **Month Comparison** - Compare current month vs last month
- 📅 **Date Range Filtering** - View expenses by week, month, or custom range

### User Experience
- ✅ **Form Validation Loops** - Stay on form until input is correct
- 🎯 **Smart Error Messages** - Clear guidance on fixing errors
- 📊 **Real-time Statistics** - See totals and counts in menus
- 💡 **Payment Method Tracking** - Track how you pay (Cash, Card, etc.)
- 📝 **Notes Field** - Add context to each expense
- 🗓️ **Date Validation** - Prevents future dates and invalid formats

## 🛠️ Technologies Used

- **Language**: Java (JDK 8 or higher)
- **GUI**: Java Swing (JOptionPane)
- **Data Structures**: ArrayList, HashMap, TreeMap
- **File I/O**: Java NIO (BufferedReader/Writer, ObjectStreams)
- **Date/Time**: java.time (LocalDate, YearMonth)
- **Serialization**: Java Object Serialization for budget storage
- **Architecture**: Repository Pattern with clean separation

## 💻 System Requirements

- **Java Development Kit (JDK)**: Version 8 or higher
- **IDE**: IntelliJ IDEA (recommended) or any Java IDE
- **Operating System**: Windows, macOS, or Linux
- **Memory**: Minimum 2GB RAM
- **Storage**: 10MB free space

## 📥 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/jonymarbravo/expense-tracker.git
   cd expense-tracker
   ```

2. **Open in IntelliJ IDEA**
   - Launch IntelliJ IDEA
   - Select `File > Open`
   - Navigate to the cloned project directory
   - Click `OK`

3. **Verify Project Structure**
   ```
   expense-tracker/
   ├── src/
   │   ├── Expense.java
   │   ├── ExpenseCategory.java
   │   ├── ChartGenerator.java
   │   ├── ExpenseRepository.java
   │   └── ExpenseTracker.java
   ├── expenses.txt (auto-generated)
   ├── expenses_backup.txt (auto-generated)
   ├── budget.dat (auto-generated)
   └── README.md
   ```

## 🚀 How to Run

### Using IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. Navigate to `src/ExpenseTracker.java`
3. Right-click on the file and select `Run 'ExpenseTracker.main()'`
4. Or click the green play button (▶️) next to the `main` method

### Using Command Line

```bash
# Navigate to src directory
cd src

# Compile all Java files
javac *.java

# Run the main class
java ExpenseTracker
```

## 📖 Usage Guide

### Adding an Expense

1. Select option `1` from the main menu
2. Fill in the required fields:
   - **Description**: What you spent on (e.g., "Lunch at restaurant", "Gas")
   - **Amount**: How much you spent (must be greater than 0)
   - **Category**: Choose from 11 categories (Food, Transport, Bills, etc.)
   - **Date**: When you spent it (format: YYYY-MM-DD)
   - **Payment Method**: How you paid (Cash, Credit Card, etc.)
   - **Notes**: Optional additional information
3. Click OK to save
4. System shows confirmation and warns if over budget

### Viewing Expenses

**All Expenses** (Option 2):
- View all expenses sorted by date (newest first)
- Select any expense to see full details

**By Category** (Option 3):
- View expenses grouped by category
- See total spending per category

### Updating an Expense

1. Select option `4` from the main menu
2. Choose the expense to update
3. Modify any fields
4. Click OK to save changes

### Searching Expenses

1. Select option `6` from the main menu
2. Enter search term (searches in description, category, and notes)
3. View matching results
4. Select an expense for details

### Viewing Charts

Select option `7` and choose from:

1. **Category Bar Chart** - Horizontal bars showing spending by category
   ```
   🍔 Food         | ████████████ $1,200.00
   🚗 Transport    | ██████       $600.00
   💡 Bills        | ██████████   $900.00
   ```

2. **Monthly Trend** - See spending over multiple months
3. **Percentage Distribution** - Pie chart showing category percentages
4. **Weekly Trend** - Week-by-week spending analysis
5. **Month Comparison** - Current vs previous month with change percentage

### Managing Budgets

Select option `9` for budget management:

**Set Budget**:
1. Choose a category
2. Enter budget amount
3. System tracks spending vs budget

**View Budget Status**:
- See all category budgets
- View spent amount and percentage
- See remaining budget
- Get warnings for over-budget categories

### Viewing Statistics

Select option `8` to see:
- Total expenses
- Number of entries
- Average expense
- Highest/lowest expense
- Most used category
- Most expensive day

### Exporting Data

Select option `10` to export:
- **To CSV**: Choose location and save as spreadsheet
- **To TXT**: Automatically saved to expenses.txt

## 📁 Project Structure

### Expense.java
- **Purpose**: Model class representing a single expense
- **Responsibilities**:
  - Store expense data with validation
  - Date and time handling
  - File format conversion
  - Sorting and comparison

### ExpenseCategory.java
- **Purpose**: Enum for predefined expense categories
- **Responsibilities**:
  - Define 11 standard categories
  - Provide emoji icons for visual appeal
  - Category name conversion

### ChartGenerator.java
- **Purpose**: ASCII chart generation engine
- **Responsibilities**:
  - Generate bar charts (horizontal)
  - Create pie charts (percentage-based)
  - Monthly/weekly trend charts
  - Comparison charts
  - Summary statistics boxes

### ExpenseRepository.java
- **Purpose**: Data persistence and business logic
- **Responsibilities**:
  - File I/O operations (TXT and DAT files)
  - CRUD operations
  - Budget management
  - Search and filtering
  - Date range queries
  - Statistics calculation
  - CSV export

### ExpenseTracker.java
- **Purpose**: Main application with user interface
- **Responsibilities**:
  - JOptionPane-based UI
  - Menu navigation
  - Form validation loops
  - User interaction handling
  - Chart display

## 📊 Chart Visualizations

### Example Charts

**Category Bar Chart:**
```
╔══════════════════════════════════════════════════════════════════════╗
║                    EXPENSES BY CATEGORY                              ║
╚══════════════════════════════════════════════════════════════════════╝

🍔 Food          | ████████████████████████████ $1,245.50
🚗 Transport     | ████████████ $520.00
💡 Bills         | ████████████████████ $850.00
🛍️ Shopping      | ████████ $320.75
```

**Monthly Trend:**
```
╔══════════════════════════════════════════════════════════════════════╗
║                    MONTHLY EXPENSE TREND                             ║
╚══════════════════════════════════════════════════════════════════════╝

Oct 2024     | ███████████████████████ $2,150.00
Nov 2024     | ████████████████████████████ $2,450.00
Dec 2024     | ██████████████████ $1,875.00
```

**Percentage Distribution:**
```
╔══════════════════════════════════════════════════════════════════════╗
║                  EXPENSE DISTRIBUTION (%)                            ║
╚══════════════════════════════════════════════════════════════════════╝

🍔 Food          | ████████████████████ 42.5% ($1,245.50)
💡 Bills         | ███████████████ 29.0% ($850.00)
🚗 Transport     | ██████████ 17.8% ($520.00)
```

## 📂 File Structure

### expenses.txt
- **Purpose**: Main expense database
- **Format**: Pipe-delimited text file
- **Fields**: ID|Description|Amount|Category|Date|PaymentMethod|Notes
- **Backup**: Automatically backed up before every save

### expenses_backup.txt
- **Purpose**: Automatic backup
- **Created**: Before every save operation
- **Use**: Recovery in case of file corruption

### budget.dat
- **Purpose**: Budget limits storage
- **Format**: Serialized Java HashMap
- **Content**: Category -> Budget amount mappings

## 🎯 Why This Project Impresses Companies

### Demonstrates Core Skills
- ✅ Data structure mastery (Lists, Maps, Sets)
- ✅ File I/O expertise (BufferedReader/Writer, Serialization)
- ✅ Date/time handling (java.time API)
- ✅ Algorithm implementation (sorting, filtering, aggregation)

### Shows Creativity
- ✅ ASCII chart generation without libraries
- ✅ Visual data representation in console
- ✅ User-friendly text-based interface
- ✅ Emoji usage for better UX

### Professional Development
- ✅ Clean architecture (Repository pattern)
- ✅ Comprehensive error handling
- ✅ Input validation
- ✅ Data backup strategies
- ✅ Export functionality

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Jony Mar Barrete**
- GitHub: [@jonymarbravo](https://github.com/jonymarbravo)
- Email: jonymarbarrete88@gmail.com

## 🙏 Acknowledgments

- Built to demonstrate Java fundamentals and data visualization
- Inspired by budgeting apps like Mint, YNAB, and PocketGuard
- Created for educational and portfolio purposes

## 📞 Support

For support, email jonymarbarrete88@gmail.com or open an issue in the GitHub repository.

---

⭐ **If you find this project helpful, please give it a star!** ⭐

## 💡 Tips for Better Expense Tracking

- **Be Consistent**: Log expenses daily
- **Use Categories**: Keep your spending organized
- **Set Budgets**: Know your limits
- **Review Monthly**: Check your spending patterns
- **Export Regularly**: Keep backups of your data

---

**Made with ❤️ and ☕ by [Jony Mar Barrete](https://github.com/jonymarbravo)**
