package ru.theater.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "play")
public class Play {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id", nullable = false)
    private Person director;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @Column(name = "price_parterre", nullable = false)
    private Integer priceParterre;
    
    @Column(name = "price_balcony", nullable = false)
    private Integer priceBalcony;
    
    @Column(name = "price_mezzanine", nullable = false)
    private Integer priceMezzanine;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "play_actor",
        joinColumns = @JoinColumn(name = "play_id"),
        inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Person> actors = new HashSet<>();
    
    @OneToMany(mappedBy = "play", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();
    
    public Play() {
    }
    
    public Play(String title, Theater theater, Person director, 
                Integer durationMinutes, Integer priceParterre,
                Integer priceBalcony, Integer priceMezzanine) {
        this.title = title;
        this.theater = theater;
        this.director = director;
        this.durationMinutes = durationMinutes;
        this.priceParterre = priceParterre;
        this.priceBalcony = priceBalcony;
        this.priceMezzanine = priceMezzanine;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Theater getTheater() {
        return theater;
    }
    
    public void setTheater(Theater theater) {
        this.theater = theater;
    }
    
    public Person getDirector() {
        return director;
    }
    
    public void setDirector(Person director) {
        this.director = director;
    }
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public Integer getPriceParterre() {
        return priceParterre;
    }
    
    public void setPriceParterre(Integer priceParterre) {
        this.priceParterre = priceParterre;
    }
    
    public Integer getPriceBalcony() {
        return priceBalcony;
    }
    
    public void setPriceBalcony(Integer priceBalcony) {
        this.priceBalcony = priceBalcony;
    }
    
    public Integer getPriceMezzanine() {
        return priceMezzanine;
    }
    
    public void setPriceMezzanine(Integer priceMezzanine) {
        this.priceMezzanine = priceMezzanine;
    }
    
    public Set<Person> getActors() {
        return actors;
    }
    
    public void setActors(Set<Person> actors) {
        this.actors = actors;
    }
    
    public List<Session> getSessions() {
        return sessions;
    }
    
    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }
    
    public void addActor(Person actor) {
        actors.add(actor);
        actor.getActedPlays().add(this);
    }
    
    public void removeActor(Person actor) {
        actors.remove(actor);
        actor.getActedPlays().remove(this);
    }
    
    public void addSession(Session session) {
        sessions.add(session);
        session.setPlay(this);
    }
    
    public void removeSession(Session session) {
        sessions.remove(session);
        session.setPlay(null);
    }
    
    public String getFormattedDuration() {
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        if (hours > 0 && minutes > 0) {
            return hours + " ч " + minutes + " мин";
        } else if (hours > 0) {
            return hours + " ч";
        } else {
            return minutes + " мин";
        }
    }
    
    @Override
    public String toString() {
        return "Play{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Play play = (Play) o;
        return id != null && id.equals(play.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
