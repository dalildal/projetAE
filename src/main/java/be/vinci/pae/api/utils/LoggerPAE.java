package be.vinci.pae.api.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

public class LoggerPAE {

  protected static Logger logger = Logger.getLogger("be.vinci.pae");

  /**
   * Constructor of the logger.
   */
  public LoggerPAE() {
    Handler fh;
    try {
      fh = new FileHandler("myLog.log", 1000000, 5);
      logger.addHandler(fh);
      fh.setFormatter(new MyFormatter());
    } catch (SecurityException | IOException e) {
      throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * This methode add each log into myLog.
   * 
   * @param level of the log
   * @param msg of the log
   */
  public void addLog(Level level, String msg) {
    logger.log(level, msg);
  }

}


