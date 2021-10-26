package be.vinci.pae.business.options;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = OptionImpl.class)
public interface Option extends OptionDTO {

}
