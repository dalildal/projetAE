package be.vinci.pae.business.picture;

public class PictureFactoryImpl implements PictureFactory {

  @Override
  public PictureDTO getPicture() {
    return (PictureDTO) new PictureImpl();
  }

}
