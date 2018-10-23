package orm.exceptions;

public class EntityNotFound extends Exception{
    public EntityNotFound(String message){
        super(message);
    }
}
