package be.vinci.pae.data;

import java.sql.PreparedStatement;

public interface DALBackendServices {

  /**
   * This method prepare one PreparedStatement to use.
   * 
   * @param sql of query to execute
   * @return preparedStatement
   */
  PreparedStatement getPreparedStatement(String sql);

}
