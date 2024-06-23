package ru.emil.tgvectorization.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(schema = "base", name = "user_chat")
@Getter
@Setter
public class TgUserChat {

    @Column(name = "user_chat_id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_chat_id_generator")
    @SequenceGenerator(name="user_chat_id_generator", sequenceName = "base.user_chat_user_chat_id_seq", allocationSize=1)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "chat_id")
    private Long chatId;
}
