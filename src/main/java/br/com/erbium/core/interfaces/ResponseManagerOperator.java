package br.com.erbium.core.interfaces;

import br.com.erbium.core.ErbiumResponse;
import br.com.erbium.core.Routers;
import lombok.NonNull;

/**
 * Fluent interface for managing HTTP responses and associated response scripts/triggers.
 */
public interface ResponseManagerOperator {


    public ErbiumResponse getLastResponse();

    public ErbiumResponse getResponse();

    public ErbiumResponse getPenultimateResponse();


    public ErbiumResponse getResponse(@NonNull Integer index);

    public ResponseManagerOperator qrset(@NonNull String varName, @NonNull String jsonPath);
    public ResponseManagerOperator rset(@NonNull String varName, @NonNull String jsonPath);
    public Object get(@NonNull String varName);
    Routers out();
    
}
