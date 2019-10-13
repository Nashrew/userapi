package com.andrewn.userapi.model.users;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name="users",
        uniqueConstraints={
            @UniqueConstraint(columnNames = {"first_name", "last_name"})
        })
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @NotNull
    @Column(name="first_name")
    private String firstName;

    @NotNull
    @Column(name="last_name")
    private String lastName;

    public User(){}

    public User(@NotNull String firstName, @NotNull String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(Integer id, @NotNull String firstName, @NotNull String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Use the getters below to get rid of IDE errors if you don't want to or are unable to install and configure lombok

    //    public Integer getId() { return id; }
    //    public void setId(Integer id) { this.id = id; }
    //
    //    public String getFirstName() {
    //        return firstName;
    //    }
    //    public String getLastName() {
    //        return lastName;
    //    }
}
