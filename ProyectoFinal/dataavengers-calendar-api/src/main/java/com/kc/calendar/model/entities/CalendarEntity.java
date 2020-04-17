package com.kc.calendar.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEntity {

    @Id
    private String id;
 
    @Column(name="json")
    private String json;   

}