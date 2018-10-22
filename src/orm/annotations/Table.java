package orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Used to specify name of the database table to which the entity is being mapped.
 * </p>
 *
 * <b>name</b> - table name.
 * <br>
 * <b>schema</b> (optional) - specifies the schema to which the table belongs to.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    String name();
    String schema() default "";
}
