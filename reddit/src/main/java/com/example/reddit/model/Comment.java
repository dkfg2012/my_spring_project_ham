package com.example.reddit.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;


@Data //such that we do not need getter and setter for our attributes
@AllArgsConstructor //create the class constructor with all attributes as the constructor args, e.g. public Comment(Long id, String text ...){this.id = id; this.text = text ...}, with this annotation, we do not need to write the constructor
@NoArgsConstructor //create class constructor without args, e.g. public Comment(){}
//with allargs and noargs, we can create a comment with args Comment(id, text, post ...) and a comment without args Comment() (use default id, text ...)
@Entity //POJOs representing data that can be persisted to the database, default name will be the class name in lowercase
public class Comment {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotEmpty
    private String text;

    @ManyToOne(fetch = LAZY) //LAZY = fetch when needed
    @JoinColumn(name = "postId", referencedColumnName = "postId") //mark the column that join with other entity
    private Post post;

    private Instant createdDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;
}
