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
        Class<?> clazz = object.getClass();
        Annotation[] annotations = clazz.getAnnotations();


        // represents name of the table to which the entity is mapped
        String tableName = "";
        // used to check whether passed argument is an entity or not
        boolean isEntity = false;


        for (Annotation annotation : annotations) {
            if (annotation instanceof Entity) {
                // passed argument is an Entity
                isEntity = true;

                Entity entity = (Entity) annotation;

                // has name value
                if (!entity.name().isEmpty()) {
                    // setting table name to the annotated value
                    tableName = entity.name();
                }

                // doesn't have an annotated name value
                else {
                    // setting table name to the entity's class name
                    tableName = clazz.getSimpleName();
                }
            }

            // if the entity is annotated as a table
            if (annotation instanceof Table) {
                Table table = (Table) annotation;

                // setting table name to the annotated values

                // has schema name
                if(table.schema().equals(""))
                    tableName = table.name();
                // doesn't have schema name
                else {
                    tableName = table.schema() + "." + table.name();
                }
            }
        }


        // quitting if passed object isn't an entity
        if (!isEntity) {
            System.err.println("Object of the class <" + clazz.getSimpleName() + "> cannot be persisted because it isn't an Entity.");
            return;

        }


        // holds query' column names as the hashmap keys and column values as hashmap values
        Map map = new HashMap<Object, Object>();
        // list of super classes
        ArrayList<Class<?>> superClasses = new ArrayList<>();
        // super class of the entity
        Class<?> superClazz = clazz.getSuperclass();


        // checking superclasses for mapped fields (every class annotated with MappedSuperclass has mapped fields)
        while (superClazz != null) {
            // getting the annotations of the super class
            Annotation[] superClazzAnnotations = superClazz.getAnnotations();

            // checking if any of the annotations is a MappedSuperclass
            for (Annotation annotation : superClazzAnnotations) {
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


        // getting column names and values for superclasses
        for (Class<?> superClass : superClasses) {
            Map columnNamesAndValues = getColumnNamesAndValues(superClass, object);

            // an error happened
            if (columnNamesAndValues == null) {
                return;
            }

            map.putAll(columnNamesAndValues);
        }


        // getting column names and values for current class
        Map columnNamesAndValues = getColumnNamesAndValues(clazz, object);


        // an error happened
        if (columnNamesAndValues == null) {
            return;
        }


        // combining maps
        map.putAll(columnNamesAndValues);

        String query = formInsertQuery(tableName, map);

        System.out.println(query);
    }

    /**
     * Updates a database entity.
     *
     * @param object Entity that needs to be updated.
     */
    public void update(Object object) {

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
        Map map = new HashMap<Object, Object>();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
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
                // checking if fields annotated with notnull are null
                if (fieldAnnotation instanceof NotNull) {
                    try {
                        // ako je private modifier stavljamo ga na public
                        field.setAccessible(true);
                        if (field.get(object) == null) {
                            System.err.println("A property of an entity (" + field.getName() + ") which is marked as @NotNull contains a null value.");
                            return null;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                // we've run into a column, getting the column name
                if (fieldAnnotation instanceof Column) {
                    Column column = (Column) fieldAnnotation;
                    String columnName = column.name();

                    try {
                        // ako je private modifier stavljamo ga na public
                        field.setAccessible(true);
                        Object columnValue = field.get(object);

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
        for(int i = 0; i < columnNames.size() - 2; i++){
            stringBuilder.append(columnNames.get(i) + ",");
        }

        // makes a string that looks like "INSERT INTO TABLE NAME (COL1,COL2,COL3) VALUES ("
        stringBuilder.append(columnNames.get(columnNames.size()-1) + ") VALUES (");


        // makes a string that looks like "INSERT INTO TABLE NAME (COL1,COL2,COL3) VALUES (VAL1, VAL2, VAL3)"
        for(int i = 0; i < columnNames.size() - 2; i++){
            stringBuilder.append(map.get(columnNames.get(i)).toString() + ",");
        }


        stringBuilder.append(map.get(columnNames.get(columnNames.size()-1)).toString() + ")");


        return stringBuilder.toString();
    }
}
