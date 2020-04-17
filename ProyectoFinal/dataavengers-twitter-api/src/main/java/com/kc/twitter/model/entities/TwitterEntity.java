package com.kc.twitter.model.entities;

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
public class TwitterEntity {

    @Id
    private long id;

    @Column(name="source")
    private String source;

    @Column(name="json")
    private String json;   

}