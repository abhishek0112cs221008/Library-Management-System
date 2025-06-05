import com.mysql.cj.protocol.Message;

import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LibraryDB {
    private Connection conn;

    public LibraryDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root", "0000");
            System.out.println("DataBase Connected Successfully!");
        }
        catch (SQLException e) {
            System.out.println("Connected Failed!");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return conn;
    }

    //Register The user
    public boolean registerUser(String username, String password, String role) {
        String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, role);
            stmt.executeUpdate();
            return true;
        }
        catch (SQLException e) {
            System.out.println("Registration Failed : " + e.getMessage());
            return false;
        }
    }

    // Login User
    public String loginUser(String username, String password) {
        String sql = "SELECT role, password FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedInput = hashPassword(password);
                String storedHash = rs.getString("password");
                if (hashedInput.equals(storedHash)) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }
//    public String loginUser(String username, String password) {
//        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
//
//        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setString(1,username);
//            stmt.setString(2,password);
//
//            ResultSet rs = stmt.executeQuery();
//            if(rs.next()) {
//                return rs.getString("role");
//            }
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public boolean addBook(String title, String author) {
        String sql = "INSERT INTO books (title, author) VALUES (?, ?)" + "ON DUPLICATE KEY UPDATE book_count = book_count+1";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.executeUpdate();
            return true;
        }
        catch(SQLException e) {
            System.out.println("Error Adding Book: " + e.getMessage());
            return false;
        }
    }

    public void viewBooks() {
        String sql = "SELECT * FROM books";

        try (Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n--- Book List ---");
            while(rs.next()) {
                System.out.printf("ID: %d | Title: %s | Author: %s | Issued: %s | Count: %d\n",
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getBoolean("is_issued") ? "Yes" : "No",
                            rs.getInt("book_count")
                        );
            }

        }
        catch(SQLException e) {
            System.out.println("Error Viewing Book: " + e.getMessage());
        }
    }

    public boolean deleteBook(int bookId) {
//        String sql = "DELETE FROM books WHERE  = ?";
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1,bookId);
//            int rows = stmt.executeUpdate();
//            return rows > 0;
//        }
//        catch (Exception e) {
//            System.out.println("Error Deleting Book: " + e.getMessage());
//            return false;
//        }

        String selectSql = "SELECT book_count FROM books WHERE book_id = ?";
        String updateSql = "UPDATE books SET book_count = book_count - 1 WHERE book_id = ?";
        String deleteSql = "DELETE FROM books WHERE book_id = ?";

        try {
            // 1. get current book count
            int currbookCnt = 0;
            try(PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1,bookId);
                try(ResultSet rs = selectStmt.executeQuery()) {
                    if(rs.next()) {
                        currbookCnt = rs.getInt("book_count");
                    }
                    else {
                        System.out.println("No book found with ID " + bookId + ".");
                        return false;
                    }
                }
            }

            // 2. Decide whether to decrement book count or delete book
            if(currbookCnt > 1) {
                // Decrement
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)){
                    updateStmt.setInt(1,bookId);
                    int rowAffected = updateStmt.executeUpdate();
                    if(rowAffected > 0) {
                        return true;
                    } else {
                        System.err.println("Failed to decrement book count with book id: "+bookId);
                        return false;
                    }
                }
            } else if (currbookCnt == 1) {
                // Delete book
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1,bookId);
                    int rows = deleteStmt.executeUpdate();
                    if(rows > 0) {
                        return true;
                    }
                    else {
                        System.out.println("Failed to delete book with id "+bookId+".");
                        return false;
                    }
                }
                catch (Exception e) {
                    System.out.println("Error Deleting Book: " + e.getMessage());
                    return false;
                }
            } else {
                System.out.println("Book with ID "+ bookId +"has a count of " + currbookCnt + ". No action taken.");
                return false;
            }
        }
        catch (SQLException e) {
            System.err.println("DataBase error while attempting to remove book with ID" + bookId + e.getMessage());
            return false;
        }
    }

    // View available books
    public void viewAvailableBooks() {
        String sql = "SELECT * FROM books WHERE is_issued = false";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Available Books ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Title: %s | Author: %s\n",
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching available books: " + e.getMessage());
        }
    }

    // Issue a book
    public boolean issueBook(String username, int bookId) {
        String check = "SELECT is_issued FROM books WHERE book_id = ?";
        String update = "UPDATE books SET is_issued = true WHERE book_id = ?";
        String history = "INSERT INTO issue_history (user, book_id, action) VALUES (?, ?, 'issue')";

        try (PreparedStatement checkStmt = conn.prepareStatement(check)) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && !rs.getBoolean("is_issued")) {
                conn.setAutoCommit(false);

                try (PreparedStatement updateStmt = conn.prepareStatement(update);
                     PreparedStatement histStmt = conn.prepareStatement(history)) {

                    updateStmt.setInt(1, bookId);
                    updateStmt.executeUpdate();

                    histStmt.setString(1, username);
                    histStmt.setInt(2, bookId);
                    histStmt.executeUpdate();

                    conn.commit();
                    return true;
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                } finally {
                    conn.setAutoCommit(true);
                }
            } else {
                System.out.println("Book already issued or not found.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error issuing book: " + e.getMessage());
            return false;
        }
    }

    // Return a book
    public boolean returnBook(String username, int bookId) {
        String update = "UPDATE books SET is_issued = false WHERE book_id = ?";
        String history = "INSERT INTO issue_history (user, book_id, action) VALUES (?, ?, 'return')";

        try (PreparedStatement updateStmt = conn.prepareStatement(update);
             PreparedStatement histStmt = conn.prepareStatement(history)) {

            updateStmt.setInt(1, bookId);
            int rows = updateStmt.executeUpdate();

            if (rows > 0) {
                histStmt.setString(1, username);
                histStmt.setInt(2, bookId);
                histStmt.executeUpdate();
                return true;
            } else {
                System.out.println("Book not found.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
            return false;
        }
    }

    // View issue history
    public void viewIssueHistory(String username) {
        String sql = "SELECT * FROM issue_history WHERE user = ? ORDER BY action_time DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Your Book History ---");
            while (rs.next()) {
                System.out.printf("Book ID: %d | Action: %s | Time: %s\n",
                        rs.getInt("book_id"),
                        rs.getString("action"),
                        rs.getTimestamp("action_time"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching history: " + e.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for(byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ? AND role != 'admin'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public void viewAllUsers() {
        String sql = "SELECT user_id, username, role FROM users WHERE role = 'user' ORDER BY user_id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Registered Users ---");
            while (rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("username");
                String role = rs.getString("role");

                System.out.printf("ID: %d | Username: %s | Role: %s\n", id, name, role);
            }
            System.out.println("-------------------------\n");
        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
    }

    public void exportUserHistory(String username) {
        String sql = "SELECT * FROM issue_history WHERE user = ? ORDER BY action_time DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            FileWriter writer = new FileWriter(username + "_history.txt");

            writer.write("Book History for " + username + ":\n\n");
            while (rs.next()) {
                writer.write(String.format("Book ID: %d | Action: %s | Time: %s\n",
                        rs.getInt("book_id"),
                        rs.getString("action"),
                        rs.getTimestamp("action_time")));
            }

            writer.close();
            System.out.println("History exported to " + username + "_history.txt");
        } catch (SQLException | IOException e) {
            System.out.println("Error exporting history: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error exporting history:  " + e.getMessage());
        }
    }



}
