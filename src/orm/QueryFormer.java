package orm;

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
        List<String> columnNames = new ArrayList<String>(map.keySet());

        StringBuilder stringBuilder = new StringBuilder("INSERT INTO " + tableName + " (");

        // makes a string that looks like "INSERT INTO TABLE_NAME (COL1,COl2,COl3)"
        for(int i = 0; i < columnNames.size() - 1; i++){
            stringBuilder.append(columnNames.get(i) + ",");
        }

        // makes a string that looks like "INSERT INTO TABLE NAME (COL1,COL2,COL3) VALUES ("
        stringBuilder.append(columnNames.get(columnNames.size()-1) + ") VALUES (");


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

    public String formDeleteQuery(String tableName, String columnName, Object columnValue){

        if (columnValue instanceof String){
            columnValue = "'"+columnValue+"'";
        }

        return "DELETE FROM " + tableName + " WHERE " + columnName + " = " + columnValue;
    }

}
