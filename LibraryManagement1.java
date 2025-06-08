
    
import java.util.Scanner;

public class LibraryManagement1 {
    static String[] studentIds = new String[10];
    static String[] studentPasswords = new String[10];

    static String[] staffIds = new String[10];
    static String[] staffPasswords = new String[10];

    static String[] bookTitles = new String[100];
    static String[] bookAuthors = new String[100];
    static boolean[] isIssued = new boolean[100];

    static Scanner sc = new Scanner(System.in);
    
    // Main Method
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Library Management System ---");
            System.out.println("1. Register as Student");
            System.out.println("2. Register as Staff");
            System.out.println("3. Login as Student");
            System.out.println("4. Login as Staff");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    registerStudent();
                    break;
                case 2:
                    registerStaff();
                    break;
                case 3:
                    if (loginUser(studentIds, studentPasswords)) {
                        studentMenu();
                    }
                    break;
                case 4:
                    if (loginUser(staffIds, staffPasswords)) {
                        staffMenu();
                    }
                    break;
                case 5:
                    System.out.println("Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    // Method to Register Student
    static void registerStudent() {
        boolean added = false;
        System.out.print("Enter new student ID: ");
        String id = sc.nextLine();

        if (isUsernameTaken(studentIds, id)) {
            System.out.println("Student ID already exists. Try a different ID.");
            return;
        }

        System.out.print("Enter password (word part): ");
        String word = sc.nextLine();
        System.out.print("Enter password (number part): ");
        int num = sc.nextInt();
        sc.nextLine();
        String pass = word + num;

        for (int i = 0; i < studentIds.length; i++) {
            if (studentIds[i] == null) {
                studentIds[i] = id;
                studentPasswords[i] = pass;
                added = true;
                System.out.println("Student registered successfully.");
                break;
            }
        }

        if (!added) {
            System.out.println("Student registration limit reached.");
        }
    }
    
    // Method to Register Staff

    static void registerStaff() {
        boolean added = false;
        System.out.print("Enter new staff ID: ");
        String id = sc.nextLine();

        if (isUsernameTaken(staffIds, id)) {
            System.out.println("Staff ID already exists. Try a different ID.");
            return;
        }

        System.out.print("Enter password (word part): ");
        String word = sc.nextLine();
        System.out.print("Enter password (number part): ");
        int num = sc.nextInt();
        sc.nextLine();
        String pass = word + num;

        for (int i = 0; i < staffIds.length; i++) {
            if (staffIds[i] == null) {
                staffIds[i] = id;
                staffPasswords[i] = pass;
                added = true;
                System.out.println("Staff registered successfully.");
                break;
            }
        }

        if (!added) {
            System.out.println("Staff registration limit reached.");
        }
    }

    static boolean isUsernameTaken(String[] ids, String id) {
        for (String existingId : ids) {
            if (id.equals(existingId)) {
                return true;
            }
        }
        return false;
    }

    // Method to login 
    
    static boolean loginUser(String[] ids, String[] passwords) {
        System.out.print("Enter user ID: ");
        String id = sc.nextLine();
        System.out.print("Enter password: ");
        String pass = sc.nextLine();

        for (int i = 0; i < ids.length; i++) {
            if (ids[i] != null && ids[i].equals(id) && passwords[i].equals(pass)) {
                System.out.println("Login successful.");
                return true;
            }
        }
        System.out.println("Invalid credentials.");
        return false;
    }

    // Method to enter Student Menu
    
    static void studentMenu() {
        while (true) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. View Books");
            System.out.println("2. Issue Book");
            System.out.println("3. Return Book");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewBooks();
                    break;
                case 2:
                    issueBook();
                    break;
                case 3:
                    returnBook();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // Method to enter Staff menu

    static void staffMenu() {
        while (true) {
            System.out.println("\n--- Staff Menu ---");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. View Issued Books");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");
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
                    viewIssuedBooks();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
 
    // Method to add book

    static void addBook() {
        boolean added = false;
        for (int i = 0; i < bookTitles.length; i++) {
            if (bookTitles[i] == null) {
                System.out.print("Enter book title: ");
                String title = sc.nextLine();
                System.out.print("Enter author name: ");
                String author = sc.nextLine();
                bookTitles[i] = title;
                bookAuthors[i] = author;
                isIssued[i] = false;
                added = true;
                System.out.println("Book added successfully at ID: " + i);
                break;
            }
        }
        if (!added) {
            System.out.println("Book limit reached.");
        }
    }

    // Method to view book

    static void viewBooks() {
        boolean found = false;
        for (int i = 0; i < bookTitles.length; i++) {
            if (bookTitles[i] != null) {
                found = true;
                System.out.println("ID: " + i);
                System.out.println("Title: " + bookTitles[i]);
                System.out.println("Author: " + bookAuthors[i]);
                System.out.println("Status: " + (isIssued[i] ? "Issued" : "Available"));
                System.out.println("---------------------");
            }
        }
        if (!found) {
            System.out.println("No books available.");
        }
    }

    // Method to view issued books

    static void viewIssuedBooks() {
        boolean found = false;
        for (int i = 0; i < bookTitles.length; i++) {
            if (bookTitles[i] != null && isIssued[i]) {
                found = true;
                System.out.println("ID: " + i);
                System.out.println("Title: " + bookTitles[i]);
                System.out.println("Author: " + bookAuthors[i]);
                System.out.println("Status: Issued");
                System.out.println("---------------------");
            }
        }
        if (!found) {
            System.out.println("No books are currently issued.");
        }
    }

    // Method to issue book

    static void issueBook() {
        System.out.print("Enter Book ID to issue: ");
        int id = sc.nextInt();
        sc.nextLine();
        if (id >= 0 && id < bookTitles.length && bookTitles[id] != null) {
            if (!isIssued[id]) {
                isIssued[id] = true;
                System.out.println("Book issued successfully.");
            } else {
                System.out.println("Book is already issued.");
            }
        } else {
            System.out.println("Invalid Book ID.");
        }
    }

    // Method to Return book

    static void returnBook() {
        System.out.print("Enter Book ID to return: ");
        int id = sc.nextInt();
        sc.nextLine();
        if (id >= 0 && id < bookTitles.length && bookTitles[id] != null) {
            if (isIssued[id]) {
                isIssued[id] = false;
                System.out.println("Book returned successfully.");
            } else {
                System.out.println("Book was not issued.");
            }
        } else {
            System.out.println("Invalid Book ID.");
        }
    }
}

