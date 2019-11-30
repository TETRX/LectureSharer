import java.io.*;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ClientsServer extends UnicastRemoteObject implements ServerInterface {
    private String login;
    protected ClientsServer() throws RemoteException {
    }

    ClientsServer(String login) throws RemoteException{
        super();
        this.login=login;
    }

    public synchronized byte[] download(String file, String course) throws RemoteException {
        if(!getFilesInCourse(course).contains(file)){
            throw new RuntimeException("No such file in this course");
        }
        File f = new File(CoursesStoragePath.getDir() + File.separator + course + File.separator + file);
        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong on the server side");
        }
    }

    public ServerInterface logIn(String login, String password) throws RemoteException {
        throw new RuntimeException("Logged in as "+this.login);
    }

    public ServerInterface signUp(String login, String password) throws RemoteException {
        throw new RuntimeException("Logged in as "+this.login);
    }

    public List<String> getAvailable() throws RemoteException {
        return DBHandler.getCourses(this.login);
    }

    public List<String> getEditable() throws RemoteException {
        return DBHandler.getEditableCourses(this.login);
    }

    public List<String> getFiles() throws RemoteException {
        return DBHandler.getFiles(this.login);
    }

    public List<String> getFilesInCourse(String course) throws RemoteException {
        return DBHandler.getFilesInCourse(this.login,course);
    }

    public void addListener(String listener, String course) throws RemoteException {
        DBHandler.addListener(login,listener,course);
    }

    public synchronized void upload(String course ,String file, byte[] data) throws RemoteException {
        DBHandler.addFile(login,course,file);
        File f = new File(CoursesStoragePath.getDir() + File.separator + course + File.separator + file);
        f.getParentFile().mkdirs();
        try{
            OutputStream os = new FileOutputStream(f);
            os.write(data);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong on the server side"); //Don't give the client info on what went wrong but notify him of it
        }
    }

    public void createCourse(String courseName) throws RemoteException {
        DBHandler.createCourse(login,courseName);
    }
}
