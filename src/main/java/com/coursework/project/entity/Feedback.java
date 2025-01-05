package com.coursework.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @Column(name = "rating", nullable = false)
    private double rating;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String description;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "advantages", columnDefinition = "TEXT")
    private String advantages;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "disadvantages", columnDefinition = "TEXT")
    private String disadvantages;


    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
