package com.kc.console.rest.model.collections;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.StringPath;


/**
 * QTwitter is a Querydsl query type for Twitter
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTwitter extends EntityPathBase<Twitter> {

    private static final long serialVersionUID = -791272517L;

    public static final QTwitter twitter = new QTwitter("twitter");

    public final StringPath createdAt = createString("createdAt");

    public final StringPath dateFormatted = createString("dateFormatted");

    public final NumberPath<Integer> favoriteCount = createNumber("favoriteCount", Integer.class);

    public final BooleanPath favorited = createBoolean("favorited");

    public final ListPath<String, StringPath> hashtags = this.<String, StringPath>createList("hashtags", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath idStr = createString("idStr");

    public final StringPath lang = createString("lang");

    public final NumberPath<Integer> retweetCount = createNumber("retweetCount", Integer.class);

    public final BooleanPath retweeted = createBoolean("retweeted");

    public final StringPath sourceQuery = createString("sourceQuery");

    public final StringPath text = createString("text");

    public QTwitter(String variable) {
        super(Twitter.class, forVariable(variable));
    }

    public QTwitter(Path<? extends Twitter> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTwitter(PathMetadata metadata) {
        super(Twitter.class, metadata);
    }

}

