package com.kc.console.rest.model.collections;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.StringPath;


/**
 * QFxcm is a Querydsl query type for Fxcm
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFxcm extends EntityPathBase<Fxcm> {

    private static final long serialVersionUID = 1606908020L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFxcm fxcm = new QFxcm("fxcm");

    public final NumberPath<Double> askClose = createNumber("askClose", Double.class);

    public final NumberPath<Double> askHigh = createNumber("askHigh", Double.class);

    public final NumberPath<Double> askLow = createNumber("askLow", Double.class);

    public final NumberPath<Double> askOpen = createNumber("askOpen", Double.class);

    public final NumberPath<Double> bidClose = createNumber("bidClose", Double.class);

    public final NumberPath<Double> bidHigh = createNumber("bidHigh", Double.class);

    public final NumberPath<Double> bidLow = createNumber("bidLow", Double.class);

    public final NumberPath<Double> bidOpen = createNumber("bidOpen", Double.class);

    public final QObjectId collectionId;

    public final StringPath dateFormatted = createString("dateFormatted");

    public final StringPath instrument = createString("instrument");

    public final StringPath periodId = createString("periodId");

    public final NumberPath<Long> tickqty = createNumber("tickqty", Long.class);

    public final NumberPath<Long> timestamp = createNumber("timestamp", Long.class);

    public QFxcm(String variable) {
        this(Fxcm.class, forVariable(variable), INITS);
    }

    public QFxcm(Path<? extends Fxcm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFxcm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFxcm(PathMetadata metadata, PathInits inits) {
        this(Fxcm.class, metadata, inits);
    }

    public QFxcm(Class<? extends Fxcm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.collectionId = inits.isInitialized("collectionId") ? new QObjectId(forProperty("collectionId")) : null;
    }

}

