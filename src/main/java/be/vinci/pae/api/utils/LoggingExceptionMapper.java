package be.vinci.pae.api.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.Config;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LoggingExceptionMapper implements ExceptionMapper<Throwable> {

  LoggerPAE logger = new LoggerPAE();

  @Override
  public Response toResponse(Throwable exception) {
    exception.printStackTrace();
    if (getStatusCode(exception) == 500) {
      logger.addLog(Level.SEVERE, exception.getMessage());
    } else {
      logger.addLog(Level.WARNING, exception.getMessage());
    }
    if (Config.getBoolProperty("SendStackTraceToClient")) {
      return Response.status(getStatusCode(exception)).entity(getEntity(exception)).build();
    }
    return Response.status(getStatusCode(exception)).entity(exception.getMessage()).build();
  }

  /**
   * Get appropriate HTTP status code for an exception.
   * 
   * @param exception throwable
   * @return int of status
   */
  private int getStatusCode(Throwable exception) {
    if (exception instanceof WebApplicationException) {
      return ((WebApplicationException) exception).getResponse().getStatus();
    }
    if (exception instanceof BusinessException) {
      return ((BusinessException) exception).getResponse().getStatus();
    }
    return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
  }

  /**
   * Get response body for an exception.
   * 
   * @param exception throwable
   * @return
   */
  private Object getEntity(Throwable exception) {
    StringWriter errorMsg = new StringWriter();
    exception.printStackTrace(new PrintWriter(errorMsg));
    return errorMsg.toString();
  }

}
