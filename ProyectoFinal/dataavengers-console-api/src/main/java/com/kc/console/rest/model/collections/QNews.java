package com.kc.console.rest.model.collections;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.StringPath;


/**
 * QNews is a Querydsl query type for News
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QNews extends EntityPathBase<News> {

    private static final long serialVersionUID = 1607128715L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNews news = new QNews("news");

    public final StringPath article = createString("article");

    public final StringPath authorName = createString("authorName");

    public final QObjectId collectionId;

    public final StringPath dateFormatted = createString("dateFormatted");

    public final StringPath fullUrl = createString("fullUrl");

    public final StringPath objectID = createString("objectID");

    public final StringPath publicationTime = createString("publicationTime");

    public final StringPath summary = createString("summary");

    public final ListPath<String, StringPath> tags = this.<String, StringPath>createList("tags", String.class, StringPath.class, PathInits.DIRECT2);

    public QNews(String variable) {
        this(News.class, forVariable(variable), INITS);
    }

    public QNews(Path<? extends News> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNews(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNews(PathMetadata metadata, PathInits inits) {
        this(News.class, metadata, inits);
    }

    public QNews(Class<? extends News> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.collectionId = inits.isInitialized("collectionId") ? new QObjectId(forProperty("collectionId")) : null;
    }

}

