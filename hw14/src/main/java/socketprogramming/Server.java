package socketprogramming;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws Exception {

        ServerSocket server=new ServerSocket(777);
        System.out.println("Server started");
        System.out.println("Waiting for a client ...");

        Socket socket=server.accept();
        System.out.println("Client accepted");

        PrintStream printStream=new PrintStream(socket.getOutputStream());
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));

        BufferedReader readForConsole=new BufferedReader(new InputStreamReader(System.in));

        String request="";
        while ( (request=bufferedReader.readLine() )!=null ){

            System.out.println(request);

            String response=readForConsole.readLine();
            printStream.println(response);
        }

        printStream.close();
        bufferedReader.close();
        readForConsole.close();
    }

}

