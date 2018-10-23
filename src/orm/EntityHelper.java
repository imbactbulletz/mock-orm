package orm;

import orm.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityHelper {

    private ClassHelper classHelper;

    public EntityHelper() {
        classHelper = new ClassHelper();
    }

    /**
     * <p>Checks whether the object's class is annotated as an entity.</p>
     * @param object Object whose class is being checked.
     * @return <b>true</b> if it is an entity, <b>false</b> if it isn't.
     */
    public boolean isEntity(Object object){
        Class<?> clazz = object.getClass();

        Annotation[] classAnnotations = clazz.getDeclaredAnnotations();

        for(Annotation classAnnotation : classAnnotations){
            if(classAnnotation instanceof Entity){
                return true;
            }
        }

        return false;
    }

    public String findTableName(Class<?> clazz){
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
     * Gets column names and values from a list of classes and an object from which the names and values are extracted.
     * @param classes Classes that contain fields.
     * @param object Object that contains field values.
     * @return A map of column names and values.
     */
    public Map<Object,Object> getColumnNamesAndValuesForClasses(List<Class<?>> classes, Object object){
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

    public Field findIdField(Class clazz){

        List<Class<?>> classes = classHelper.getSuperClasses(clazz);
        classes.add(clazz);

        Field idField = null;

        for(int i = classes.size() - 1; i >= 0; i--){
            Field[] fieldList = classes.get(i).getDeclaredFields();
            for (Field field: fieldList){
                Annotation[] annotationList = field.getDeclaredAnnotations();
                for (Annotation annotation: annotationList){
                    if (annotation instanceof ID){
                        idField = field;
                        break;
                    }
                }
            }
        }

        return idField;
    }

    /**
     * <p> Maps column names and values by iterating through all of the annotated fields of a class.</p>
     * @param clazz Class that has annotated fields.
     * @param object Instance of a clazz that contains values.
     * @return
     */
    public Map getColumnNamesAndValues(Class<?> clazz, Object object) {
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
}
