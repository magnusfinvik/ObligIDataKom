import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;


public class Server extends Application {
    ArrayList<ObjectOutputStream> list = new ArrayList<>();

    Stage primaryStage;
    private int clientNumber = 0;
    private TextArea textArea = new TextArea();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        initialView();
    }


    private void initialView() {
        try {
            Scene scene = new Scene(new ScrollPane(textArea), 450, 200);
            primaryStage.setTitle("ChatServer");
            primaryStage.setScene(scene);
            primaryStage.show();

            new Thread(() -> {
                try {

                    System.setProperty("javax.net.ssl.trustStore", "c:\\Users\\magnusfinvik\\keystore");
                    System.setProperty("javax.net.ssl.keyStorePassword", "password");

                    SSLServerSocketFactory serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

                    ServerSocket serverSocket = serverSocketFactory.createServerSocket(8000);
                    Platform.runLater(()-> {
                        textArea.appendText("MultiThreadServer started at "
                                + new Date() + '\n');
                    });


                    while (true) {
                        Socket socket = serverSocket.accept();
                        new Thread(() -> {
                           new ClientHandler(socket);

                        });

                        clientNumber++;

                        Platform.runLater(() -> {
                            textArea.appendText("Starting thread for client " + clientNumber +
                                    " at " + new Date() + '\n');
                        });

                        new Thread(new ClientHandler(socket)).start();
                    }
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }).start();
        }finally {

        }
        }

    public static void main(String[]args) {
        launch(args);
    }



    private class ClientHandler implements Runnable {
        private Socket socket;
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try{
                ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputToClient = new ObjectOutputStream(socket.getOutputStream());
                list.add(outputToClient);

                while(true){
                    Message message = (Message) inputFromClient.readObject();
                    for(int i = 0; i < list.size(); i++){
                        list.get(i).writeObject(message);
                    }

                    Platform.runLater(() -> {
                        textArea.appendText("message received from client \n");
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
