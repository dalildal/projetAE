package be.vinci.pae.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import be.vinci.pae.api.filters.AdminOnly;
import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.api.utils.Json;
import be.vinci.pae.business.furniture.Furniture;
import be.vinci.pae.business.furniture.FurnitureDTO;
import be.vinci.pae.business.furniture.FurnitureUCC;
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
@Path("/furniture")
public class FurnitureResource {
  // private final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private FurnitureUCC furnitureUCC;

  // @Inject
  // private FurnitureFactory furnitureFactory;

  /**
   * This method return the list of all furniture.
   * 
   * @param type of furniture
   * @return listFurniture
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<FurnitureDTO> getAllFurniture(@DefaultValue("") @QueryParam("type") String type,
      @QueryParam("etat") List<String> etat) {
    List<FurnitureDTO> listeFurniture = furnitureUCC.getAllFurniture(type, etat);
    return listeFurniture;
  }

  /**
   * This method update state of the furniture.
   * 
   * @param id of the furniture
   * @param json contains data for furniture
   * @return FurnitureDTO of furniture in id Path
   */
  @PUT
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public FurnitureDTO updateFurnitureState(@PathParam("id") int id, JsonNode json) {
    String state = json.get("etat").asText();
    Double price = json.get("prix").asDouble();
    Double dealerPrice = json.get("prixAntiquaire").asDouble();
    String date = json.get("date").asText();
    Double purchasePrice = json.get("prixAchat").asDouble();
    String recoveryDate = json.get("dateRecuperation").asText();
    String description = json.get("description").asText();
    String type = json.get("type").asText();
    int idFavoritePicture = json.get("idPicture").asInt();

    if (state.equals("null")) {
      furnitureUCC.putFurniture(id, description, type, price, idFavoritePicture);
    } else {
      furnitureUCC.putFurnitureState(id, state, date, price, dealerPrice, purchasePrice,
          recoveryDate, description);
    }

    Furniture furniture = (Furniture) furnitureUCC.getFurniture(id);

    return Json.filterPublicJsonView(furniture, Furniture.class);

  }

  /**
   * This method update state of the all furniture from visit.
   * 
   * @param idVisit of the furniture
   * @return Response
   */
  @PUT
  @Path("/visit/{idVisit}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public Response updateFurnitureState(@DefaultValue("-1") @PathParam("idVisit") int idVisit) {

    if (idVisit == -1) {
      throw new WebApplicationException("Veuillez fournir l'id de la visite",
          Status.PRECONDITION_FAILED);
    }

    furnitureUCC.putAllFurnitureFromVisit(idVisit);

    return Response.ok(true).build();

  }

  /**
   * This method return the furniture.
   * 
   * @param id of the furniture
   * @return FurnitureDTO of furniture in id Path
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public FurnitureDTO getFurniture(@PathParam("id") int id) {
    return furnitureUCC.getFurniture(id);
  }

  /**
   * This method get all furniture's type.
   * 
   * @return String list of all furniture's type
   */
  @GET
  @Path("/type")
  @Produces(MediaType.APPLICATION_JSON)
  public List<String> getAllFurnitureType() {
    return furnitureUCC.getAllFurnitureType();
  }

  /**
   * This method get furniture's type.
   * 
   * @param id of type
   * @return libelle of type
   */
  @GET
  @Path("/type/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public String[] getFurnitureType(@PathParam("id") int id) {
    String[] type = new String[1];

    type[0] = furnitureUCC.getFurnitureType(id);

    return type;
  }

  /**
   * This method is call by frontend when user want add a furniture via a visit request.
   * 
   * @param furnitureDTO parameters
   * @return Response status
   */
  @POST
  @Authorize
  @Path("add")
  public Response add(FurnitureDTO furnitureDTO) {

    // Get and check credentials
    if (furnitureDTO.getIdType() == 0) {
      throw new WebApplicationException("Veuillez remplire tous les champs",
          Status.PRECONDITION_FAILED);
    }

    int id = furnitureUCC.add(furnitureDTO);

    return Response.ok(id).build();
  }

