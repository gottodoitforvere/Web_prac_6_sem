package ru.theater.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.theater.dao.PlayDao;
import ru.theater.dao.SessionDao;
import ru.theater.model.Play;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/sessions")
public class SessionController {

    private final SessionDao sessionDao;
    private final PlayDao playDao;

    @Autowired
    public SessionController(SessionDao sessionDao, PlayDao playDao) {
        this.sessionDao = sessionDao;
        this.playDao = playDao;
    }

    @GetMapping
    public String list(@RequestParam("playId") Long playId,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Play play = playDao.findByIdWithDetails(playId);
        if (play == null) {
            redirectAttributes.addFlashAttribute("error", "Спектакль не найден.");
            return "redirect:/theaters";
        }

        model.addAttribute("play", play);
        model.addAttribute("sessions", sessionDao.findByPlayId(playId));
        return "session/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam("playId") Long playId,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Play play = playDao.findByIdWithDetails(playId);
        if (play == null) {
            redirectAttributes.addFlashAttribute("error", "Спектакль не найден.");
            return "redirect:/theaters";
        }

        model.addAttribute("play", play);
        return "session/form";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam("id") Long id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        ru.theater.model.Session session = sessionDao.findByIdWithPlay(id);
        if (session == null) {
            redirectAttributes.addFlashAttribute("error", "Сеанс не найден.");
            return "redirect:/theaters";
        }

        model.addAttribute("sessionItem", session);
        model.addAttribute("play", session.getPlay());
        return "session/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam("playId") Long playId,
                       @RequestParam("sessionDate") String sessionDateStr,
                       @RequestParam("sessionTime") String sessionTimeStr,
                       RedirectAttributes redirectAttributes) {

        LocalDate sessionDate;
        LocalTime sessionTime;
        try {
            sessionDate = LocalDate.parse(sessionDateStr);
            sessionTime = LocalTime.parse(sessionTimeStr);
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Некорректная дата или время сеанса.");
            if (id == null) {
                return "redirect:/sessions/new?playId=" + playId;
            }
            return "redirect:/sessions/edit?id=" + id;
        }

        if (id == null) {
            Play play = playDao.findByIdWithDetails(playId);
            if (play == null) {
                redirectAttributes.addFlashAttribute("error", "Спектакль не найден.");
                return "redirect:/theaters";
            }

            ru.theater.model.Session session = new ru.theater.model.Session(play, sessionDate, sessionTime);
            sessionDao.save(session);
            redirectAttributes.addFlashAttribute("message", "Сеанс успешно добавлен.");
        } else {
            ru.theater.model.Session session = sessionDao.findByIdWithPlay(id);
            if (session == null) {
                redirectAttributes.addFlashAttribute("error", "Сеанс не найден.");
                return "redirect:/sessions?playId=" + playId;
            }

            session.setSessionDate(sessionDate);
            session.setSessionTime(sessionTime);
            sessionDao.update(session);
            redirectAttributes.addFlashAttribute("message", "Данные сеанса успешно обновлены.");
        }

        return "redirect:/sessions?playId=" + playId;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id,
                         RedirectAttributes redirectAttributes) {
        ru.theater.model.Session session = sessionDao.findByIdWithPlay(id);
        if (session == null) {
            redirectAttributes.addFlashAttribute("error", "Сеанс не найден.");
            return "redirect:/theaters";
        }

        Long playId = session.getPlay().getId();
        sessionDao.delete(session);
        redirectAttributes.addFlashAttribute("message", "Сеанс успешно удалён.");
        return "redirect:/sessions?playId=" + playId;
    }

    @PostMapping("/buy")
    public String buyTickets(@RequestParam("sessionId") Long sessionId,
                             @RequestParam("playId") Long playId,
                             @RequestParam("seatType") String seatType,
                             @RequestParam("count") Integer count,
                             RedirectAttributes redirectAttributes) {

        if (count == null || count <= 0) {
            redirectAttributes.addFlashAttribute("error", "Количество билетов должно быть положительным.");
            return "redirect:/sessions?playId=" + playId;
        }

        if (!isValidSeatType(seatType)) {
            redirectAttributes.addFlashAttribute("error", "Некорректный тип места.");
            return "redirect:/sessions?playId=" + playId;
        }

        ru.theater.model.Session existingSession = sessionDao.findById(sessionId);
        if (existingSession == null) {
            redirectAttributes.addFlashAttribute("error", "Сеанс не найден.");
            return "redirect:/sessions?playId=" + playId;
        }

        boolean success = sessionDao.buyTickets(sessionId, seatType, count);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Покупка билетов успешно выполнена.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Недостаточно свободных мест выбранного типа.");
        }

        return "redirect:/sessions?playId=" + playId;
    }

    private boolean isValidSeatType(String seatType) {
        return "parterre".equalsIgnoreCase(seatType)
            || "balcony".equalsIgnoreCase(seatType)
            || "mezzanine".equalsIgnoreCase(seatType);
    }
}