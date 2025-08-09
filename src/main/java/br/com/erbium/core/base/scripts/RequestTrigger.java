package br.com.erbium.core.base.scripts;

import br.com.erbium.core.Endpoint;
import br.com.erbium.core.RequestManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public abstract class RequestTrigger extends Script {

    @Getter @Setter @Accessors(fluent = true)
    protected RequestManager requestManager;


    public void attach(RequestManager requestManager) {
        requestManager(requestManager);
    }

    public abstract Endpoint exec();

}
