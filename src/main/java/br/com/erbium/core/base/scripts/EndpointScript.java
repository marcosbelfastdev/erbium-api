package br.com.erbium.core.base.scripts;

import br.com.erbium.core.Endpoint;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public abstract class EndpointScript extends Script implements Runnable {

    @Getter @Setter @Accessors(fluent = true)
    protected Endpoint endpoint;


    public void attach(Endpoint endpoint) {
        endpoint(endpoint);
    }

    @Override
    public void run() {

    }

}
