import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientAccepter {
    public static void main(String []args){
        try {
           // System.setProperty("java.rmi.server.hostname", /*for non-local tests, insert server ip address as string and uncomment this line*/);
            Registry registry = LocateRegistry.createRegistry(40960);
            registry.rebind("ServerInterface", new Server());
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
