package be.vinci.pae.business.furniture;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = FurnitureImpl.class)
public interface Furniture extends FurnitureDTO {


}
