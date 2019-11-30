import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Server extends UnicastRemoteObject implements ServerInterface {
    protected Server() throws RemoteException {
    }


    public byte[] download(String file, String course) throws RemoteException {
        throw new RuntimeException("Not signed in");
    }

    public ServerInterface logIn(String login, String password) throws RemoteException {
        DBHandler.login(login,password);
        return new ClientsServer(login);
    }

    public ServerInterface signUp(String login, String password) throws RemoteException {
        DBHandler.signUp(login,password);
        return new ClientsServer(login);
    }

    public List<String> getAvailable() throws RemoteException {
        throw new RuntimeException("Not signed in");
    }

    public List<String> getEditable() throws RemoteException {
        throw new RuntimeException("Not signed in");
    }

    public List<String> getFiles() throws RemoteException {
        throw new RuntimeException("Not signed in");
    }

    public List<String> getFilesInCourse(String course) throws RemoteException {
        throw new RuntimeException("Not signed in");
    }

    public void addListener(String listener, String course) throws RemoteException {
        throw new RuntimeException("Not signed in");
    }

    public void upload( String course, String file,byte[] data) throws RemoteException {
        throw new RuntimeException("Not signed in");
    }

    public void createCourse(String courseName) throws RemoteException {
        throw new RuntimeException("Not signed in");
    }
}
