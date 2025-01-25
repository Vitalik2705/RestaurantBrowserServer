package com.coursework.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "dining_table")
@Data
public class DiningTable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "table_id")
  private Long tableId;

  @Column(name = "capacity", nullable = false)
  private int capacity;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "restaurant_id", nullable = false)
  private Restaurant restaurant;

  @OneToMany(mappedBy = "diningTable", cascade = CascadeType.ALL)
  private List<Reservation> reservations;

}

