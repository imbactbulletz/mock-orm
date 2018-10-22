package model;

import orm.annotations.Column;
import orm.annotations.Entity;

@Entity
public class EntityStudent extends EntityBasic {

    @Column(name="FIRST_NAME")
    private String firstName;


    @Column(name="LAST_NAME")
    private String lastName;

    @Column(name="PASSED_EXAMS")
    private int noOfPassedExams;


    public EntityStudent(){
        super();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getNoOfPassedExams() {
        return noOfPassedExams;
    }

    public void setNoOfPassedExams(int noOfPassedExams) {
        this.noOfPassedExams = noOfPassedExams;
    }
}
