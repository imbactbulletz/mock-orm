import model.EntityStudent;
import orm.ORM;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class TestClass {

    public static void main(String[] args) {

        ORM orm = new ORM();

        EntityStudent student = new EntityStudent();

        student.setFirstName("Stefan");
        student.setLastName("Cvetic");
        student.setNoOfPassedExams(23);

        orm.persist(student);

    }
}
