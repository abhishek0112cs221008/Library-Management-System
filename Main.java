import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static LibraryDB db = new LibraryDB();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while(true) {
            System.out.println("\n=== Library System ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. exit");
            System.out.println("Choose option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> login();
                case 2 -> register();
                case 3 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid Choice!");
            }
        }
    }

    private static void register() {
        sc.nextLine();
        System.out.println("Enter Username: ");
        String username = sc.nextLine();
        System.out.println("Enter password: ");
        String password = sc.nextLine();
        System.out.println("Enter Role: (admin/user)");
        String role = sc.nextLine();

        boolean success = db.registerUser(username, password, role.toLowerCase());

        if(success) {
            System.out.println("Login Successful!");
        }
        else {
            System.out.println("Login Failed (Maybe Change User Name Because username Already Taken)");
        }
    }

    private static void login() {
        sc.nextLine();
        System.out.println("Enter Username: ");
        String username = sc.nextLine();

        System.out.println("Enter password: ");
        String password = sc.nextLine();

        String role = db.loginUser(username, password);

        if(role != null) {
            System.out.println("Login Successful as: " + role.toUpperCase());
            if(role.equalsIgnoreCase("admin")){
                adminMenu(username);
            }
            else {
                userMenu(username);
            }
        }
        else {
            System.out.println("Login failed.");
        }
    }

    private static void adminMenu(String username) {
        while(true) {
            System.out.println("Welcome "+ username + "\n=== ADMIN MENU ====");
            System.out.println("1. Add Book");
            System.out.println("2. View Book");
            System.out.println("3. Delete Book");
            System.out.println("4. Delete User");
            System.out.println("5. View All Users");
            System.out.println("6. LogOUT");
            System.out.println("Choose Options: ");
            int choice = sc.nextInt();
            sc.nextLine(); // clear new line

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter book title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter book author: ");
                    String author = sc.nextLine();

                    if(db.addBook(title, author)) {
                        System.out.println("book Added Successfully!");
                    }
                    else {
                        System.out.println("Failed to add book.");
                    }
                }

                case 2 -> db.viewBooks();

                case 3 -> {
                    System.out.print("Enter Book Id to delete: ");
                    int bookid = sc.nextInt();
                    if(db.deleteBook(bookid)) {
                        System.out.println("book deleted Successfully!");
                    } else {
                        System.out.println("Failed to delete book.");
                    }
                }

                case 4 -> {
                    System.out.print("Enter username to delete: ");
                    String delUser = sc.nextLine();

                    if (delUser.equals(username)) {
                        System.out.println("You cannot delete yourself.");
                    } else if (db.deleteUser(delUser)) {
                        System.out.println("User deleted successfully.");
                    } else {
                        System.out.println("User not found or couldn't be deleted.");
                    }
                }

                case 5 -> db.viewAllUsers();

                case 6 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid Choice.");
            }
        }
    }

    private static void userMenu(String username) {
        while (true) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. View Available Books");
            System.out.println("2. Issue Book");
            System.out.println("3. Return Book");
            System.out.println("4. View My History");
            System.out.println("5. Export My History");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // clear newline

            switch (choice) {
                case 1 -> db.viewAvailableBooks();
                case 2 -> {
                    System.out.print("Enter Book ID to issue: ");
                    int id = sc.nextInt();
                    if (db.issueBook(username, id)) {
                        System.out.println("Book issued successfully.");
                    } else {
                        System.out.println("Failed to issue book.");
                    }
                }
                case 3 -> {
                    System.out.print("Enter Book ID to return: ");
                    int id = sc.nextInt();
                    if (db.returnBook(username, id)) {
                        System.out.println("Book returned successfully.");
                    } else {
                        System.out.println("Failed to return book.");
                    }
                }
                case 4 -> db.viewIssueHistory(username);
                case 5 -> db.exportUserHistory(username);

                case 6 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}