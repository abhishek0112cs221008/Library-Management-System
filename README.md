📚 Library Management System (CLI-Based)
A simple yet powerful Command-Line Interface (CLI) based Library Management System built with Core Java and MySQL. This system allows admins and users to manage books, issue/return them, and handle fines with ease.
📌 Key Features
👤 User
- Register/Login
- View available books
- Issue and return books
- View issued history
- Pay overdue fines
🛠️ Admin
- Login with secure credentials
- Add/Edit/Delete books
- View users and issued books
- Manage overdue and fine reports
💸 Fine System
- Auto-calculation of overdue fines
- Block return without fine clearance
- Payment history tracking
🧰 Tech Stack
Component | Technology Used
-----------|--------------------
Language | Java (Core Java)
Database | MySQL
Interface | Command-Line (CLI)
Connector | JDBC
📂 Project Structure

LibraryManagementSystem/
├── src/
│   ├── Main.java
│   ├── DatabaseConnection.java
│   ├── models/
│   │   ├── Book.java
│   │   └── User.java
│   ├── services/
│   │   ├── AdminService.java
│   │   ├── UserService.java
│   │   ├── BookService.java
│   │   └── FineService.java
│   └── utils/
│       └── InputHandler.java
├── sql/
│   └── library_schema.sql
└── README.md


🚀 Getting Started
✅ Prerequisites
- Java JDK 8 or above
- MySQL Server installed
- JDBC Connector (optional if using IDE)
🔧 Setup Instructions
1. Clone the Repository
   git clone https://github.com/your-username/library-management-cli.git
   cd library-management-cli

2. Configure Database
   CREATE DATABASE library_db;
   USE library_db;
   Run the SQL script from sql/library_schema.sql

3. Update DB Credentials in DatabaseConnection.java
   String url = "jdbc:mysql://localhost:3306/library_db";
   String user = "root";
   String password = "your_mysql_password";

4. Compile & Run the App
   javac src/*.java
   java src/Main
🧪 Sample CLI Output
=== Welcome to Library Management System ===
1. Login as Admin
2. Login as User
3. Register
4. Exit
🎯 Future Improvements
- GUI using JavaFX or Swing
- Cloud-based DB & deployment
- Email OTP for user verification
- Enhanced fine analytics dashboard
🤝 Contribution
Feel free to fork the project and submit pull requests. Issues and feature suggestions are welcome too.
📝 License
This project is licensed under the MIT License – see the LICENSE file for details.
👨‍💻 Author
Abhishek Patel
B.E. CSE | Java Developer
📧 0112cs221008@gmail.com
🔗 GitHub: https://github.com/abhishek0112cs221008
🔗 LinkedIn: https://www.linkedin.com/in/abhishek-patel-93201426a
