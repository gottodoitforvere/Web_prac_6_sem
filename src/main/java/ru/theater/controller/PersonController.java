package ru.theater.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.theater.dao.PersonDao;
import ru.theater.model.Person;
import ru.theater.model.PersonRole;

@Controller
@RequestMapping("/persons")
public class PersonController {

    private final PersonDao personDao;

    @Autowired
    public PersonController(PersonDao personDao) {
        this.personDao = personDao;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("persons", personDao.findAll());
        return "person/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("roles", PersonRole.values());
        return "person/form";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam("id") Long id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Person person = personDao.findById(id);
        if (person == null) {
            redirectAttributes.addFlashAttribute("error", "Персона не найдена.");
            return "redirect:/persons";
        }

        model.addAttribute("person", person);
        model.addAttribute("roles", PersonRole.values());
        return "person/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam("name") String name,
                       @RequestParam("role") PersonRole role,
                       RedirectAttributes redirectAttributes) {

        if (name == null || name.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Имя персоны обязательно.");
            if (id == null) {
                return "redirect:/persons/new";
            }
            return "redirect:/persons/edit?id=" + id;
        }

        Person person;
        if (id == null) {
            person = new Person();
        } else {
            person = personDao.findById(id);
            if (person == null) {
                redirectAttributes.addFlashAttribute("error", "Персона не найдена.");
                return "redirect:/persons";
            }
        }

        person.setName(name.trim());
        person.setRole(role);

        if (id == null) {
            personDao.save(person);
            redirectAttributes.addFlashAttribute("message", "Персона успешно добавлена.");
        } else {
            personDao.update(person);
            redirectAttributes.addFlashAttribute("message", "Данные персоны успешно обновлены.");
        }

        return "redirect:/persons";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id,
                         RedirectAttributes redirectAttributes) {
        Person person = personDao.findById(id);
        if (person == null) {
            redirectAttributes.addFlashAttribute("error", "Персона не найдена.");
            return "redirect:/persons";
        }

        if (personDao.isPersonInUse(id)) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Нельзя удалить персону, потому что она используется в спектаклях."
            );
            return "redirect:/persons";
        }

        personDao.delete(person);
        redirectAttributes.addFlashAttribute("message", "Персона успешно удалена.");
        return "redirect:/persons";
    }
}