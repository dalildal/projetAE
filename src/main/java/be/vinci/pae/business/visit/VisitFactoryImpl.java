package be.vinci.pae.business.visit;

public class VisitFactoryImpl implements VisitFactory {

  @Override
  public VisitDTO getVisit() {
    return (VisitDTO) new VisitImpl();
  }

}
