package com.example.keycloak.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class PageResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private long totalRecordCount;
    boolean hasNext;
    boolean hasPrevious;
    private transient T data;

}
