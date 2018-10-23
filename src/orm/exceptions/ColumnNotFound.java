package orm.exceptions;

public class ColumnNotFound extends Exception {
    public ColumnNotFound(String message){
        super(message);
    }
}
