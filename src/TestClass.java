import model.PostEntity;
import model.CommentEntity;
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

        PostEntity post = new PostEntity();

        CommentEntity comment1 = new CommentEntity();
        comment1.setContent("Comment 1");

        CommentEntity comment2 = new CommentEntity();
        comment2.setContent("Comment 2 ");

        post.getComments().add(comment1);
        post.getComments().add(comment2);

        orm.persist(post);

    }
}
