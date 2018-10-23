package orm;

import orm.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
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

    /**
     * <p>Retrieves a table name for a given class. If a Table annotation is present, then table name will be the same as of the annotation's value.
     * If Table annotation isn't present, then value of Entity annotation is looked up. If it is empty as well, then the simplified class name is taken
     * as the name of the table.</p>
     * @param clazz A class which represents an Entity, whose table name needs to be found.
     * @return Name of the database table.
     */
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
    public Map<String,Object> getColumnNamesAndValuesForClasses(List<Class<?>> classes, Object object){
        Map map = new HashMap<String, Object>();


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

    /**
     * Retrieves the field which is marked as identifying column (primary key) in a class.
     * @param clazz Entity class whose identifying field we need to find.
     * @return A Field that is an identifying column.
     */
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
    public Map<String, Object> getColumnNamesAndValues(Class<?> clazz, Object object) {
        Map map = new HashMap<String, Object>();

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
     * <p>Iterates through a class and all of its superclasses searching for fields that are marked as GeneratedValue.
     * Once a field marked as GeneratedValue is found, then a lookup is made for Column annotation which contains the column name.</p>
     * @param clazz Class that needs to be inspected.
     * @return A list of column names that represent GeneratedValue fields.
     */
    public List<String> getGeneratedValuesColumnNames(Class<?> clazz){

        // a list of column names that belong to fields marked as GeneratedValue
        List<String> generatedValuesColumnNames = new ArrayList<>();

        // putting together superclasses and current class so that we can search for
        // columnNames in all of the relevant classes
        List<Class<?>> superClasses = classHelper.getSuperClasses(clazz);
        List<Class<?>> allClasses = new ArrayList<>();

        allClasses.addAll(superClasses);
        allClasses.add(clazz);

        for(Class<?> cls : allClasses){

            Field[] clsFields = cls.getDeclaredFields();

            for(Field clsField : clsFields){
                Annotation[] clsFieldAnnotations = clsField.getDeclaredAnnotations();

                boolean isGeneratedValue = false;

                // checking for generatedValue annotation
                for(Annotation clsFieldAnnotation : clsFieldAnnotations){
                    if(clsFieldAnnotation instanceof GeneratedValue){
                        isGeneratedValue = true;
                        break;
                    }
                }

                // found a GeneratedValue annotation
                if(isGeneratedValue){
                    boolean isColumn = false;

                    // searching for Column annotation
                    for(Annotation clsFieldAnnotation : clsFieldAnnotations){
                        // found the Column annotation, getting the column name
                        if(clsFieldAnnotation instanceof Column){
                            isColumn = true;
                            Column column = (Column) clsFieldAnnotation;

                            // adding column name to the list of the column names
                            generatedValuesColumnNames.add(column.name());
                        }
                    }

                    // didn't find a column name, field with GeneratedValue wasn't annotated -> big NO NO
                    if(!isColumn){
                        System.err.println("Column name for a GeneratedValue could not be found. <<" + clazz.getSimpleName() + ">>");
                        return null;
                    }
                }
            }
        }

        return generatedValuesColumnNames;
    }

}
