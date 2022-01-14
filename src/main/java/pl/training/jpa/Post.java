package pl.training.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@NamedEntityGraph(name = Post.WITH_COMMENTS, attributeNodes = {
        @NamedAttributeNode("comments")
})
@NamedQuery(name = Post.GET_ALL_EAGER, query = "select p from Post p join fetch p.comments pc")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    public static final String GET_ALL_EAGER = "POSTS_GET_ALL_EAGER";
    public static final String WITH_COMMENTS = "POSTS_WITH_COMMENTS";

    @GeneratedValue
    @Id
    private Long id;
    private String title;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 2_000)
    private String content;
    @Column(unique = true)
    private String slug;
    @JoinColumn(name = "POST_ID")
    @OneToMany(fetch = FetchType.LAZY) //, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public void addComments(Comment ... comments) {
        this.comments.addAll(Arrays.asList(comments));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        var post = (Post) other;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
