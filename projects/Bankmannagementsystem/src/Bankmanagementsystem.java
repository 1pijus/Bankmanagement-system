import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

    class Account {
        private String accountNumber;
        private String name;
        private String dob;
        private String aadharNo;
        private String mobileNo;
        private double balance;
        

    private static int nextAccountNumber = 1000000000;
       public void setAccountNumber(String accountNumber)
     {
        this.accountNumber = accountNumber;
     }

    public Account(String name, String dob, String aadharNo, String mobileNo, double initialBalance) 
    {
        this.accountNumber = generateAccountNumber();
        this.name = name;
        this.dob = dob;
        this.aadharNo = aadharNo;
        this.mobileNo = mobileNo;
        this.balance = initialBalance;
    }

    private static String generateAccountNumber() 
    {
        String accountNumberStr = String.valueOf(nextAccountNumber);
        nextAccountNumber++;
        return String.format("%012d", Long.parseLong(accountNumberStr));
    }

    public void deposit(double amount) 
    {
        balance += amount;
    }

    public boolean withdraw(double amount) 
    {
        if (balance >= amount) 
        {
            balance -= amount;
            return true;
        }
        return false;
    }

    public double getBalance() 
    {
        return balance;
    }

    public String getAccountNumber() 
    {
        return accountNumber;
    }

    public String getName() 
    {
        return name;
    }

    public String getDOB() 
    {
        return dob;
    }

    public String getAadharNo() 
    {
        return aadharNo;
    }

    public String getMobileNo() 
    {
        return mobileNo;
    }

    public String toString() 
    {
        return "Account Number: " + accountNumber +
                "\nName: " + name +
                "\nDate of Birth: " + dob +
                "\nAadhar Number: " + aadharNo +
                "\nMobile Number: " + mobileNo +
                "\nBalance: " + balance;
    }
}
public class Bankmanagementsystem  
{

