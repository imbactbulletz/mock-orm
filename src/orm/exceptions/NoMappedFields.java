package orm.exceptions;

public class NoMappedFields extends Exception {
    public NoMappedFields(String message){
        super(message);
    }
}