  /**
   * This method return the list of all furniture.
   * 
   * @param idList list furniture's id
   * @return listFurniture
   */
  @GET
  @Path("/allFurnitureById")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public List<FurnitureDTO> getAllFurnitureById(@QueryParam("idList") List<Integer> idList,
      @DefaultValue("") @QueryParam("etat") String etat) {
    if (idList == null || etat.equals("")) {
      throw new WebApplicationException("Veuillez indiquer les id et l'etat des meubles",
          Status.PRECONDITION_FAILED);
    }
    List<FurnitureDTO> listeFurniture = furnitureUCC.getAllFurnitureById(idList, etat);
    return listeFurniture;
  }

  /**
   * This method add an sales in table.
   * 
   * @param json with id furniture and id user
   * @return Response
   */
  @POST
  @Path("/addSales")
  @Consumes(MediaType.APPLICATION_JSON)
  @AdminOnly
  public Response addSales(JsonNode json) {
    if (!json.hasNonNull("idFurniture") || !json.hasNonNull("idUser")) {
      throw new WebApplicationException("Veuillez indiquer l'id du meuble et/ou l'id de l'user",
          Status.PRECONDITION_FAILED);
    }

    int idFurniture = json.get("idFurniture").asInt();
    int idUser = json.get("idUser").asInt();

    furnitureUCC.addSales(idFurniture, idUser);

    return Response.ok(true).build();
  }

  /**
   * This method is call by frontend to each refresh or state change.
   * 
   * @param filter needed to search furniture
   * @return furniture by filter
   */
  @GET
  @Path("search/{filter}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public HashMap<Integer, List<String>> getFurnitures(
      @DefaultValue("") @PathParam("filter") String filter) {

    HashMap<Integer, List<String>> mapFurniture = new HashMap<Integer, List<String>>();

    if (!filter.equals("") && filter != null) {
      mapFurniture = furnitureUCC.getFurnitureByFilters(filter);
    } else {
      throw new WebApplicationException("Veuillez remplire la barre de recherche",
          Status.PRECONDITION_FAILED);
    }

    return mapFurniture;
  }

  /**
   * This method return the list of all furniture purchase by Satcho at a user.
   * 
   * @param idUser of the user
   * @return listFurniture
   */
  @GET
  @Path("allFurniturePurchase/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public List<FurnitureDTO> getAllFurnituresPurchase(@PathParam("id") int idUser) {
    List<FurnitureDTO> listFurniture = new ArrayList<FurnitureDTO>();

    if (idUser > 0) {
      listFurniture = furnitureUCC.getAllPurchaseFurnitureOfUser(idUser);
    } else {
      throw new WebApplicationException("Veuillez indiquer l'id du user",
          Status.PRECONDITION_FAILED);
    }

    return listFurniture;
  }

  /**
   * This method return the list of all furniture purchase by Satcho at a user.
   * 
   * @param idUser of the user
   * @return listFurniture
   */
  @GET
  @Path("searchedFurniturePurchase/{id}/{search}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<FurnitureDTO> getAllFurnituresPurchase(@PathParam("id") int idUser,
      @PathParam("search") String search) {
    List<FurnitureDTO> listFurniture = new ArrayList<FurnitureDTO>();

    if (idUser > 0 && !search.equals("")) {
      listFurniture = furnitureUCC.selectSearchSellFurnitureOfUser(idUser, search);
    } else {
      throw new WebApplicationException("Veuillez indiquer l'id du user",
          Status.PRECONDITION_FAILED);
    }

    return listFurniture;
  }

  /**
   * This method return the list of all furniture sell by Satcho at a user.
   * 
   * @param idUser of the user
   * @return listFurniture
   */
  @GET
  @Path("allFurnitureSell/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public List<FurnitureDTO> getAllFurnituresSell(@PathParam("id") int idUser) {
    List<FurnitureDTO> listFurniture = new ArrayList<FurnitureDTO>();

    if (idUser > 0) {
      listFurniture = furnitureUCC.getAllSellFurnitureOfUser(idUser);
    } else {
      throw new WebApplicationException("Veuillez indiquer l'id du user",
          Status.PRECONDITION_FAILED);
    }

    return listFurniture;
  }

}
