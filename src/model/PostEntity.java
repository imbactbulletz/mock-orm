package model;

import orm.annotations.Column;
import orm.annotations.Entity;
import orm.annotations.OneToMany;

import java.util.List;

@Entity()
public class PostEntity extends BasicEntity {

    @Column(name="title")
    private String title;

    @OneToMany(mappedBy = "post")
    private List<CommentEntity> comments;



    // getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public void setId(int id) {
        super.setId(id);
    }
}
