package orm;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {

    /**
     * Reads a result set and retusn a list of objects that are read from the set.
     * @param clazz Type of objects that are expected to be contained in the ResultSet
     * @param query Query that needs to be executed
     * @return
     */
    public List<Object> executeQuery(Class clazz, String query){
        // todo jdbc insertion, reading of resultset

        System.out.println(query);

        return new ArrayList<>();
    }

    public int executeUpdate(String query){
        return -1;
    }
}
