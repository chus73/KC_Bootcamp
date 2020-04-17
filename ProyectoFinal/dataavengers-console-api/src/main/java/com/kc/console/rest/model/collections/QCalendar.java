package com.kc.console.rest.model.collections;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.StringPath;


/**
 * QCalendar is a Querydsl query type for Calendar
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCalendar extends EntityPathBase<Calendar> {

    private static final long serialVersionUID = -596006154L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCalendar calendar = new QCalendar("calendar");

    public final StringPath actual = createString("actual");

    public final QObjectId collectionId;

    public final StringPath currency = createString("currency");

    public final StringPath endDateFormatted = createString("endDateFormatted");

    public final StringPath event = createString("event");

    public final StringPath eventDateFormatted = createString("eventDateFormatted");

    public final StringPath forecast = createString("forecast");

    public final StringPath hour = createString("hour");

    public final StringPath id = createString("id");

    public final StringPath importance = createString("importance");

    public final StringPath previous = createString("previous");

    public final StringPath rawEventDate = createString("rawEventDate");

    public final StringPath startDateFormatted = createString("startDateFormatted");

    public QCalendar(String variable) {
        this(Calendar.class, forVariable(variable), INITS);
    }

    public QCalendar(Path<? extends Calendar> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCalendar(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCalendar(PathMetadata metadata, PathInits inits) {
        this(Calendar.class, metadata, inits);
    }

    public QCalendar(Class<? extends Calendar> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.collectionId = inits.isInitialized("collectionId") ? new QObjectId(forProperty("collectionId")) : null;
    }

}

