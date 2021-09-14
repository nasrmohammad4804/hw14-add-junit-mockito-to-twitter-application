package socketprogramming;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 777);
        System.out.println("Connected");

        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        BufferedReader readForConsole = new BufferedReader(new InputStreamReader(System.in));

        String str="";
        while (!(str=readForConsole.readLine()).equals("exit")){

            dataOutputStream.writeBytes(str+"\n");
            String responseResult= bufferedReader.readLine();
            System.out.println(responseResult);
        }
        readForConsole.close();
        bufferedReader.close();
        dataOutputStream.close();

    }
}

