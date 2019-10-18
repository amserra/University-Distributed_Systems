
// import java.apache.commons.validator.*;
import java.io.Console;
import java.util.Scanner;

// Import routines package!
import org.apache.commons.validator.routines.UrlValidator;

public class UI {
    RMIClient client;
    Scanner sc = new Scanner(System.in);

    UI(RMIClient client) {
        this.client = client;
    }

    public void mainMenu() {
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
            System.out.println("3. Logout");
            System.out.println("4. Exit");

            int option = validateIntegerValue(1, 4);
            switch (option) {
            case 1:
                search();
                return;
            case 2:
                client.searchHistory();
                return;
            case 3:
                logout();
                return;
            case 4:
                client.shutdown();
                return;
            default:
                System.out.println("CRITICAL ERROR. SHUTTING DOWN");
                System.exit(1);
            }
        } else if (client.typeOfClient.compareTo("admin") == 0) {
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
                client.searchHistory();
                return;
            case 3:
                administrationPage();
                return;
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
        }

    }

    public void administrationPage() {
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

    public void searchHistory(String[] values) {
        // Vai receber o search history(ns ainda que tipo de dados) através do Client
        // E vai imprimir aqui
        System.out.println("\n-----Search history-----\n");
        // imprimir aqui
    }

    public void indexNewURL() {
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

    public void grantPrivileges() {
        System.out.println("\n-----Grant admin privileges-----\n");
        // Obter lista de utilizadores(Client) e numera-los(1.,2.,3.,...)
        // int option = validateIntegerValue(1, n);, com n o num utilizadores
        // Pedir para selecionar um numero
        // Ir buscar a lista de clientes ao RMI server(objetos), procurar por
        // username e meter o seu typeOfClient = "admin"
    }

    public void login() {
        System.out.println("\n-----Login-----");
        System.out.println("NOTE: Type -1 to return to the main menu\n");
        String userName = validateStringValue("Username: ",
                "Invalid username.\nInsert a username with only letters and numbers and length within 4 to 15 characters.",
                4, 15);

        String password = validatePasswordValue(
                "Invalid password.\nInsert a password with only letters and numbers and length within 4 to 15 characters.");

        client.login();
    }

    public void logout() {
        System.out.println("\nAre you sure you want to logout?(y/n)");
        boolean result = validateLogout();
        client.logout(result);
    }

    public void register() {
        System.out.println("\n-----Register-----");
        System.out.println("NOTE: Type -1 to return to the main menu\n");
        String userName = validateStringValue("Username: ",
                "Invalid username.\nInsert a username with only letters and numbers and length within 4 to 15 characters.",
                4, 15);

        String password = validatePasswordValue(
                "Invalid password.\nInsert a password with only letters and numbers and length within 4 to 15 characters.");

        // Chamar metodo do server RMI, para ele poder enviar para o server Multicast

        // change type of user?
        client.register();
    }

    public void search() {
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

    public String validateURL() {
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