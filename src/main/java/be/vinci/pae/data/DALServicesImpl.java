package be.vinci.pae.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.FatalException;
import jakarta.ws.rs.core.Response.Status;

public class DALServicesImpl implements DALBackendServices, DALServices {

  protected PreparedStatement ps;
  private BasicDataSource basicDS;
  private static ThreadLocal<Connection> TL;

  /**
   * Constructor of class.
   * 
   */
  public DALServicesImpl() {

    basicDS = new BasicDataSource();

    // DB connection
    basicDS.setUrl(Config.getProperty("HostSQL"));
    basicDS.setUsername(Config.getProperty("UserSQL"));
    basicDS.setPassword(Config.getProperty("PwdSQL"));
    basicDS.setInitialSize(Config.getIntProperty("InitialPoolSize"));
    basicDS.setMaxTotal(Config.getIntProperty("MaxPoolSize"));

    TL = new ThreadLocal<Connection>();
  }

  @Override
  public void startTransaction() {
    try {
      if (TL.get() == null) {
        Connection conn = basicDS.getConnection();
        conn.setAutoCommit(false);
        TL.set(conn);
      }
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void commitTransaction() {
    try {
      if (TL.get() != null) {
        Connection conn = TL.get();
        conn.commit();
        TL.remove();
        conn.close();
      }
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void rollbackTransaction() {
    try {
      if (TL.get() != null) {
        Connection conn = TL.get();
        conn.rollback();
        TL.remove();
        conn.close();
      }
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public PreparedStatement getPreparedStatement(String sql) {

    try {
      Connection conn = TL.get();
      this.ps = conn.prepareStatement(sql);

    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return ps;
  }

}
