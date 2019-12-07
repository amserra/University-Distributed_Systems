
import java.io.Console;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * Class that contains the Client UI
 */
public class UI {
    RMIClient client;
    Scanner sc = new Scanner(System.in);

    /**
     * Recieves the client reference and assigns it to a parameter.
     * 
     * @param client
     */
    UI(RMIClient client) {
        this.client = client;
    }

    /**
     * Main menu UI, with all the options available, depending on the type of
     * client.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void mainMenu() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Main Menu-----");
        System.out.println("Type of client: " + client.typeOfClient + "\n");
        if (client.typeOfClient.equals("anonymous")) {
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
        } else if (client.typeOfClient.equals("user")) {
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
        } else if (client.typeOfClient.equals("admin")) {
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
        return;
    }

    /**
     * Administration page, containing all the admin options available.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
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
        return;
    }

    /**
     * Login UI page. Asks and validates for a username and a password.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
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

    /**
     * Register UI page. Asks and validates for a username and a password.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void register() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Register-----");
        System.out.println("NOTE: Type -1 to return to the main menu\n");
        String userName = validateStringValue("Username: ",
                "Invalid username.\nInsert a username with only letters and numbers and length within 4 to 15 characters.",
                4, 15);

        String password = validatePasswordValue(
                "Invalid password.\nInsert a password with only letters and numbers and length within 4 to 15 characters.");

        client.authentication(false, userName, password);
    }

    /**
     * Logout UI confirm page. Checks if user is certain about his/hers choice.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void logout() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\nAre you sure you want to logout?(y/n)");
        boolean result = validateLogout();
        client.logout(result);
    }

    /**
     * Search UI page.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void search() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Search-----");
        System.out.println("NOTE: Type -1 to return to the main menu\n");

        String[] words = validateSearch();

        client.search(words);
    }

    /**
     * Search History page.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void searchHistory() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Search history-----\n");
        client.searchHistory();
    }

    /**
     * Links pointing page.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void linksPointing() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----List of pages with link to another page-----\n");
        System.out.println("NOTE: Type -1 to return to the main menu\n");

        String url = validateURL();
        client.linksPointing(url);
    }

    /**
     * Page to index a new URL.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void indexNewURL() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Index new URLs-----\n");
        System.out.println("NOTE: Type -1 to return to the main menu\n");

        String url = validateURL();
        client.indexNewURL(url);
    }

    /**
     * RTS page. Changes the users inRealTimeStatistics parameter upon entrance and
     * departure.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void realTimeStatistics() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Real-time Statistics-----\n");
        System.out.println("NOTE: Type anything to return to the admin page menu\n");

        this.client.inRealTimeStatistics = true;
        client.realTimeStatistics();
        sc.nextLine();
        this.client.inRealTimeStatistics = false;
        administrationPage();
        return;
    }

    /**
     * Grant privileges page. Asks for a username.
     * 
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public void grantPrivileges() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("\n-----Grant admin privileges-----\n");
        System.out.println("NOTE: Type -1 to return to the main menu\n");

        String userName = validateStringValue("Username to make admin: ",
                "Invalid username.\nInsert a valid username (with only letters and numbers and length within 4 to 15 characters).",
                4, 15);
        client.grantPrivileges(userName);
    }

    // Value validation methods

    /**
     * Validates if a integer value is within a range.
     * 
     * @param min Minimum value
     * @param max Maximum value
     * @return A value between min and max (including)
     */
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

    /**
     * Validates logout(yes/YES/y/Y or no/NO/n/N)
     * 
     * @return true for yes, false for no
     */
    public boolean validateLogout() {
        while (true) {
            String line = sc.nextLine();
            try {
                if ((line.toLowerCase().equals("yes")) || (line.toLowerCase().equals("y"))) {
                    return true;
                } else if ((line.toLowerCase().equals("no")) || (line.toLowerCase().equals("n"))) {
                    return false;
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("ERROR: Introduce a valid option(y/n).");
        }
    }

    /**
     * Validates if a string only contains alphanumeric characters and is between a
     * set size.
     * 
     * @param msg
     * @param errorMsg Error message to be displayed
     * @param min      Minimum size
     * @param max      Maximum size
     * @return The validated string
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public String validateStringValue(String msg, String errorMsg, int min, int max)
            throws RemoteException, MalformedURLException, NotBoundException {
        while (true) {
            System.out.print(msg);
            String line = sc.nextLine();

            try {
                if (line.equals("-1")) {
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

    /**
     * Validates a password value and makes it impossible to see in the terminal.
     * 
     * @param errorMsg
     * @return
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public String validatePasswordValue(String errorMsg)
            throws RemoteException, MalformedURLException, NotBoundException {
        Console console = System.console();
        //console.flush();
        while (true) {
            System.out.println("Note that, for security reasons, your password doesn't happear as you type it.");
            //String line = new String(console.readPassword("Password: "));
            String line = "password";
            try {
                if (line.equals("-1")) {
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

    /**
     * Validates a set of search terms.
     * 
     * @return
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public String[] validateSearch() throws RemoteException, MalformedURLException, NotBoundException {
        int count;
        while (true) {
            count = 0;
            System.out.print("Search terms: ");
            String line = sc.nextLine();
            try {
                if (line.equals("-1")) {
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
        }
    }

    /**
     * Validates an URL with an external library(commons-validator-1.6.jar)
     * 
     * @return
     * @throws RemoteException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    public String validateURL() throws RemoteException, MalformedURLException, NotBoundException {
        // Only allows http/https URLs
        UrlValidator defaultValidator = new UrlValidator(); // default schemes
        while (true) {
            System.out.print("Insert URL: ");
            String line = sc.nextLine();
            try {
                if (line.equals("-1")) {
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