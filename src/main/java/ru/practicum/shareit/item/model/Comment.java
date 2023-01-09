package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;
import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode (of = "id")
@Entity
@Table(name = "comments")

public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    @Column(name = "comment_text", nullable = false, length = 4000)
    private String text;
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_Id")
    private Item item;
    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;
    @Column(name = "insert_date")
    private LocalDateTime created;
}
