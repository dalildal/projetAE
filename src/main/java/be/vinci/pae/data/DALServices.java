package be.vinci.pae.data;

public interface DALServices {

  /**
   * This method set connection data.
   * 
   */
  void startTransaction();

  /**
   * This method execute all new changes.
   * 
   */
  void commitTransaction();

  /**
   * This method callback last commit.
   * 
   */
  void rollbackTransaction();

}
