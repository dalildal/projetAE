package be.vinci.pae.business.options;

public class OptionFactoryImpl implements OptionFactory {

  @Override
  public OptionDTO getOption() {
    return (OptionDTO) new OptionImpl();
  }

}
