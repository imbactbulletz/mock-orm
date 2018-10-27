package model;

import orm.annotations.Column;
import orm.annotations.Entity;
import orm.annotations.JoinColumn;
import orm.annotations.ManyToOne;


@Entity
public class CommentEntity extends BasicEntity {

    @ManyToOne
    @JoinColumn(name = "ID")
    private PostEntity post;

    @Column(name="content")
    private String content;



    // getters and setters


    public PostEntity getPost() {
        return post;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public void setId(int id) {
        super.setId(id);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
