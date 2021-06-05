package sample.protocol;

import java.io.Serializable;

public class Protocol implements Serializable {
    public static final long serialVersionUID = 1;

    public Protocol() {

    }

    public void setMessage(float message) {
        this.message = message;
    }
    public float message;

}
