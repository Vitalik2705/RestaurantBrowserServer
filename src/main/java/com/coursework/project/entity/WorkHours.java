package com.coursework.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "work_hours")
@Data
public class WorkHours {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "work_hours_id")
  private Long workHoursId;

  @Enumerated(EnumType.STRING)
  @Column(name = "day_of_week", nullable = false)
  private DayOfWeek dayOfWeek;

  @Column(name = "is_day_off", nullable = false)
  @JsonProperty("isDayOff")
  private boolean isDayOff;

  @Column(name = "start_time")
  private String startTime;

  @Column(name = "end_time")
  private String endTime;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "restaurant_id", nullable = false)
  private Restaurant restaurant;

}

