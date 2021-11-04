package pl.training.jpa;

import pl.training.jpa.commons.EntityTest;

public class CommentTests extends EntityTest<Comment> {

    @Override
    protected Comment initializeEntity() {
        var comment = new Comment();
        comment.setValue("My comment");
        return comment;
    }

}
