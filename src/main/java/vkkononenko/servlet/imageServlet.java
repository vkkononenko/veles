package vkkononenko.servlet;

import org.primefaces.model.DefaultStreamedContent;
import vkkononenko.models.SystemUser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by v.kononenko on 21.03.2019.
 */
@WebServlet("/images/*")
public class imageServlet extends HttpServlet {

    @PersistenceContext(name = "veles")
    private EntityManager em;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = String.valueOf(request.getPathInfo().substring(1)); // Gets string that goes after "/images/".
        SystemUser systemUser = em.find(SystemUser.class, Long.parseLong(id));
        response.setHeader("Content-Type", getServletContext().getMimeType(systemUser.getAvatarType()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + systemUser.getAvatarName() + "\"");
        DefaultStreamedContent input = new DefaultStreamedContent(new ByteArrayInputStream(systemUser.getAvatar()),
                systemUser.getAvatarType());
        BufferedOutputStream output = null;

        try {

            output = new BufferedOutputStream(response.getOutputStream());
            byte[] buffer = new byte[8192];
            for (int length = 0; (length = input.getStream().read(buffer)) > 0;) {
                output.write(buffer, 0, length);
            }
        } finally {
            if (output != null) try { output.close(); } catch (IOException logOrIgnore) {}
            input.getStream().close();
        }
    }
}
