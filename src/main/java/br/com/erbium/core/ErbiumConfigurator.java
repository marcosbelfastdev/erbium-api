package br.com.erbium.core;

import lombok.Getter;
import lombok.experimental.Accessors;

public class ErbiumConfigurator {

//    @Getter
//    @Accessors(fluent = true)
//    OutputConfig outputConfiguration = new OutputConfig();

    @Getter @Accessors(fluent = true)
    Routers routers;

    public ErbiumConfigurator() {
        OutputConfig outputConfiguration = new OutputConfig();
        Routers routers = new Routers(outputConfiguration);
        routers.add(new DefaultConsoleRouter());
    }

    public ErbiumConfigurator setRouters(Routers router) {
        routers = router;
        return this;
    }

    public Routers getRouters() {
        return routers;
    }

    public Routers out() {
        return routers;
    }


}
