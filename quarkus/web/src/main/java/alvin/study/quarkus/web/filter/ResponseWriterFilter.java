package alvin.study.quarkus.web.filter;

import java.io.IOException;

import alvin.study.quarkus.web.endpoint.model.Response;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

@Provider
public class ResponseWriterFilter implements WriterInterceptor {

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        context.setEntity(
            Response.builder()
                    .payload(context.getEntity())
                    .build());
        context.proceed();
    }
}
