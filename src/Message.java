import java.io.Serializable;


public class Message implements Serializable{
    private String name;
    private String message;

    public Message(String name, String message){
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
