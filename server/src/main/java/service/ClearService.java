package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import request.ClearRequest;
import result.ClearResult;

public class ClearService extends Service{

    public ClearService (UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
        super(userDAO, gameDAO, authDAO);
    }

    public ClearResult clearAll(ClearRequest clearRequest){
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();

        System.out.println("You made it to the clearAll function in ClearService");

        ClearResult clearResult = new ClearResult();
        return clearResult;
    }
}
