import model.EntityStudent;
import orm.ORM;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;


public class TestClass {

    //Aspekt da se koristi kod rada sa JDBC-em nad bazom
    //Aspek da se koristi kad se pokrene konstruktor ORM-a
    //Zatim skenirati sve klase u model paketu da bi ih napravili preko JDBC-a
    //Update??

    public static void main(String[] args) {

        ORM orm = new ORM();

        EntityStudent student = new EntityStudent();

        student.setFirstName("Stefan");
        student.setLastName("Cvetic");
        student.setNoOfPassedExams(23);


        //orm.update(student);
        orm.persist(student);

//        orm.delete(student);
    }
}
