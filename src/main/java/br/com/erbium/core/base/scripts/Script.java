package br.com.erbium.core.base.scripts;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public abstract class Script implements Runnable {
    @Getter
    protected Object result;

//    @Getter @Setter
//    @Accessors(fluent = true)
//    protected Throwable throwable;

    public <T> T getResultAs(Class<T> clazz) {
        if (result == null)
            return null;
        return clazz.cast(result);
    }
}
