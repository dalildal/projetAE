package be.vinci.pae.business.visit;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import be.vinci.pae.data.DALServices;
import be.vinci.pae.data.visit.VisitDAO;
import be.vinci.pae.utils.BusinessException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

public class VisitUCCImpl implements VisitUCC {
  @Inject
  private VisitDAO visitDAO;
  @Inject
  private DALServices dalServices;

  @Override
  public int add(VisitDTO visit) {
    dalServices.startTransaction();
    int id;
    try {
      id = visitDAO.insert(visit);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.UNAUTHORIZED);
    }
    return id;
  }

  @Override
  public List<VisitDTO> getAllVisit(String etat) {
    dalServices.startTransaction();
    List<VisitDTO> list = null;
    try {
      list = visitDAO.selectAllVisit(etat);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.UNAUTHORIZED);
    }

    return list;
  }

  @Override
  public VisitDTO getVisit(int id) {
    dalServices.startTransaction();
    VisitDTO visit = null;
    try {
      visit = visitDAO.findById(id);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.UNAUTHORIZED);
    }
    return visit;
  }

  @Override
  public void putVisit(int id, String date, String raison) {

    dalServices.startTransaction();

    Timestamp timestamp = null;
    if (!date.equals("null")) {
      try {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d'T'HH:mm");
        Date parsedDate = dateFormat.parse(date);
        timestamp = new Timestamp(parsedDate.getTime());
      } catch (Exception e) {
        dalServices.rollbackTransaction();
        throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
      }
    }
    visitDAO.updateVisit(id, timestamp, raison);
    dalServices.commitTransaction();
  }

  @Override
  public List<Integer> getListIdFurniture(int id) {
    dalServices.startTransaction();
    List<Integer> list = visitDAO.selectListIdFurniture(id);
    if (list == null) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Aucun meubles pour cette visite.", Status.NOT_ACCEPTABLE);
    }
    dalServices.commitTransaction();
    return list;
  }

  @Override
  public boolean addFurnitureVisit(int idVisit, int idFurniture) {
    dalServices.startTransaction();
    try {
      visitDAO.insertFurnitureVisit(idVisit, idFurniture);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.UNAUTHORIZED);
    }
    return true;
  }

}
