package be.vinci.pae.business.users;

public class UserFactoryImpl implements UserFactory {

  @Override
  public UserDTO getUser() {
    return (UserDTO) new UserImpl();
  }

}
