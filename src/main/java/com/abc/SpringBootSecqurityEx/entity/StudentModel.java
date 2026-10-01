package com.abc.SpringBootSecqurityEx.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="student")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StudentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;

    private String studentName;

    private String address;

    private Float semisterCost;

    private Integer semisterNo;

}
