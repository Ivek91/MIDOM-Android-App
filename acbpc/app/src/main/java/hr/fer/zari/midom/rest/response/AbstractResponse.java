package hr.fer.zari.midom.rest.response;

abstract public class AbstractResponse<T> {

    private int code;
    private T message;

    public AbstractResponse() {
    }

    public AbstractResponse(int code, T message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AbstractResponse{" +
                "code=" + code +
                ", message=" + message +
                '}';
    }
}
