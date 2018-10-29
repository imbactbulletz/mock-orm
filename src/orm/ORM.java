package orm;


import orm.exceptions.EntityNotFound;
import orm.exceptions.IDNotFound;
import orm.exceptions.NoColumnsFound;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.*;

/**
 * Represents an Object Relation Mapper that maps certain objects into database tables. It can persist/load/update/delete
 * entities from the database.
 */
public class ORM {

    private QueryFormer queryFormer;
    private EntityHelper entityHelper;
    private ClassHelper classHelper;
    private DatabaseConnector databaseConnector;

    public ORM(){
        queryFormer = new QueryFormer();
        entityHelper = new EntityHelper();
        classHelper = new ClassHelper();
        databaseConnector = new DatabaseConnector();
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
            try {
                throw new EntityNotFound("Object of the class <" + clazz.getSimpleName() + "> cannot be persisted because it isn't an Entity.");
            } catch (EntityNotFound entityNotFound) {
                entityNotFound.printStackTrace();
                return;
            }
        }

        // trying to find field annotated with ID (entities without an ID annotation cannot be mapped)
        Field idField = entityHelper.findIdField(clazz);

        if(idField == null){
            try {
                throw new IDNotFound("Couldn't find the primary column field.");
            } catch (IDNotFound idNotFound) {
                idNotFound.printStackTrace();
                return;
            }
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


        // throws an exception if there are no columns other than GeneratedValue
        if(columnNamesAndValues.keySet().size() == 0){
            try {
                throw new NoColumnsFound("There must be at least one column not being a GeneratedValue.");
            } catch (NoColumnsFound noColumnsFound) {
                noColumnsFound.printStackTrace();
                return;
            }
        }

        // forming a query out of the map
        String query = queryFormer.formInsertQuery(tableName, columnNamesAndValues);


        // getting all GeneratedValue column names so that we can request their value after object's persistance
        List<String> generatedValuesColumnNames = entityHelper.getGeneratedValuesColumnNames(clazz);


        // found column names for generated values
        if(generatedValuesColumnNames != null && generatedValuesColumnNames.size() != 0){
            // making a select query and appending it to the insert query
            query = query + " " + queryFormer.formSelectQuery(tableName, generatedValuesColumnNames, columnNamesAndValues);
        }


        List<Object> results= databaseConnector.executeQuery(clazz, query);


        // check for oneToMany
        Field oneToManyField = entityHelper.getOneToManyField(clazz);

        if(oneToManyField != null) {

            // getting the generic type of oneToManyField - typically a List
            Type genericType = oneToManyField.getGenericType();
            List objectList = null;
            Class listClassType = null;

            // getting the objects contained in the list
            try {
                oneToManyField.setAccessible(true);
                // getting
                objectList = (List)oneToManyField.get(object);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


            // if generic type is parameterized
            if(genericType instanceof ParameterizedType) {

                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                // Getting the parameterized type T type of List<T>
                Type type = parameterizedType.getActualTypeArguments()[0];

                try {
                    listClassType = Class.forName(type.getTypeName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


            }


            // setting manyToOne field to reference the object
            Field manyToOneField = entityHelper.getManyToOneField(listClassType);

            for(Object o : objectList){
                manyToOneField.setAccessible(true);
                try {
                    manyToOneField.set(o, object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            String joinColumnName = entityHelper.getManyToOneColumnName(listClassType);




            Field primaryField = entityHelper.findIdField(clazz);

            primaryField.setAccessible(true);
            Object primaryFieldValue = null;

            try {
                primaryFieldValue = primaryField.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            tableName = entityHelper.findTableName(listClassType);

            superClasses = classHelper.getSuperClasses(listClassType);

            allClasses = new ArrayList<>();

            allClasses.addAll(superClasses);
            allClasses.add(listClassType);


            for(Object o : objectList){
                columnNamesAndValues = entityHelper.getColumnNamesAndValuesForClasses(allClasses, o);
                columnNamesAndValues.put(joinColumnName, primaryFieldValue);


                // throws an exception if there are no columns other than GeneratedValue
                if(columnNamesAndValues.keySet().size() == 0){
                    try {
                        throw new NoColumnsFound("There must be at least one column not being a GeneratedValue.");
                    } catch (NoColumnsFound noColumnsFound) {
                        noColumnsFound.printStackTrace();
                        return;
                    }
                }

                query = queryFormer.formInsertQuery(tableName, columnNamesAndValues);

                databaseConnector.executeQuery(listClassType, query);
            }

        }
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
            try {
                throw new EntityNotFound("Object of the class <" + clazz.getSimpleName() + "> cannot be deleted because it isn't an Entity.");
            } catch (EntityNotFound entityNotFound) {
                entityNotFound.printStackTrace();
                return;
            }
        }

        Field idField = entityHelper.findIdField(clazz);

        if (idField == null){
            try {
                throw new IDNotFound("Object of the class <" + clazz.getSimpleName() + "> cannot be deleted because it doesn't have ID field.");
            } catch (IDNotFound idNotFound) {
                idNotFound.printStackTrace();
                return;
            }
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
