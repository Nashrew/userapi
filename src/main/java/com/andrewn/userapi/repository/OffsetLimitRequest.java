package com.andrewn.userapi.repository;

import com.andrewn.userapi.model.exceptions.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

public class OffsetLimitRequest implements Pageable, Serializable {

    private static final long serialVersionUID = 42L;

    private int limit;
    private int offset;
    private Sort sort;

    public OffsetLimitRequest(int offset, int limit, Sort sort){
        if (offset < 0) {
            throw new BadRequestException("Offset index must not be less than zero");
        }

        if (limit < 1) {
            throw new BadRequestException("Limit must not be less than one");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetLimitRequest((int) (getOffset() + getPageSize()), getPageSize(), getSort());
    }

    public OffsetLimitRequest previous() {
        return hasPrevious() ? new OffsetLimitRequest((int) (getOffset() - getPageSize()), getPageSize(), getSort()) : this;
    }

    @Override
    public boolean hasPrevious() {
        return offset >= limit;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetLimitRequest(0, getPageSize(), getSort());
    }
}
