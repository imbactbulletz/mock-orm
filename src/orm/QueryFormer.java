package orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryFormer {

    /**
     * <p>Forms an INSERT query for insertion into the database.</p>
     * @param tableName  Name of the table that we're using for insertion
     * @param map  Map that contains column names and values
     * @return
     */
    public String formInsertQuery(String tableName, Map map){
        // creating a list of column names out of map keys
        List columnNames = new ArrayList<String>(map.keySet());

        StringBuilder stringBuilder = new StringBuilder("INSERT INTO " + tableName + " (");

        // makes a string that looks like "INSERT INTO TABLE_NAME (COL1,COl2,COl3)"
        for(int i = 0; i < columnNames.size() - 1; i++){
            stringBuilder.append(columnNames.get(i));
            stringBuilder.append(",");
        }

        // makes a string that looks like "INSERT INTO TABLE NAME (COL1,COL2,COL3) VALUES ("
        stringBuilder.append(columnNames.get(columnNames.size()-1));
        stringBuilder.append(") VALUES (");


        // makes a string that looks like "INSERT INTO TABLE NAME (COL1,COL2,COL3) VALUES (VAL1, VAL2, VAL3)"
        for(int i = 0; i <= columnNames.size() - 1; i++) {

            Object columnValue = map.get(columnNames.get(i));

            // if columnValue is a String then we need to format it properly
            if(columnValue instanceof String){
                stringBuilder.append("'");
                stringBuilder.append(columnValue);
                stringBuilder.append("'");
            } else {
                // if columnValue is some other type then we're just appending it to the stringBuilder
                stringBuilder.append(columnValue);
            }

            // appending "," for all entries but the last
            if(i < columnNames.size() - 1){
                stringBuilder.append(",");
            } else {
                // closing the query with a bracket
                stringBuilder.append(")");
            }
        }


        return stringBuilder.toString();
    }

    /**
     * <p>Forms a DELETE query.</p>
     * @param tableName Name of the table that DELETE will be performed on.
     * @param columnName Name of the conditional column that the query will use.
     * @param columnValue Value that a record in the column needs to have in order to be deleted.
     * @return A formed query.
     */
    public String formDeleteQuery(String tableName, String columnName, Object columnValue){

        if (columnValue instanceof String){
            columnValue = "'"+columnValue+"'";
        }

        return "DELETE FROM " + tableName + " WHERE " + columnName + " = " + columnValue;
    }


    /**
     * Forms a SELECT query.
     * <p><b>Example of such a query:</b> "SELECT A, B FROM C WHERE D = E AND F = G". A and B are columns for selection while D,E,F and G are conditional columns and values.</p>
     * @param tableName Name of the table that SELECT needs to be performed on. (C from the above example)
     * @param columnsForSelection List of column names that need to be selected. (A and B from the above example)
     * @param conditionalColumnNamesAndValues Map of column names and their values which serve as a criteria (condition) for SELECT clause. (D,E,F and G from the above example)
     * @return a complete SELECT query.
     */
    public String formSelectQuery(String tableName, List<String> columnsForSelection, Map<String, Object> conditionalColumnNamesAndValues){
        StringBuilder stringBuilder = new StringBuilder("SELECT ");

        // appending the columns that need to be selected
        for(int i = 0; i < columnsForSelection.size(); i++){
            stringBuilder.append(columnsForSelection.get(i));

            if(i < columnsForSelection.size() -1){
                stringBuilder.append(", ");
            }
        }

        // appending table name part
        stringBuilder.append(" FROM ");
        stringBuilder.append(tableName);

        // appending the conditional columns part
        stringBuilder.append(" WHERE ");

        // converting the keySet to an ArrayList so we can tell at which iteration we're exactly
        List<String> conditionalColumnNames = new ArrayList<>();
        conditionalColumnNames.addAll(conditionalColumnNamesAndValues.keySet());

        for(int i = 0;  i < conditionalColumnNames.size(); i++){

            String columnName = conditionalColumnNames.get(i);
            Object columnValue = conditionalColumnNamesAndValues.get(columnName);


            stringBuilder.append(columnName);
            stringBuilder.append(" = ");

            // single-quoting columnValue
            if(columnValue instanceof String){
                stringBuilder.append("'");
                stringBuilder.append(columnValue);
                stringBuilder.append("'");
            } else {
                // columnValue isn't a String
                stringBuilder.append(columnValue);
            }

            // appending AND if not we're on the last column name
            if(i < conditionalColumnNames.size() -1){
                stringBuilder.append(" AND ");
            }
        }

        return stringBuilder.toString();
    }
}
