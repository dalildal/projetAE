package be.vinci.pae.utils;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import be.vinci.pae.business.furniture.FurnitureFactory;
import be.vinci.pae.business.furniture.FurnitureFactoryImpl;
import be.vinci.pae.business.furniture.FurnitureUCC;
import be.vinci.pae.business.furniture.FurnitureUCCImpl;
import be.vinci.pae.business.options.OptionFactory;
import be.vinci.pae.business.options.OptionFactoryImpl;
import be.vinci.pae.business.options.OptionUCC;
import be.vinci.pae.business.options.OptionUCCImpl;
import be.vinci.pae.business.picture.PictureFactory;
import be.vinci.pae.business.picture.PictureFactoryImpl;
import be.vinci.pae.business.picture.PictureUCC;
import be.vinci.pae.business.picture.PictureUCCImpl;
import be.vinci.pae.business.users.UserFactory;
import be.vinci.pae.business.users.UserFactoryImpl;
import be.vinci.pae.business.users.UserUCC;
import be.vinci.pae.business.users.UserUCCImpl;
import be.vinci.pae.business.visit.VisitFactory;
import be.vinci.pae.business.visit.VisitFactoryImpl;
import be.vinci.pae.business.visit.VisitUCC;
import be.vinci.pae.business.visit.VisitUCCImpl;
import be.vinci.pae.data.DALBackendServices;
import be.vinci.pae.data.DALServices;
import be.vinci.pae.data.DALServicesImpl;
import be.vinci.pae.data.furniture.FurnitureDAO;
import be.vinci.pae.data.furniture.FurnitureDAOImpl;
import be.vinci.pae.data.option.OptionDAO;
import be.vinci.pae.data.option.OptionDAOImpl;
import be.vinci.pae.data.picture.PictureDAO;
import be.vinci.pae.data.picture.PictureDAOImpl;
import be.vinci.pae.data.users.UserDAO;
import be.vinci.pae.data.users.UserDAOImpl;
import be.vinci.pae.data.visit.VisitDAO;
import be.vinci.pae.data.visit.VisitDAOImpl;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationBinder extends AbstractBinder {

  @Override
  protected void configure() {
    bind(UserFactoryImpl.class).to(UserFactory.class).in(Singleton.class);
    bind(UserDAOImpl.class).to(UserDAO.class).in(Singleton.class);
    bind(UserUCCImpl.class).to(UserUCC.class).in(Singleton.class);
    bind(DALServicesImpl.class).to(DALBackendServices.class).in(Singleton.class);
    bind(DALServicesImpl.class).to(DALServices.class).in(Singleton.class);
    bind(FurnitureFactoryImpl.class).to(FurnitureFactory.class).in(Singleton.class);
    bind(FurnitureDAOImpl.class).to(FurnitureDAO.class).in(Singleton.class);
    bind(FurnitureUCCImpl.class).to(FurnitureUCC.class).in(Singleton.class);
    bind(OptionFactoryImpl.class).to(OptionFactory.class).in(Singleton.class);
    bind(OptionDAOImpl.class).to(OptionDAO.class).in(Singleton.class);
    bind(OptionUCCImpl.class).to(OptionUCC.class).in(Singleton.class);
    bind(VisitFactoryImpl.class).to(VisitFactory.class).in(Singleton.class);
    bind(VisitDAOImpl.class).to(VisitDAO.class).in(Singleton.class);
    bind(VisitUCCImpl.class).to(VisitUCC.class).in(Singleton.class);
    bind(PictureFactoryImpl.class).to(PictureFactory.class).in(Singleton.class);
    bind(PictureDAOImpl.class).to(PictureDAO.class).in(Singleton.class);
    bind(PictureUCCImpl.class).to(PictureUCC.class).in(Singleton.class);
  }
}
