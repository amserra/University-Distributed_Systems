import java.util.Scanner;
import java.io.*;
import java.lang.Object;

public class UI {
    String typeOfClient;
    Scanner sc = new Scanner(System.in);

    UI(String typeOfClient) {
        this.typeOfClient = typeOfClient;
    }

    public void mainMenu() {
        clearConsole();
        System.out.println("-----Main Menu-----");
        System.out.println("Type of client: " + this.typeOfClient + "\n");
        if (this.typeOfClient.compareTo("anonymous") == 0) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Search");
            System.out.println("4. Exit");

            int option = validateIntegerValue(1, 4);
            System.out.println(option);
            switch (option) {
            case 1:
                login();
                return;
            case 2:
                register();
                return;
            case 3:
                search();
                return;
            case 4:
                shutdown();
                return;
            default:
                System.out.println("CRITICAL ERROR. SHUTTING DOWN");
                System.exit(1);
            }
        } else if (this.typeOfClient.compareTo("user") == 0) {
            System.out.println("1. Search");
            System.out.println("2. Search history");
            System.out.println("3. Logout");
            System.out.println("4. Exit");

            int option = validateIntegerValue(1, 4);
            System.out.println(option);
            switch (option) {
            case 1:
                search();
                return;
            case 2:
                searchHistory();
                return;
            case 3:
                logout();
                return;
            case 4:
                shutdown();
                return;
            default:
                System.out.println("CRITICAL ERROR. SHUTTING DOWN");
                System.exit(1);
            }
        } else if (this.typeOfClient.compareTo("admin") == 0) {
            System.out.println("1. Search");
            System.out.println("2. Search history");
            System.out.println("3. Administration page");
            System.out.println("4. Logout");
            System.out.println("5. Exit");

            int option = validateIntegerValue(1, 5);
            switch (option) {
            case 1:
                search();
                return;
            case 2:
                searchHistory();
                return;
            case 3:
                administrationPage();
                return;
            case 4:
                logout();
                return;
            case 5:
                shutdown();
                return;
            default:
                System.out.println("CRITICAL ERROR. SHUTTING DOWN");
                System.exit(1);
            }
        }

    }

    public void shutdown() {
        // Desligar conexao
        System.out.println("Shutdown complete.\nHope to see you again soon! :)");
        System.exit(1);
    }

    public void administrationPage() {
        clearConsole();
        System.out.println("-----Administration page-----\n");

        System.out.println("1. Index new URL");
        System.out.println("2. Real-time statistics");
        System.out.println("3. Grant admin privileges to another user");
        System.out.println("4. Back to main menu");

        int option = validateIntegerValue(1, 4);
        switch (option) {
        case 1:
            indexNewURL();
            return;
        case 2:
            realTimeStatistics();
            return;
        case 3:
            grantPrivileges();
            return;
        case 4:
            mainMenu();
            return;
        default:
            System.out.println("CRITICAL ERROR. SHUTTING DOWN");
            System.exit(1);
        }

    }

    public void searchHistory() {

    }

    public void indexNewURL() {

    }

    public void realTimeStatistics() {

    }

    public void grantPrivileges() {

    }

    public void login() {
        clearConsole();
        System.out.println("-----Login-----");
        System.out.println("NOTE: Type -1 to return to the main menu at any time\n");
        String userName = validateStringValue("Username: ",
                "Invalid username.\nInsert a username with only letters and numbers and length within 4 to 15 characters.",
                4, 15);

        String password = validatePasswordValue(
                "Invalid password.\nInsert a password with only letters and numbers and length within 4 to 15 characters.");

        // Chamar metodo do server RMI, para ele poder enviar para o server Multicast

        // change type of user?

    }

    public void logout() {
        System.out.println("Are you sure you want to logout?(y/n)");
        boolean result = validateLogout();
        if (result == true) {
            this.typeOfClient = "anonymous";
        }
        mainMenu();
    }

    public void register() {
        clearConsole();
        System.out.println("-----Register-----");
        System.out.println("NOTE: Type -1 to return to the main menu at any time\n");
        String userName = validateStringValue("Username: ",
                "Invalid username.\nInsert a username with only letters and numbers and length within 4 to 15 characters.",
                4, 15);

        String password = validatePasswordValue(
                "Invalid password.\nInsert a password with only letters and numbers and length within 4 to 15 characters.");

        // Chamar metodo do server RMI, para ele poder enviar para o server Multicast

        // change type of user?
    }

    public void search() {
        clearConsole();
        System.out.println("-----Search-----");
        System.out.println("NOTE: Type -1 to return to the main menu at any time\n");

        String[] words = validateSearch();
    }

    public int validateIntegerValue(int min, int max) {
        while (true) {
            System.out.print("Enter an option: ");
            String line = sc.nextLine();
            try {
                int n = Integer.parseInt(line);
                if (n >= min && n <= max) {
                    return n;
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("ERROR: Introduce a valid option.");
        }
    }

    public boolean validateLogout() {
        while (true) {
            String line = sc.nextLine();
            try {
                if ((line.toLowerCase().compareTo("yes") == 0) || (line.toLowerCase().compareTo("y") == 0)) {
                    return true;
                } else if ((line.toLowerCase().compareTo("no") == 0) || (line.toLowerCase().compareTo("n") == 0)) {
                    return false;
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("ERROR: Introduce a valid option(y/n).");
        }
    }

    public String validateStringValue(String msg, String errorMsg, int min, int max) {
        while (true) {
            System.out.print(msg);
            String line = sc.nextLine();

            try {
                if (line.compareTo("-1") == 0) {
                    mainMenu();
                    return null;
                } else if ((line != null) && (!line.equals("")) && (line.matches("[a-zA-Z0-9]*"))
                        && (line.length() >= min) && line.length() <= max) {
                    return line;
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("ERRO: " + errorMsg);
        }
    }

    public String validatePasswordValue(String errorMsg) {
        Console console = System.console();
        while (true) {
            String line = new String(console.readPassword("Password: "));
            try {
                if (line.compareTo("-1") == 0) {
                    mainMenu();
                    return null;
                } else if ((line != null) && (!line.equals("")) && (line.matches("[a-zA-Z0-9]*"))
                        && (line.length() >= 4) && line.length() <= 15) {
                    return line;
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("ERRO: " + errorMsg);
        }

    }

    public String[] validateSearch() {
        int count;
        while (true) {
            count = 0;
            System.out.print("Search terms: ");
            String line = sc.nextLine();
            try {
                if (line.compareTo("-1") == 0) {
                    mainMenu();
                    return null;
                } else if ((line != null) && (!line.equals(""))) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        if (!word.matches("[a-zA-Z0-9]*")) {
                            break;
                        } else {
                            count++;
                        }
                    }
                    if (count == words.length) {
                        return words;
                    }
                }

            } catch (NumberFormatException e) {
            }
            System.out.println("ERROR: Invalid search terms.");
            // Procura com várias palavras!
        }
    }

    public final static void clearConsole() {
        for (int i = 0; i < 50; ++i)
            System.out.println();
    }
}