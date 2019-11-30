import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command {
    final String help = "h - help\n" +
            "c - get all courses\n" +
            "e - get your courses\n" +
            "f <course> - get files in course, if <course> is empty, get all accessible files\n" +
            "u <course> <file> <path> - upload a <file> to a <course>, found locally with <path>\n" +
            "d <course> <file> <path> - download a <file> from a <course>, and save locally in <path>\n" +
            "n <course> - new course\n" +
            "a <listener> <course> - add a listener with a <listener> username to a <course>\n" +
            "q - quit\n" +
            "l - logout";

    ServerInterface stub;
    List<String> args;
    Command(String line,ServerInterface stub){
        args = Arrays.asList(line.split(" "));
        this.stub=stub;
    }

    boolean execute() throws IOException {
        if(args.get(0).length()!=1){
            throw new RuntimeException("Syntax Error: wrong function name: "+args.get(0));
        }
        switch (args.get(0).charAt(0)){
            case 'h':
                h();
                break;
            case 'c':
                c();
                break;
            case 'e':
                e();
                break;
            case 'f':
                f();
                break;
            case 'u':
                u();
                break;
            case 'd':
                d();
                break;
            case 'n':
                n();
                break;
            case 'a':
                a();
                break;
            case 'l':
                return false;
            case 'q':
                System.exit(0);
            default:
                throw new RuntimeException("Syntax Error: wrong function name: "+args.get(0));

        }

        return true;
    }

    private void a() throws RemoteException{
        checkNumArg(3);
        stub.addListener(args.get(1),args.get(2));
    }

    private void h(){
        checkNumArg(1);
        System.out.println(help);
    }

    private void c() throws RemoteException {
        checkNumArg(1);
        stub.getAvailable().forEach(System.out::println);
    }

    private void e() throws RemoteException {
        checkNumArg(1);
        stub.getEditable().forEach(System.out::println);
    }

    private void f() throws RemoteException {
        List <Integer> argNum = new ArrayList<>();
        argNum.add(1);
        argNum.add(2);
        checkNumArg(argNum);
        if(args.size()==1){
            stub.getFiles().forEach(System.out::println);
        }
        else{
            stub.getFilesInCourse(args.get(1)).forEach(System.out::println);
        }
    }

    private void u() throws IOException {
        checkNumArg(4);
        File f =new File(args.get(3));
        stub.upload(args.get(1),args.get(2), Files.readAllBytes(Paths.get(f.getAbsolutePath())));
    }

    private void d() throws IOException {
        checkNumArg(4);
        File f = new File(args.get(3));
        try {
            OutputStream outputStream = new FileOutputStream(f);
            outputStream.write(stub.download(args.get(2),args.get(1)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void n()throws RemoteException{
        checkNumArg(2);
        stub.createCourse(args.get(1));
    }

    private void checkNumArg(int x){
        if(args.size()!=x){
            throw new RuntimeException("Syntax Error: this function takes "+x+" arguments");
        }
    }

    private void checkNumArg(List<Integer> x){
        for(int y: x){
            if(args.size()==y){
                return;
            }
        }
        throw new RuntimeException("Syntax Error: this function takes "+x+" arguments");
    }
}
