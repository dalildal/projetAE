package be.vinci.pae.utils;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class BusinessException extends WebApplicationException {

  private static final long serialVersionUID = -1816049362268244251L;

  public BusinessException() {
    super(Response.status(Status.INTERNAL_SERVER_ERROR).build());
  }

  public BusinessException(Throwable cause) {
    super(cause, Response.status(Status.INTERNAL_SERVER_ERROR).build());
  }

  public BusinessException(String message, Throwable cause) {
    super(cause,
        Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type("text/plain").build());
  }

  public BusinessException(String message) {
    super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type("text/plain").build());
  }

  public BusinessException(String message, Response.Status preconditionFailed) {

    super(message, preconditionFailed);
  }
}
