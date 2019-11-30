import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {
    byte[] download(String file, String course) throws RemoteException;
    ServerInterface logIn(String login, String password) throws RemoteException;
    ServerInterface signUp(String login, String password) throws RemoteException;
    List<String> getAvailable() throws RemoteException;
    List<String> getEditable() throws RemoteException;
    List<String> getFiles() throws RemoteException;
    List<String> getFilesInCourse(String course) throws RemoteException;
    void addListener(String listener, String course) throws RemoteException;
    void upload(String course,String file, byte[] data) throws RemoteException;
    void createCourse(String courseName) throws RemoteException;
}
