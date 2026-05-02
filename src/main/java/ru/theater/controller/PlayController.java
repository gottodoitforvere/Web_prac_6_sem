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
import ru.theater.model.Person;
import ru.theater.model.Play;
import ru.theater.model.Theater;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/plays")
public class PlayController {

    private final PlayDao playDao;
    private final TheaterDao theaterDao;
    private final PersonDao personDao;

    @Autowired
    public PlayController(PlayDao playDao, TheaterDao theaterDao, PersonDao personDao) {
        this.playDao = playDao;
        this.theaterDao = theaterDao;
        this.personDao = personDao;
    }

    @GetMapping
    public String list(@RequestParam(value = "theaterId", required = false) Long theaterId,
                    @RequestParam(value = "directorId", required = false) Long directorId,
                    @RequestParam(value = "actorId", required = false) Long actorId,
                    @RequestParam(value = "date", required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                    @RequestParam(value = "filtered", required = false) Boolean filtered,
                    Model model,
                    RedirectAttributes redirectAttributes) {

        List<Play> plays;
        Theater theater = null;

        if (theaterId != null) {
            theater = theaterDao.findById(theaterId);
            if (theater == null) {
                redirectAttributes.addFlashAttribute("error", "Театр не найден.");
                return "redirect:/theaters";
            }
            plays = playDao.findByTheaterIdWithBasicDetails(theaterId);
        } else if (directorId != null || actorId != null || date != null) {
            plays = playDao.findByFiltersWithBasicDetails(null, directorId, actorId, date);
        } else {
            plays = playDao.findAllWithBasicDetails();
        }

        model.addAttribute("plays", plays);
        model.addAttribute("theater", theater);
        model.addAttribute("filtered", filtered != null && filtered);
        model.addAttribute("theaterId", theaterId);
        model.addAttribute("directorId", directorId);
        model.addAttribute("actorId", actorId);
        model.addAttribute("date", date);

        return "play/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam("theaterId") Long theaterId,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Theater theater = theaterDao.findById(theaterId);
        if (theater == null) {
            redirectAttributes.addFlashAttribute("error", "Театр не найден.");
            return "redirect:/theaters";
        }

        model.addAttribute("theater", theater);
        model.addAttribute("directors", personDao.findAllDirectors());
        model.addAttribute("actors", personDao.findAllActors());
        model.addAttribute("selectedActorMap", Collections.emptyMap());

        return "play/form";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam("id") Long id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Play play = playDao.findByIdWithDetails(id);
        if (play == null) {
            redirectAttributes.addFlashAttribute("error", "Спектакль не найден.");
            return "redirect:/theaters";
        }

        model.addAttribute("play", play);
        model.addAttribute("theater", play.getTheater());
        model.addAttribute("directors", personDao.findAllDirectors());
        model.addAttribute("actors", personDao.findAllActors());
        model.addAttribute("selectedActorMap", buildSelectedActorMap(play.getActors()));

        return "play/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam("theaterId") Long theaterId,
                       @RequestParam("title") String title,
                       @RequestParam("directorId") Long directorId,
                       @RequestParam(value = "actorIds", required = false) List<Long> actorIds,
                       @RequestParam("durationMinutes") Integer durationMinutes,
                       @RequestParam("priceParterre") Integer priceParterre,
                       @RequestParam("priceBalcony") Integer priceBalcony,
                       @RequestParam("priceMezzanine") Integer priceMezzanine,
                       RedirectAttributes redirectAttributes) {

        Theater theater = theaterDao.findById(theaterId);
        if (theater == null) {
            redirectAttributes.addFlashAttribute("error", "Театр не найден.");
            return "redirect:/theaters";
        }

        Person director = personDao.findById(directorId);
        List<Person> selectedActors = loadActors(actorIds);

        String error = validatePlay(title, durationMinutes, priceParterre, priceBalcony,
                                    priceMezzanine, director, selectedActors);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            if (id == null) {
                return "redirect:/plays/new?theaterId=" + theaterId;
            }
            return "redirect:/plays/edit?id=" + id;
        }

        Play play;
        if (id == null) {
            play = new Play();
        } else {
            play = playDao.findByIdWithDetails(id);
            if (play == null) {
                redirectAttributes.addFlashAttribute("error", "Спектакль не найден.");
                return "redirect:/plays?theaterId=" + theaterId;
            }
        }

        play.setTitle(title.trim());
        play.setTheater(theater);
        play.setDirector(director);
        play.setDurationMinutes(durationMinutes);
        play.setPriceParterre(priceParterre);
        play.setPriceBalcony(priceBalcony);
        play.setPriceMezzanine(priceMezzanine);
        play.setActors(new HashSet<>(selectedActors));

        if (id == null) {
            playDao.save(play);
            redirectAttributes.addFlashAttribute("message", "Спектакль успешно добавлен.");
        } else {
            playDao.saveOrUpdate(play);
            redirectAttributes.addFlashAttribute("message", "Данные спектакля успешно обновлены.");
        }

        return "redirect:/plays?theaterId=" + theaterId;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id,
                         RedirectAttributes redirectAttributes) {
        Play play = playDao.findByIdWithDetails(id);
        if (play == null) {
            redirectAttributes.addFlashAttribute("error", "Спектакль не найден.");
            return "redirect:/theaters";
        }

        Long theaterId = play.getTheater().getId();
        playDao.delete(play);
        redirectAttributes.addFlashAttribute("message", "Спектакль успешно удалён.");
        return "redirect:/plays?theaterId=" + theaterId;
    }

    private String validatePlay(String title,
                                Integer durationMinutes,
                                Integer priceParterre,
                                Integer priceBalcony,
                                Integer priceMezzanine,
                                Person director,
                                List<Person> actors) {
        if (title == null || title.trim().isEmpty()) {
            return "Название спектакля обязательно.";
        }
        if (director == null || !director.canBeDirector()) {
            return "Выбран некорректный режиссёр.";
        }
        if (durationMinutes == null || durationMinutes <= 0) {
            return "Продолжительность должна быть положительной.";
        }
        if (priceParterre == null || priceParterre < 0 ||
            priceBalcony == null || priceBalcony < 0 ||
            priceMezzanine == null || priceMezzanine < 0) {
            return "Цены билетов должны быть неотрицательными.";
        }
        for (Person actor : actors) {
            if (!actor.canBeActor()) {
                return "В списке актёров есть некорректная персона.";
            }
        }
        return null;
    }

    private List<Person> loadActors(List<Long> actorIds) {
        List<Person> actors = new ArrayList<>();
        if (actorIds == null) {
            return actors;
        }
        for (Long actorId : actorIds) {
            Person actor = personDao.findById(actorId);
            if (actor != null) {
                actors.add(actor);
            }
        }
        return actors;
    }

    private Map<Long, Boolean> buildSelectedActorMap(Set<Person> actors) {
        Map<Long, Boolean> result = new HashMap<>();
        for (Person actor : actors) {
            result.put(actor.getId(), true);
        }
        return result;
    }
}