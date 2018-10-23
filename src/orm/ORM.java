package orm;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Represents an Object Relation Mapper that maps certain objects into database tables. It can persist/load/update/delete
 * entities from the database.
 */
public class ORM {

    private QueryFormer queryFormer;
    private EntityHelper entityHelper;
    private ClassHelper classHelper;

    public ORM(){
        queryFormer = new QueryFormer();
        entityHelper = new EntityHelper();
        classHelper = new ClassHelper();
    }

    /**
     * Inserts an entity into the database.
     *
     * @param object Entity that needs to be inserted.
     */
    public void persist(Object object) {


        // getting class object that represents object's class
        Class<?> clazz = object.getClass();

        // quitting if passed object isn't an entity
        if (!entityHelper.isEntity(object)) {
            System.err.println("Object of the class <" + clazz.getSimpleName() + "> cannot be persisted because it isn't an Entity.");
            return;
        }

        // represents name of the table to which the entity is mapped
        String tableName = entityHelper.findTableName(clazz);

        // list of super classes
        List<Class<?>> superClasses = classHelper.getSuperClasses(clazz);

        // list of all the classes ( superclasses + object's class)
        List<Class<?>> allClasses = new ArrayList<>();

        allClasses.addAll(superClasses);
        allClasses.add(clazz);


        // getting column names and values as a map
        Map<String, Object> columnNamesAndValues = entityHelper.getColumnNamesAndValuesForClasses(allClasses, object);

        // forming a query out of the map
        String query = queryFormer.formInsertQuery(tableName, columnNamesAndValues);


        // getting all GeneratedValue column names so that we can request their value after object's persistance
        List<String> generatedValuesColumnNames = entityHelper.getGeneratedValuesColumnNames(clazz);


        // found column names for generated values
        if(generatedValuesColumnNames != null && generatedValuesColumnNames.size() != 0){
            // making a select query and appending it to the insert query
            query = query + " " + queryFormer.formSelectQuery(tableName, generatedValuesColumnNames, columnNamesAndValues);
        }


        System.out.println(query);
    }

    /**
     * Updates a database entity.
     *
     * @param object Entity that needs to be updated.
     */
    public void update(Object object) {
        Class<?> clazz = object.getClass();
        Annotation[] annotations = clazz.getAnnotations();

    }

    /**
     * Retrieves an entity from the database as an object.
     *
     * @param entityClass Class of the entity that needs to be retrieved.
     * @param args        Additional parameters
     * @return entity as an Object.
     */
    public Object load(Class entityClass, Object... args) {
        return null;
    }


    /**
     * Removes an entity from the database.
     *
     * @param object The entity that needs to be removed.
     */
    public void delete(Object object) {
        Class<?> clazz = object.getClass();

        if (!entityHelper.isEntity(object)) {
            System.err.println("Object of the class <" + clazz.getSimpleName() + "> cannot be deleted because it isn't an Entity.");
            return;
        }

        Field idField = entityHelper.findIdField(clazz);

        if (idField == null){
            System.err.println("Object of the class <" + clazz.getSimpleName() + "> cannot be deleted because it doesn't have ID field.");
            return;
        }

        Object idFieldValue = null;
        try {
            idField.setAccessible(true);
            idFieldValue = idField.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        String tableName = entityHelper.findTableName(clazz);
        String columnName = idField.getName();


        String deleteQuery = queryFormer.formDeleteQuery(tableName, columnName, idFieldValue);
        System.out.println(deleteQuery);

    }



}
