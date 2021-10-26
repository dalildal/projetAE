package be.vinci.pae.business.users;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = UserImpl.class)
public interface User extends UserDTO {

  void encryptPwd();

  boolean checkPwd(String password);

  boolean checkCanBeAdmin();

  boolean changeToAdmin();

}
