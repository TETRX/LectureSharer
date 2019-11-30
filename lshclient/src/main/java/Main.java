import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Main {
    static ServerInterface stub;
    public static void main(String[] args) {

        try {
            do{
                Registry registry = LocateRegistry.getRegistry(/*for non-local tests, insert server ip address as string*/40960);
                stub = (ServerInterface) registry.lookup("ServerInterface");
                if(!handleLogin())
                    break;
            }while (!handleOperations());

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    static boolean handleLogin() throws RemoteException{
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Type e to exit, s to sign up, l to login");
            String a = scanner.nextLine();
            if (a.compareTo("e") == 0) {
                return false;
            }
            if (a.compareTo("s") == 0) {
                System.out.println("login: ");
                String login = scanner.nextLine();
                System.out.println("password: ");
                String password = scanner.nextLine();
                try {
                    stub = stub.signUp(login, password);
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                    continue;
                }
                return true;
            }
            if (a.compareTo("l") == 0) {
                System.out.println("login: ");
                String login = scanner.nextLine();
                System.out.println("password: ");
                String password = scanner.nextLine();
                try {
                    stub = stub.logIn(login, password);
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                    continue;
                }
                return true;
            }
        }
    }

    static boolean handleOperations(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type h for help");
        while (true) {
            String a = scanner.nextLine();
            try {
                if(!new Command(a,stub).execute()){
                    return false;
                }
            }catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
