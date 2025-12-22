package kr.hhplus.be.server.common.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEntity extends BaseTimeEntity {

    @Column(name = "removed")
    protected Boolean removed = false;

    public void remove() {
        this.removed = true;
    }
}
