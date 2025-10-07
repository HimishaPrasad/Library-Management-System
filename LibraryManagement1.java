import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root"; 
    private static final String PASS = "root"; 

    private static Connection conn;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println(" Connected to Library Database!");
            mainMenu();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===================== MAIN MENU =====================
    private static void mainMenu() throws SQLException {
        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Librarian Section");
            System.out.println("2. Student Section");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    librarianLogin();
                    break;

                case 2:
                    studentSection();
                    break;

                case 3:
                    System.out.println("Exiting... Goodbye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println(" Invalid choice! Please try again.");
                    break;
            }
        }
    }

    // ===================== LIBRARIAN SECTION =====================
    private static void librarianLogin() throws SQLException {
        System.out.print("\nEnter Librarian Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM librarian WHERE username='' AND password=?");
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("\n Welcome Librarian: " + username);
            librarianMenu();
        } else {
            System.out.println(" Invalid Credentials!");
        }
    }

    private static void librarianMenu() throws SQLException {
        while (true) {
            System.out.println("\n===== LIBRARIAN MENU =====");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. View All Students");
            System.out.println("4. Issue Book to Student");
            System.out.println("5. Return Book");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addBook();
                    break;

                case 2:
                    viewBooks();
                    break;

                case 3:
                    viewStudents();
                    break;

                case 4:
                    issueBookByLibrarian();
                    break;

                case 5:
                    returnBook();
                    break;

                case 6:
                    System.out.println("Logging out...");
                    return;

                default:
                    System.out.println(" Invalid choice! Try again.");
                    break;
            }
        }
    }

    private static void addBook() throws SQLException {
        System.out.print("Enter Book Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Author Name: ");
        String author = sc.nextLine();
        System.out.print("Enter Quantity: ");
        int qty = sc.nextInt();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO books (title, author, quantity) VALUES (?, ?, ?)");
        ps.setString(1, title);
        ps.setString(2, author);
        ps.setInt(3, qty);
        ps.executeUpdate();

        System.out.println(" Book added successfully!");
    }

    private static void viewBooks() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM books");

        System.out.println("\n===== BOOK LIST =====");
        System.out.printf("%-5s %-30s %-20s %-10s%n", "ID", "Title", "Author", "Qty");
        System.out.println("-------------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-5d %-30s %-20s %-10d%n",
                    rs.getInt("id"), rs.getString("title"),
                    rs.getString("author"), rs.getInt("quantity"));
        }
    }

    private static void viewStudents() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM students");

        System.out.println("\n===== STUDENT LIST =====");
        System.out.printf("%-5s %-20s %-20s%n", "ID", "Name", "Username");
        System.out.println("---------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-5d %-20s %-20s%n",
                    rs.getInt("id"), rs.getString("name"), rs.getString("username"));
        }
    }

    private static void issueBookByLibrarian() throws SQLException {
        System.out.print("Enter Student ID: ");
        int studentId = sc.nextInt();
        System.out.print("Enter Book ID: ");
        int bookId = sc.nextInt();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE id=? AND quantity>0");
        ps.setInt(1, bookId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            PreparedStatement issue = conn.prepareStatement(
                    "INSERT INTO issued_books (student_id, book_id, issue_date) VALUES (?, ?, CURDATE())");
            issue.setInt(1, studentId);
            issue.setInt(2, bookId);
            issue.executeUpdate();

            PreparedStatement update = conn.prepareStatement("UPDATE books SET quantity = quantity - 1 WHERE id=?");
            update.setInt(1, bookId);
            update.executeUpdate();

            System.out.println(" Book issued successfully!");
        } else {
            System.out.println(" Book not available or invalid Book ID!");
        }
    }

    private static void returnBook() throws SQLException {
        System.out.print("Enter Issue ID: ");
        int issueId = sc.nextInt();

        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM issued_books WHERE issue_id=? AND return_date IS NULL");
        ps.setInt(1, issueId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int bookId = rs.getInt("book_id");

            PreparedStatement updateReturn = conn.prepareStatement(
                    "UPDATE issued_books SET return_date=CURDATE() WHERE issue_id=?");
            updateReturn.setInt(1, issueId);
            updateReturn.executeUpdate();

            PreparedStatement updateQty = conn.prepareStatement(
                    "UPDATE books SET quantity = quantity + 1 WHERE id=?");
            updateQty.setInt(1, bookId);
            updateQty.executeUpdate();

            System.out.println(" Book returned successfully!");
        } else {
            System.out.println(" Invalid Issue ID or Book already returned!");
        }
    }

    // ===================== STUDENT SECTION =====================
    private static void studentSection() throws SQLException {
        while (true) {
            System.out.println("\n===== STUDENT SECTION =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    registerStudent();
                    break;

                case 2:
                    studentLogin();
                    break;

                case 3:
                    return;

                default:
                    System.out.println(" Invalid choice! Try again.");
                    break;
            }
        }
    }

    private static void registerStudent() throws SQLException {
        System.out.print("Enter Full Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO students (name, username, password) VALUES (?, ?, ?)");
        ps.setString(1, name);
        ps.setString(2, username);
        ps.setString(3, password);
        ps.executeUpdate();

        System.out.println(" Registration successful!");
    }

    private static void studentLogin() throws SQLException {
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM students WHERE username=? AND password=?");
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int studentId = rs.getInt("id");
            System.out.println("\n Welcome " + rs.getString("name") + "!");
            studentMenu(studentId);
        } else {
            System.out.println(" Invalid Credentials!");
        }
    }

    private static void studentMenu(int studentId) throws SQLException {
        while (true) {
            System.out.println("\n===== STUDENT MENU =====");
            System.out.println("1. View Available Books");
            System.out.println("2. View My Issued Books");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    viewBooks();
                    break;

                case 2:
                    viewIssuedBooks(studentId);
                    break;

                case 3:
                    System.out.println("Logging out...");
                    return;

                default:
                    System.out.println(" Invalid choice! Try again.");
                    break;
            }
        }
    }

    private static void viewIssuedBooks(int studentId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT i.issue_id, b.title, b.author, i.issue_date, i.return_date " +
                "FROM issued_books i JOIN books b ON i.book_id = b.id WHERE i.student_id=?");
        ps.setInt(1, studentId);
        ResultSet rs = ps.executeQuery();

        System.out.println("\n===== YOUR ISSUED BOOKS =====");
        System.out.printf("%-8s %-30s %-20s %-15s %-15s%n", "IssueID", "Title", "Author", "Issued", "Returned");
        System.out.println("--------------------------------------------------------------------------");

        while (rs.next()) {
            System.out.printf("%-8d %-30s %-20s %-15s %-15s%n",
                    rs.getInt("issue_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDate("issue_date"),
                    rs.getDate("return_date"));
        }
    }
}