    private static Account findAccount(ArrayList<Account> accounts, String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) 
            {
                return account;
            }
        }
        return null;
    }

    public static void main(String[] args) 
    {
        ArrayList<Account> accounts = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        String dbUrl = "jdbc:mysql://localhost:3306/bankdata";
        String dbUser = "bankmuser";
        String dbPassword = "bankpassword";

        try {
        
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            Statement statement = connection.createStatement();
            if (connection != null && !connection.isClosed())
             {
                System.out.println("Connected to the database!");
                System.out.println("Connection URL: " + dbUrl);
                System.out.println("Connected User: " + dbUser);
             }
        while (true)
         {
            System.out.println("\nMenu:");
            System.out.println("1. Create account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Balance inquiry");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            String choiceStr = scanner.nextLine();

            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
                } 
            catch (NumberFormatException e) 
                {
                    System.out.println("Invalid input. Please enter a valid choice.");
                    continue;
                }

            switch (choice) 
            {
                case 1:
                    System.out.print("Enter customer name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Aadhar number : ");
                    String aadharNo = scanner.nextLine();
                        System.out.print("Enter date of birth: ");
                    String dob = scanner.nextLine();
                    System.out.print("Enter mobile number : ");
                    String mobileNo = scanner.nextLine();
                    System.out.print("Enter initial balance: ");
                    double initialBalance = scanner.nextDouble();
                    scanner.nextLine();

                    if (aadharNo.length() != 12 ) 
                        {
                            System.out.println("Invalid Aadhar number. Account creation failed.");
                            break;
                        }
                    else if(mobileNo.length() != 10)
                    {
                          System.out.println("Invalid Mobile number. Account creation failed.");
                        break;

                     }

                     Account account = new Account(name, dob, aadharNo, mobileNo, initialBalance);
                     accounts.add(account);
                     String insertQuery = "INSERT INTO accounts (account_number,name, dob, aadharNo, mobileNo, balance) VALUES (?, ?, ?, ?, ?,?)";
                        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                        insertStatement.setString(1, account.getAccountNumber());
                        insertStatement.setString(2, account.getName());
                        insertStatement.setString(3, account.getDOB());
                        insertStatement.setString(4, account.getAadharNo());
                        insertStatement.setString(5, account.getMobileNo());
                        insertStatement.setDouble(6, account.getBalance());
                        insertStatement.executeUpdate();
                        insertStatement.close();
                     System.out.println("Account created successfully!");
                     System.out.println("Account Number: " + account.getAccountNumber()); // Display the account number
                     break;
               
                case 2:
                     System.out.print("Enter account number: ");
                     String depositAccountNumber = scanner.next();
                     System.out.print("Enter deposit amount: ");
                     double depositAmount = scanner.nextDouble();
                 
                     try {
                         String updateQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                         PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                         updateStatement.setDouble(1, depositAmount);
                         updateStatement.setString(2, depositAccountNumber);
                         int rowsUpdated = updateStatement.executeUpdate();
                         updateStatement.close();
                 
                         if (rowsUpdated > 0) 
                         {
                             System.out.println("Deposit successful.");
                             Account updatedAccount = findAccount(accounts, depositAccountNumber);
                             if (updatedAccount != null) 
                             {
                                 updatedAccount.deposit(depositAmount);
                                 System.out.println("Updated balance: " + updatedAccount.getBalance());
                             }
                         } 
                         else
                            {
                                System.out.println("Account not found.");
                            }
                         } 
                     catch (SQLException e) 
                        {
                            e.printStackTrace();
                        }
                        break;
                    

            
                
                case 3:
                        System.out.print("Enter account number: ");
                        String withdrawAccountNumber = scanner.next();
                        System.out.print("Enter withdrawal amount: ");
                        double withdrawAmount = scanner.nextDouble();
                        
                        try {
                            String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                            updateStatement.setDouble(1, withdrawAmount);
                            updateStatement.setString(2, withdrawAccountNumber);
                            int rowsUpdated = updateStatement.executeUpdate();
                            updateStatement.close();
                    
                            if (rowsUpdated > 0) 
                            {
                                System.out.println("Withdrawal successful.");
                                Account updatedAccount = findAccount(accounts, withdrawAccountNumber);
                                if (updatedAccount != null)
                                  {
                                    if (updatedAccount.withdraw(withdrawAmount)) 
                                        {
                                            System.out.println("Updated balance: " + updatedAccount.getBalance());
                                        } 
                                    else 
                                        {
                                            System.out.println("Insufficient balance.");
                                        }
                                }
                            }
                           else 
                                {
                                    System.out.println("Account not found.");
                                }
                           } 
                        catch (SQLException e) 
                            {
                                e.printStackTrace();
                            }
                        break;
            


                    case 4:
                            String query = "SELECT * FROM accounts";
                            ResultSet resultSet = statement.executeQuery(query);
                            System.out.print("Enter the account number to search: ");
                            String searchAccountNumber = scanner.next();
                            while (resultSet.next()) 
                            {
                                String accountNumber = resultSet.getString("account_number");
                                name = resultSet.getString("name");
                                dob = resultSet.getString("dob");
                                aadharNo = resultSet.getString("aadharNo");
                                mobileNo = resultSet.getString("mobileNo");
                                double balance = resultSet.getDouble("balance"); // Fetch the balance from the resultSet
                                
                                if (accountNumber.equals(searchAccountNumber)) 
                                    {
                                        // Create an Account object and display its information
                                        Account accountobj = new Account(name, dob, aadharNo, mobileNo, balance); // Use the fetched balance
                                        accountobj.setAccountNumber(accountNumber);
                                        System.out.println(accountobj);
                                        break; // Exit the loop once the account is found
                                    }
                                else
                                {
                                System.out.println("not found");
                                }
                            }

                            resultSet.close();
                        break;



                    case 5:
                        System.out.println("Exiting the bank management system.");
                        scanner.close();
                        statement.close();
                        connection.close();
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
    }
               
            }
             

            }
            catch (SQLException e)
            {
            e.printStackTrace();

            }
        
}
}

