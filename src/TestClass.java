import model.CartEntity;
import model.ItemEntity;
import orm.ORM;


public class TestClass {

    //Aspekt da se koristi kod rada sa JDBC-em nad bazom
    //Aspek da se koristi kad se pokrene konstruktor ORM-a
    //Zatim skenirati sve klase u model paketu da bi ih napravili preko JDBC-a
    //Update??

    public static void main(String[] args) {

        ORM orm = new ORM();

//        StudentEntity student = new StudentEntity();
//
//        student.setFirstName("Stefan");
//        student.setLastName("Cvetic");
//        student.setNoOfPassedExams(23);
//
//
//        orm.update(student);
//        orm.persist(student);
//        orm.delete(student);

        CartEntity cart = new CartEntity();

        ItemEntity item = new ItemEntity();

        // exception, items je null u cart-u, resiti ili preko dependency injection ili rucno inicijalizovati
        cart.getItems().add(item);

        //orm.persist(cart);

    }
}
