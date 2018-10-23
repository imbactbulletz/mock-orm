package orm;

import orm.annotations.MappedSuperclass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassHelper {

    /**
     * <p>Gets all superclasses for a certain class.</p>
     * @param clazz The class whose superclasses we want to find.
     * @return List of classes that are superclasses to the passed class argument.
     */
    public List<Class<?>> getSuperClasses(Class clazz){
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
}
