package entity;

public class PetService {
    IPetDao iPetDao;

    public IPetDao getiPetDao() {
        return iPetDao;
    }

    public void setiPetDao(IPetDao iPetDao) {
        this.iPetDao = iPetDao;
    }
}
