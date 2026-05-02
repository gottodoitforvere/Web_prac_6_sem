package ru.theater.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.theater.dao.PersonDao;
import ru.theater.dao.PlayDao;
import ru.theater.dao.TheaterDao;
import ru.theater.model.Theater;

import java.time.LocalDate;

@Controller
@RequestMapping("/theaters")
public class TheaterController {

    private final TheaterDao theaterDao;
    private final PersonDao personDao;
    private final PlayDao playDao;

    @Autowired
    public TheaterController(TheaterDao theaterDao, PersonDao personDao, PlayDao playDao) {
        this.theaterDao = theaterDao;
        this.personDao = personDao;
        this.playDao = playDao;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("theaters", theaterDao.findAll());
        model.addAttribute("directors", personDao.findAllDirectors());
        model.addAttribute("actors", personDao.findAllActors());
        return "theater/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(value = "directorId", required = false) Long directorId,
            @RequestParam(value = "actorId", required = false) Long actorId,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        StringBuilder redirect = new StringBuilder("redirect:/plays?filtered=true");
        if (directorId != null) {
            redirect.append("&directorId=").append(directorId);
        }
        if (actorId != null) {
            redirect.append("&actorId=").append(actorId);
        }
        if (date != null) {
            redirect.append("&date=").append(date);
        }
        return redirect.toString();
    }

    @GetMapping("/new")
    public String createForm() {
        return "theater/form";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam("id") Long id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Theater theater = theaterDao.findById(id);
        if (theater == null) {
            redirectAttributes.addFlashAttribute("error", "Театр не найден.");
            return "redirect:/theaters";
        }
        model.addAttribute("theater", theater);
        return "theater/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam("name") String name,
                       @RequestParam("address") String address,
                       @RequestParam("seatsParterre") Integer seatsParterre,
                       @RequestParam("seatsBalcony") Integer seatsBalcony,
                       @RequestParam("seatsMezzanine") Integer seatsMezzanine,
                       RedirectAttributes redirectAttributes) {

        String error = validateTheater(name, address, seatsParterre,
                                       seatsBalcony, seatsMezzanine);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            if (id == null) return "redirect:/theaters/new";
            return "redirect:/theaters/edit?id=" + id;
        }

        Theater theater;
        if (id == null) {
            theater = new Theater();
        } else {
            theater = theaterDao.findById(id);
            if (theater == null) {
                redirectAttributes.addFlashAttribute("error", "Театр не найден.");
                return "redirect:/theaters";
            }
        }

        theater.setName(name.trim());
        theater.setAddress(address.trim());
        theater.setSeatsParterre(seatsParterre);
        theater.setSeatsBalcony(seatsBalcony);
        theater.setSeatsMezzanine(seatsMezzanine);

        if (id == null) {
            theaterDao.save(theater);
            redirectAttributes.addFlashAttribute("message", "Театр успешно добавлен.");
        } else {
            theaterDao.update(theater);
            redirectAttributes.addFlashAttribute("message",
                                                 "Данные театра успешно обновлены.");
        }
        return "redirect:/theaters";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id,
                         RedirectAttributes redirectAttributes) {
        Theater theater = theaterDao.findByIdWithPlays(id);
        if (theater == null) {
            redirectAttributes.addFlashAttribute("error", "Театр не найден.");
            return "redirect:/theaters";
        }
        if (theater.getPlays() != null && !theater.getPlays().isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                "Нельзя удалить театр, потому что у него есть спектакли. " +
                "Сначала удалите спектакли этого театра.");
            return "redirect:/theaters";
        }
        theaterDao.delete(theater);
        redirectAttributes.addFlashAttribute("message", "Театр успешно удалён.");
        return "redirect:/theaters";
    }

    private String validateTheater(String name, String address,
                                   Integer seatsParterre,
                                   Integer seatsBalcony,
                                   Integer seatsMezzanine) {
        if (name == null || name.trim().isEmpty())
            return "Название театра обязательно.";
        if (address == null || address.trim().isEmpty())
            return "Адрес театра обязателен.";
        if (seatsParterre == null || seatsParterre < 0 ||
            seatsBalcony == null || seatsBalcony < 0 ||
            seatsMezzanine == null || seatsMezzanine < 0)
            return "Количество мест должно быть неотрицательным.";
        return null;
    }
}