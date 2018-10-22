package orm.annotations;

import java.lang.annotation.*;

/**
 * <p>Marks the class as a persistable Entity. </p>
 * <b>name</b> (optional) - specifies the name of the table to which the entity will be mapped. If not provided the table name will be
 * derived from the name of the class that is annotated by this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Entity {
    String name() default "";
}
