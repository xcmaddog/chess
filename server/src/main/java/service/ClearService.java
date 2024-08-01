package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import request.ClearRequest;
import result.ClearResult;

public class ClearService extends Service{

    public ClearService (UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
        super(userDAO, gameDAO, authDAO);
    }

    public ClearResult clearAll(ClearRequest clearRequest) throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
        ClearResult clearResult = new ClearResult();
        return clearResult;
    }
}
