
// import java.apache.commons.validator.*;
import java.io.Console;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

// Import routines package!
import org.apache.commons.validator.routines.UrlValidator;

public class UI {
    RMIClient client;
    Scanner sc = new Scanner(System.in);

    UI(RMIClient client) {
        this.client = client;
    }

    public void mainMenu() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Main Menu-----");
        System.out.println("Type of client: " + client.typeOfClient + "\n");
        if (client.typeOfClient.compareTo("anonymous") == 0) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Search");
            System.out.println("4. Exit");

            int option = validateIntegerValue(1, 4);
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
                client.shutdown();
                return;
            default:
                System.out.println("CRITICAL ERROR. SHUTTING DOWN");
                System.exit(1);
            }
        } else if (client.typeOfClient.compareTo("user") == 0) {
            System.out.println("1. Search");
            System.out.println("2. Search history");
            System.out.println("3. List of pages with connection to another page");
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
                linksPointing();
            case 4:
                logout();
                return;
            case 5:
                client.shutdown();
                return;
            default:
                System.out.println("CRITICAL ERROR. SHUTTING DOWN");
                System.exit(1);
            }
        } else if (client.typeOfClient.compareTo("admin") == 0) {
            System.out.println("1. Search");
            System.out.println("2. Search history");
            System.out.println("3. List of pages with connection to another page");
            System.out.println("4. Administration page");
            System.out.println("5. Logout");
            System.out.println("6. Exit");

            int option = validateIntegerValue(1, 6);
            switch (option) {
            case 1:
                search();
                return;
            case 2:
                searchHistory();
                return;
            case 3:
                linksPointing();
                return;
            case 4:
                administrationPage();
                return;
            case 5:
                logout();
                return;
            case 6:
                client.shutdown();
                return;
            default:
                System.out.println("CRITICAL ERROR. SHUTTING DOWN");
                System.exit(1);
            }
        }

    }

    public void administrationPage() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Administration page-----\n");

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

    private void linksPointing() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----List of pages with link to another page-----\n");
        System.out.println("NOTE: Type -1 to return to the main menu\n");

        String url = validateURL();
        client.linksPointing(url);
    }

    public void searchHistory() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Search history-----\n");
        client.searchHistory();
    }

    public void indexNewURL() throws RemoteException, MalformedURLException, NotBoundException {
        // Verificar se e um URL valido
        System.out.println("\n-----Index new URLs-----\n");
        System.out.println("NOTE: Type -1 to return to the main menu\n");

        String url = validateURL();
        client.indexNewURL(url);
    }

    public void realTimeStatistics() {
        // Vai receber as estatisticas(ns ainda que tipo de dados) através do Client
        // E vai imprimir aqui
        System.out.println("\n-----Real-time Statistics-----\n");
        // imprimir aqui
    }

    public void grantPrivileges() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Grant admin privileges-----\n");
        String userName = validateStringValue("Username to make admin: ",
                "Invalid username.\nInsert a valid username (with only letters and numbers and length within 4 to 15 characters).",
                4, 15);
        client.grantPrivileges(userName);
    }

    public void login() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Login-----");
        System.out.println("NOTE: Type -1 to return to the main menu\n");
        String userName = validateStringValue("Username: ",
                "Invalid username.\nInsert a username with only letters and numbers and length within 4 to 15 characters.",
                4, 15);

        String password = validatePasswordValue(
                "Invalid password.\nInsert a password with only letters and numbers and length within 4 to 15 characters.");

        client.authentication(true, userName, password);
    }

    public void logout() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\nAre you sure you want to logout?(y/n)");
        boolean result = validateLogout();
        client.logout(result);
    }

    public void register() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Register-----");
        System.out.println("NOTE: Type -1 to return to the main menu\n");
        String userName = validateStringValue("Username: ",
                "Invalid username.\nInsert a username with only letters and numbers and length within 4 to 15 characters.",
                4, 15);

        String password = validatePasswordValue(
                "Invalid password.\nInsert a password with only letters and numbers and length within 4 to 15 characters.");

        // Chamar metodo do server RMI, para ele poder enviar para o server Multicast

        // change type of user?
        client.authentication(false, userName, password);
    }

    public void search() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Search-----");
        System.out.println("NOTE: Type -1 to return to the main menu\n");

        String[] words = validateSearch();

        client.search(words);
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

    public String validateStringValue(String msg, String errorMsg, int min, int max)
            throws RemoteException, MalformedURLException, NotBoundException {
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
            System.out.println("ERROR: " + errorMsg);
        }
    }

    public String validatePasswordValue(String errorMsg)
            throws RemoteException, MalformedURLException, NotBoundException {
        Console console = System.console();
        console.flush();
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
            System.out.println("ERROR: " + errorMsg);
        }

    }

    public String[] validateSearch() throws RemoteException, MalformedURLException, NotBoundException {
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

    public String validateURL() throws RemoteException, MalformedURLException, NotBoundException {
        // Only allows http/https URLs
        UrlValidator defaultValidator = new UrlValidator(); // default schemes
        while (true) {
            System.out.print("Insert URL: ");
            String line = sc.nextLine();
            try {
                if (line.compareTo("-1") == 0) {
                    mainMenu();
                    return null;
                } else {
                    if (defaultValidator.isValid(line)) {
                        return line;
                    }
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("ERROR: Invalid URL.");
        }
    }
}