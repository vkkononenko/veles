package vkkononenko;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by v.kononenko on 20.03.2019.
 */
public class SecurityUtils implements Serializable {

    @Inject
    protected UserSession userSession;

    @PostConstruct
    private void init() {
        if(userSession.getSystemUser().getId() == null) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
}
