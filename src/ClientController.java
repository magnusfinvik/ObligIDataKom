import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;


public class ClientController implements Initializable{

    @FXML
    TextField messageArea;
    @FXML
    TextArea messageOutput;
    @FXML
    TextField nameTag;
    @FXML
    Button connect;

    private SSLSocket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    @FXML
    public void handle() {
        try{
            String message = messageArea.getText().trim();
            String name = nameTag.getText().trim();
            Message m = new Message(name, message);
            toServer.writeObject(m);

        } catch (UnknownHostException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void streamCreatorAndMore() {
        try {
            System.out.println("0");
            toServer = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("1");
            fromServer = new ObjectInputStream(socket.getInputStream());
            System.out.println("2");

            while (true) {
                Message outPrint = (Message) fromServer.readObject();
                Platform.runLater(() -> {
                    String name = outPrint.getName();
                    String mess = outPrint.getMessage();
                    messageOutput.appendText(name + ": " + mess + "\n");
                    messageArea.clear();
                });
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void connect() {
        try {
            System.setProperty("javax.net.ssl.trustStore", "c:\\users\\magnusfinvik\\keystore");
            SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) ssf.createSocket("localhost", 8000);
           // socket.startHandshake();
            new Thread(() -> {
                    streamCreatorAndMore();
            }).start();
        } catch (Exception e){
            System.out.println("erroooorrrr");
        }
    }
}
