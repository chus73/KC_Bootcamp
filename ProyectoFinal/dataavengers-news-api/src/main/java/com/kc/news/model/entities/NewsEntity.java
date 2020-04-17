package com.kc.news.model.entities;

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
public class NewsEntity {

    @Id
    private String id;
    
    @Column(name="title")
    private String title;

    @Column(name="json")
    private String json;   

}