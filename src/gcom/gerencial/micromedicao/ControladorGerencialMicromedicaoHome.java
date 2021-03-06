package gcom.gerencial.micromedicao;

import java.rmi.RemoteException;

import javax.ejb.CreateException;

/**
 * 
 * 
 * @author Thiago Toscano
 * @created 19/04/2006
 */
public interface ControladorGerencialMicromedicaoHome extends javax.ejb.EJBHome {
    /**
     * Description of the Method
     * 
     * @return Description of the Return Value
     * @exception CreateException
     *                Description of the Exception
     * @exception RemoteException
     *                Description of the Exception
     */
    public ControladorGerencialMicromedicaoRemote create() throws CreateException,RemoteException;
    
}
