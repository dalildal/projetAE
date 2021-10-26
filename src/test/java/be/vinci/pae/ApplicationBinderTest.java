package be.vinci.pae;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import be.vinci.pae.business.furniture.FurnitureFactory;
import be.vinci.pae.business.furniture.FurnitureFactoryImpl;
import be.vinci.pae.business.furniture.FurnitureUCC;
import be.vinci.pae.business.furniture.FurnitureUCCImpl;
import be.vinci.pae.business.options.OptionFactory;
import be.vinci.pae.business.options.OptionFactoryImpl;
import be.vinci.pae.business.options.OptionUCC;
import be.vinci.pae.business.options.OptionUCCImpl;
import be.vinci.pae.business.users.UserFactory;
import be.vinci.pae.business.users.UserFactoryImpl;
import be.vinci.pae.business.users.UserUCC;
import be.vinci.pae.business.users.UserUCCImpl;
import be.vinci.pae.data.DALBackendServices;
import be.vinci.pae.data.DALServices;
import be.vinci.pae.data.DALServicesImpl;
import be.vinci.pae.data.furniture.FurnitureDAO;
import be.vinci.pae.data.option.OptionDAO;
import be.vinci.pae.data.users.UserDAO;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationBinderTest extends AbstractBinder {

  @Override
  protected void configure() {
    bind(UserFactoryImpl.class).to(UserFactory.class).in(Singleton.class);
    bind(MockUserDAO.class).to(UserDAO.class).in(Singleton.class);
    bind(UserUCCImpl.class).to(UserUCC.class).in(Singleton.class);
    bind(DALServicesImpl.class).to(DALBackendServices.class).in(Singleton.class);
    bind(DALServicesImpl.class).to(DALServices.class).in(Singleton.class);
    bind(FurnitureFactoryImpl.class).to(FurnitureFactory.class).in(Singleton.class);
    bind(MockFurnitureDAO.class).to(FurnitureDAO.class).in(Singleton.class);
    bind(FurnitureUCCImpl.class).to(FurnitureUCC.class).in(Singleton.class);
    bind(OptionFactoryImpl.class).to(OptionFactory.class).in(Singleton.class);
    bind(MockOptionDAO.class).to(OptionDAO.class).in(Singleton.class);
    bind(OptionUCCImpl.class).to(OptionUCC.class).in(Singleton.class);
  }
}
