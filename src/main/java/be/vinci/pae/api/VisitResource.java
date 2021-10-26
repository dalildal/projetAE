package be.vinci.pae.api;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import be.vinci.pae.api.filters.AdminOnly;
import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.api.utils.Json;
import be.vinci.pae.business.visit.VisitDTO;
import be.vinci.pae.business.visit.VisitUCC;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Singleton
@Path("/visits")
public class VisitResource {

  @Inject
  private VisitUCC visitUCC;

  /**
   * This method is call by frontend when user want request a visit.
   * 
   * @param visitDTO parameters
   * @return Response status
   */
  @POST
  @Authorize
  @Path("add")
  public Response add(VisitDTO visitDTO) {

    // Get and check credentials
    if (visitDTO.getTimePeriod().isEmpty() || visitDTO.getStreetVisit().isEmpty()
        || visitDTO.getNumVisit().isEmpty() || visitDTO.getPostalCodeVisit() == 0
        || visitDTO.getMunicipalityVisit().isEmpty() || visitDTO.getCountryVisit().isEmpty()) {
      throw new WebApplicationException("Veuillez remplire tous les champs",
          Status.PRECONDITION_FAILED);
    }

    int id = visitUCC.add(visitDTO);

    return Response.ok(id).build();
  }

  /**
   * This method return the list of all visit.
   * 
   * @param etat of the visit who want
   * @return listeVisit
   */

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public List<VisitDTO> getAllVisit(@DefaultValue("") @QueryParam("etat") String etat) {

    List<VisitDTO> listeVisit;

    if (!etat.equals("confirmee") || !etat.equals("annulee") || !etat.equals("attente")
        || !etat.equals("")) {
      listeVisit = visitUCC.getAllVisit(etat);
    } else {
      throw new WebApplicationException("Cet etat n'est pas valide", Status.PRECONDITION_FAILED);
    }
    return listeVisit;
  }

  /**
   * This method update the visit.
   * 
   * @param id of the user
   * @return UserDTO of user in id Path
   */
  @PUT
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public VisitDTO updateVisit(@PathParam("id") int id, JsonNode json) {

    String date = json.get("date").asText();
    String raison = json.get("raison").asText();

    VisitDTO visit = visitUCC.getVisit(id);

    visitUCC.putVisit(id, date, raison);

    return Json.filterPublicJsonView(visit, VisitDTO.class);
  }

  /**
   * This method get a list the furniture id.
   * 
   * @param id of the visit
   * @return list the furniture id
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public List<Integer> getListIdFurniture(@PathParam("id") int id) {

    List<Integer> list = visitUCC.getListIdFurniture(id);

    return list;
  }


  /**
   * This method is call by frontend when user want request a visit.
   * 
   * @param json visit and furniture id
   * @return Response status
   */
  @POST
  @Authorize
  @Path("addFurnitureVisit")
  public Response addFurnitureVisit(JsonNode json) {

    // Get and check credentials
    if (!json.hasNonNull("idVisit") || !json.hasNonNull("idFurniture")) {
      throw new WebApplicationException("Veuillez remplire tous les champs",
          Status.PRECONDITION_FAILED);
    }
    int idVisit = json.get("idVisit").asInt();
    int idFurniture = json.get("idFurniture").asInt();

    visitUCC.addFurnitureVisit(idVisit, idFurniture);
    return Response.ok("true").build();
  }
}
