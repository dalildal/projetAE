package be.vinci.pae.business.visit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = VisitImpl.class)
public interface Visit extends VisitDTO {

}
