package be.vinci.pae.utils;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class FatalException extends WebApplicationException {

  private static final long serialVersionUID = -5926196101906096391L;

  public FatalException(Throwable cause) {
    super(cause, Response.status(Status.INTERNAL_SERVER_ERROR).build());
  }

  public FatalException(String message, Throwable cause) {
    super(cause,
        Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type("text/plain").build());
  }

  public FatalException(String message) {
    super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type("text/plain").build());
  }

  public FatalException(String message, Response.Status preconditionFailed) {

    super(message, preconditionFailed);
  }

  public FatalException() {
    super(Response.status(Status.INTERNAL_SERVER_ERROR).build());
  }

}
