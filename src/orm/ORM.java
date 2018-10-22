package orm;


import orm.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Represents an Object Relation Mapper that maps certain objects into database tables. It can persist/load/update/delete
 * entities from the database.
 */
public class ORM {

    /**
     * Inserts an entity into the database.
     *
     * @param object Entity that needs to be inserted.
     */
    public void persist(Object object) {

        // getting class object that represents object's class
        Class<?> clazz = object.getClass();

        // quitting if passed object isn't an entity
        if (!isEntity(object)) {
            System.err.println("Object of the class <" + clazz.getSimpleName() + "> cannot be persisted because it isn't an Entity.");
            return;
        }

        // represents name of the table to which the entity is mapped
        String tableName = findTableName(clazz);

        // list of super classes
        List<Class<?>> superClasses = getSuperClasses(clazz);

        // list of all the classes ( superclasses + object's class)
        List<Class<?>> allClasses = new ArrayList<>();

        allClasses.addAll(superClasses);
        allClasses.add(clazz);


        // getting column names and values as a map
        Map<Object, Object> columnNamesAndValues = getColumnNamesAndValuesForClasses(allClasses, object);

        // forming a query out of the map
        String query = formInsertQuery(tableName, columnNamesAndValues);


        System.out.println(query);
    }

    /**(Class<?>)Object) superClass
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

    }

    /**
     * <p> Maps column names and values by iterating through all of the annotated fields of a class.</p>
     * @param clazz Class that has annotated fields.
     * @param object Instance of a clazz that contains values.
     * @return
     */
    private Map getColumnNamesAndValues(Class<?> clazz, Object object) {
        Map map = new HashMap<>();



        Field[] clazzFields = clazz.getDeclaredFields();

        for (Field field : clazzFields) {
            Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
            boolean isGeneratedValue = false;

            // checking if the field is a generated value
            for (Annotation fieldAnnotation : fieldAnnotations) {
                if (fieldAnnotation instanceof GeneratedValue) {
                    isGeneratedValue = true;
                }
            }

            // skipping the field if it's auto generated
            if (isGeneratedValue) {
                continue;
            }


            for (Annotation fieldAnnotation : fieldAnnotations) {
                // checking elseif fields annotated with notnull are null
                if (fieldAnnotation instanceof NotNull) {
                    try {
                        // seting the access modifier of the field to public
                        field.setAccessible(true);

                        // if the field marked as NotNull contains a null value
                        if (field.get(object) == null) {
                            System.err.println("A property of an entity (" + field.getName() + ") which is marked as @NotNull contains a null value.");
                            return null;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                // we've ran into a column, getting the column name
                if (fieldAnnotation instanceof Column) {
                    Column column = (Column) fieldAnnotation;
                    String columnName = column.name();

                    try {
                        // setting the access modifier to public
                        field.setAccessible(true);

                        // getting the value of the field
                        Object columnValue = field.get(object);

                        // inserting the column name and column alue into the column names and values map
                        map.put(columnName, columnValue);

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

            }

        }


        return map;
    }

    /**
     * <p>Forms an INSERT query for insertion into the database.</p>
     * @param tableName  Name of the table that we're using for insertion
     * @param map  Map that contains column names and values
     * @return
     */
    private String formInsertQuery(String tableName, Map map){
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


    /**
     * <p>Checks whether the object's class is annotated as an entity.</p>
     * @param object Object whose class is being checked.
     * @return <b>true</b> if it is an entity, <b>false</b> if it isn't.
     */
    private boolean isEntity(Object object){
        Class<?> clazz = object.getClass();

        Annotation[] classAnnotations = clazz.getDeclaredAnnotations();

        for(Annotation classAnnotation : classAnnotations){
            if(classAnnotation instanceof Entity){
                return true;
            }
        }

        return false;
    }

    private String findTableName(Class<?> clazz){
        String tableName = "";

        Annotation[] clazzAnnotations = clazz.getDeclaredAnnotations();


        for (Annotation clazzAnnotation : clazzAnnotations) {
                Entity entity = (Entity) clazzAnnotation;

                // has name value
                if (!entity.name().isEmpty()) {
                    // setting table name to the annotated value
                    tableName = entity.name();
                } else {
                    // doesn't have an annotated name value

                    // setting table name to the entity's class name
                    tableName = clazz.getSimpleName();
                }

            // if the entity is annotated as a table
            if (clazzAnnotation instanceof Table) {
                Table table = (Table) clazzAnnotation;

                // setting table name to the annotated values

                // has schema name
                if(table.schema().equals("")) {
                    tableName = table.name();
                }
                else {
                    // doesn't have schema name
                    tableName = table.schema() + "." + table.name();
                }
            }
        }



        return tableName;
    }


    /**
     * <p>Gets all superclasses for a certain class.</p>
     * @param clazz The class whose superclasses we want to find.
     * @return List of classes that are superclasses to the passed class argument.
     */
    private List<Class<?>> getSuperClasses(Class clazz){
        List superClasses = new ArrayList<Class<?>>();

        // getting the current class' superclass
        Class<?> superClazz = clazz.getSuperclass();

        // checking superclasses for mapped fields (every class annotated with MappedSuperclass has mapped fields)
        while (superClazz != null) {
            // getting the annotations of the super class
            Annotation[] superClazzAnnotations = superClazz.getAnnotations();

            // checking if any of the annotations is a MappedSuperclass
            for (Annotation annotation : superClazzAnnotations) {

                // checking whether the annotation inherits some other class that has annotated fields
                if (annotation instanceof MappedSuperclass) {

                    // checking whether the superclass contains annotated fields
                    Field[] fields = superClazz.getDeclaredFields();
                    boolean hasMappedField = false;

                    // iterating through each field
                    for (Field field : fields) {
                        // checking if the field is annotated
                        if (field.getDeclaredAnnotations().length > 0) {
                            hasMappedField = true;
                            break;
                        }
                    }

                    // if MappedSuperclass has no mapped fields then we're quitting
                    if (!hasMappedField) {
                        System.err.println("MappedSuperclass has no mapped fields.");
                        break;
                    }

                    // adding superClass to the list of superclasses (parent superclasses
                    // are always inserted to the beginning of the ArrayList)
                    superClasses.add(0, superClazz);
                }
            }

            //looping over to the super classes' parent class
            superClazz = superClazz.getSuperclass();
        }


        return superClasses;
    }

    /**
     * Gets column names and values from a list of classes and an object from which the names and values are extracted.
     * @param classes Classes that contain fields.
     * @param object Object that contains field values.
     * @return A map of column names and values.
     */
    private Map<Object,Object> getColumnNamesAndValuesForClasses(List<Class<?>> classes, Object object){
        Map map = new HashMap<>();


        // getting column names and values for each class
        for(Class clazz : classes){
            Map columnNamesAndValues = getColumnNamesAndValues(clazz, object);

            if(columnNamesAndValues == null){
                return null;
            }

            // combining maps
            map.putAll(columnNamesAndValues);
        }

        return map;
    }
}
