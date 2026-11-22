package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class TreatmentActionServlet extends HttpServlet {
    private final TreatmentActionService service = new TreatmentActionService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, HttpServletResponse.SC_OK, service.getAll());
        } catch (IllegalArgumentException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Внутрішня помилка сервера");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                TreatmentAction actionItem = new TreatmentAction(
                        null,
                        Long.parseLong(req.getParameter("problemId")),
                        req.getParameter("description"),
                        req.getParameter("plannedDate")
                );
                service.add(actionItem);
                writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
            } else if ("update".equals(action)) {
                long id = Long.parseLong(req.getParameter("id"));
                TreatmentAction actionItem = new TreatmentAction(
                        id,
                        Long.parseLong(req.getParameter("problemId")),
                        req.getParameter("description"),
                        req.getParameter("plannedDate")
                );
                service.update(actionItem);
                writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
            } else if ("delete".equals(action)) {
                long id = Long.parseLong(req.getParameter("id"));
                service.delete(id);
                writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
            } else {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Невідома дія");
            }
        } catch (IllegalArgumentException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Внутрішня помилка сервера");
        }
    }

    private void writeJson(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(mapper.writeValueAsString(data));
    }

    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        String safeMessage = (message == null || message.isBlank()) ? "Помилка запиту" : message;
        writeJson(resp, status, Map.of("error", safeMessage));
    }
}