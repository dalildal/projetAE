package be.vinci.pae.business.furniture;

public class FurnitureFactoryImpl implements FurnitureFactory {

  @Override
  public FurnitureDTO getFurniture() {
    return (FurnitureDTO) new FurnitureImpl();
  }


}
