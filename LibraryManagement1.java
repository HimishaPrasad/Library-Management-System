import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/librarydb";
    private static final String USER = "root"; 
    private static final String PASSWORD = "root"; 

    private static Connection conn;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" Connected to Database Successfully!");

            while (true) {
                System.out.println("\n======= LIBRARY MANAGEMENT SYSTEM =======");
                System.out.println("1. Librarian Section");
                System.out.println("2. Student Section");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1 : 
                        librarianMenu();
                        break;
                    case 2 : 
                        studentMenu(); 
                        break;
                    case 3 : {
                        System.out.println("Exiting... Thank you!");
                        conn.close();
                        System.exit(0);
                    }
                    default :
                        System.out.println(" Invalid choice. Try again!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    // ================= Librarian Section =================
    private static void librarianMenu() {
        while (true) {
            System.out.println("\n----- LIBRARIAN SECTION -----");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 :
                    registerLibrarian();
                    break;
                case 2 : {
                    if (loginLibrarian()) librarianFunctions();
                }
                case 3 :
                     return; 
                default : 
                    System.out.println(" Invalid choice!");
            }
        }
    }

    private static void registerLibrarian() {
        try {
            System.out.print("Enter Name: ");
            String name = sc.next();
            System.out.print("Enter Email: ");
            String email = sc.next();
            System.out.print("Enter Password: ");
            String pass = sc.next();

            PreparedStatement ps = conn.prepareStatement("INSERT INTO librarians(name,email,password) VALUES(?,?,?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.executeUpdate();

            System.out.println(" Librarian registered successfully!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static boolean loginLibrarian() {
        try {
            System.out.print("Enter Email: ");
            String email = sc.next();
            System.out.print("Enter Password: ");
            String pass = sc.next();

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM librarians WHERE email=? AND password=?");
            ps.setString(1, email);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println(" Login successful! Welcome, " + rs.getString("name"));
                return true;
            } else {
                System.out.println("Invalid credentials!");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    private static void librarianFunctions() {
        while (true) {
            System.out.println("\n--- LIBRARIAN FUNCTIONS ---");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Edit Book");
            System.out.println("4. Delete Book");
            System.out.println("5. View Issued Books");
            System.out.println("6. Back");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 : 
                    addBook();
                    break;
                case 2 : 
                    viewBooks();
                    break;
                case 3 : 
                    editBook();
                    break;
                case 4 : 
                    deleteBook();
                    break;
                case 5 : 
                    viewIssuedBooks();
                    break;
                case 6 :  return; 
                default :
                    System.out.println(" Invalid choice!");
            }
        }
    }

    private static void addBook() {
        try {
            sc.nextLine(); 
            System.out.print("Enter Book Title: ");
            String title = sc.nextLine();
            System.out.print("Enter Author: ");
            String author = sc.nextLine();

            PreparedStatement ps = conn.prepareStatement("INSERT INTO books(title,author) VALUES(?,?)");
            ps.setString(1, title);
            ps.setString(2, author);
            ps.executeUpdate();

            System.out.println(" Book added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    private static void viewBooks() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM books");

            System.out.println("\n BOOK LIST:");
            System.out.println("ID\tTitle\tAuthor\tAvailable");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("title") + "\t" +
                        rs.getString("author") + "\t" +
                        rs.getBoolean("available"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching books: " + e.getMessage());
        }
    }

    private static void editBook() {
        try {
            viewBooks();
            System.out.print("Enter Book ID to edit: ");
            int id = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter new Title: ");
            String title = sc.nextLine();
            System.out.print("Enter new Author: ");
            String author = sc.nextLine();

            PreparedStatement ps = conn.prepareStatement("UPDATE books SET title=?, author=? WHERE id=?");
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, id);
            int updated = ps.executeUpdate();

            if (updated > 0)
                System.out.println(" Book updated successfully!");
            else
                System.out.println(" Book not found!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteBook() {
        try {
            viewBooks();
            System.out.print("Enter Book ID to delete: ");
            int id = sc.nextInt();

            PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id=?");
            ps.setInt(1, id);
            int deleted = ps.executeUpdate();

            if (deleted > 0)
                System.out.println(" Book deleted successfully!");
            else
                System.out.println(" Book not found!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewIssuedBooks() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("""
                    SELECT i.id, s.name AS student, b.title AS book, i.issue_date, i.return_date
                    FROM issued_books i
                    JOIN students s ON i.student_id = s.id
                    JOIN books b ON i.book_id = b.id
                    """);

            System.out.println("\n ISSUED BOOKS:");
            System.out.println("ID\tStudent\tBook\tIssued On\tReturned On");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("student") + "\t" +
                        rs.getString("book") + "\t" +
                        rs.getString("issue_date") + "\t" +
                        rs.getString("return_date"));
            }
        } catch (SQLException e) {
            System.out.println("Error viewing issued books: " + e.getMessage());
        }
    }

    // ================= Student Section =================
    private static void studentMenu() {
        while (true) {
            System.out.println("\n----- STUDENT SECTION -----");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 :
                    registerStudent();
                    break;
                case 2 : {
                    int studentId = loginStudent();
                    if (studentId != -1) studentFunctions(studentId);
                }
                case 3 :  
                    return; 
                default :
                    System.out.println(" Invalid choice!");
            }
        }
    }

    private static void registerStudent() {
        try {
            System.out.print("Enter Name: ");
            String name = sc.next();
            System.out.print("Enter Email: ");
            String email = sc.next();
            System.out.print("Enter Password: ");
            String pass = sc.next();

            PreparedStatement ps = conn.prepareStatement("INSERT INTO students(name,email,password) VALUES(?,?,?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.executeUpdate();

            System.out.println(" Student registered successfully!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static int loginStudent() {
        try {
            System.out.print("Enter Email: ");
            String email = sc.next();
            System.out.print("Enter Password: ");
            String pass = sc.next();

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM students WHERE email=? AND password=?");
            ps.setString(1, email);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println(" Login successful! Welcome, " + rs.getString("name"));
                return rs.getInt("id");
            } else {
                System.out.println(" Invalid credentials!");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }
    }

    private static void studentFunctions(int studentId) {
        while (true) {
            System.out.println("\n--- STUDENT FUNCTIONS ---");
            System.out.println("1. View Books");
            System.out.println("2. Issue Book");
            System.out.println("3. Return Book");
            System.out.println("4. Back");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 : 
                    viewBooks();
                    break;
                case 2 : 
                    issueBook(studentId);
                    break;
                case 3 : 
                    returnBook(studentId);
                    break;
                case 4 : 
                    return; 
                default :
                    System.out.println(" Invalid choice!");
            }
        }
    }

    private static void issueBook(int studentId) {
        try {
            viewBooks();
            System.out.print("Enter Book ID to issue: ");
            int bookId = sc.nextInt();

            PreparedStatement check = conn.prepareStatement("SELECT available FROM books WHERE id=?");
            check.setInt(1, bookId);
            ResultSet rs = check.executeQuery();

            if (rs.next() && rs.getBoolean("available")) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO issued_books(student_id, book_id) VALUES(?, ?)");
                ps.setInt(1, studentId);
                ps.setInt(2, bookId);
                ps.executeUpdate();

                PreparedStatement update = conn.prepareStatement("UPDATE books SET available=FALSE WHERE id=?");
                update.setInt(1, bookId);
                update.executeUpdate();

                System.out.println(" Book issued successfully!");
            } else {
                System.out.println(" Book not available!");
            }
        } catch (SQLException e) {
            System.out.println("Error issuing book: " + e.getMessage());
        }
    }

    private static void returnBook(int studentId) {
        try {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT i.id, b.title FROM issued_books i
                    JOIN books b ON i.book_id = b.id
                    WHERE i.student_id=? AND i.return_date IS NULL
                    """);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nIssued Books:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " - " + rs.getString("title"));
            }

            System.out.print("Enter Issue ID to return: ");
            int issueId = sc.nextInt();

            PreparedStatement updateIssue = conn.prepareStatement("UPDATE issued_books SET return_date=NOW() WHERE id=?");
            updateIssue.setInt(1, issueId);
            int updated = updateIssue.executeUpdate();

            if (updated > 0) {
                PreparedStatement updateBook = conn.prepareStatement("""
                        UPDATE books SET available=TRUE WHERE id=(SELECT book_id FROM issued_books WHERE id=?)
                        """);
                updateBook.setInt(1, issueId);
                updateBook.executeUpdate();

                System.out.println(" Book returned successfully!");
            } else {
                System.out.println("Invalid Issue ID!");
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }
}

