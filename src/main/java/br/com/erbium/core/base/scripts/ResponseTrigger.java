package br.com.erbium.core.base.scripts;

import br.com.erbium.core.ResponseManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public abstract class ResponseTrigger extends Script implements Runnable {

    @Getter @Setter @Accessors(fluent = true)
    protected ResponseManager responseManager;

    public void attach(ResponseManager responseManager) {
        responseManager(responseManager);
    }


}
