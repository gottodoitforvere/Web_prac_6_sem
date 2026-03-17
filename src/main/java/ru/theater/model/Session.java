package ru.theater.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "session")
public class Session {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "play_id", nullable = false)
    private Play play;
    
    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;
    
    @Column(name = "session_time", nullable = false)
    private LocalTime sessionTime;
    
    @Column(name = "free_parterre", nullable = false)
    private Integer freeParterre;
    
    @Column(name = "free_balcony", nullable = false)
    private Integer freeBalcony;
    
    @Column(name = "free_mezzanine", nullable = false)
    private Integer freeMezzanine;
    
    public Session() {
    }
    
    public Session(Play play, LocalDate sessionDate, LocalTime sessionTime,
                   Integer freeParterre, Integer freeBalcony, Integer freeMezzanine) {
        this.play = play;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.freeParterre = freeParterre;
        this.freeBalcony = freeBalcony;
        this.freeMezzanine = freeMezzanine;
    }
    
    public Session(Play play, LocalDate sessionDate, LocalTime sessionTime) {
        this.play = play;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        Theater theater = play.getTheater();
        this.freeParterre = theater.getSeatsParterre();
        this.freeBalcony = theater.getSeatsBalcony();
        this.freeMezzanine = theater.getSeatsMezzanine();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Play getPlay() {
        return play;
    }
    
    public void setPlay(Play play) {
        this.play = play;
    }
    
    public LocalDate getSessionDate() {
        return sessionDate;
    }
    
    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }
    
    public LocalTime getSessionTime() {
        return sessionTime;
    }
    
    public void setSessionTime(LocalTime sessionTime) {
        this.sessionTime = sessionTime;
    }
    
    public Integer getFreeParterre() {
        return freeParterre;
    }
    
    public void setFreeParterre(Integer freeParterre) {
        this.freeParterre = freeParterre;
    }
    
    public Integer getFreeBalcony() {
        return freeBalcony;
    }
    
    public void setFreeBalcony(Integer freeBalcony) {
        this.freeBalcony = freeBalcony;
    }
    
    public Integer getFreeMezzanine() {
        return freeMezzanine;
    }
    
    public void setFreeMezzanine(Integer freeMezzanine) {
        this.freeMezzanine = freeMezzanine;
    }
    
    public Integer getTotalFreeSeats() {
        return freeParterre + freeBalcony + freeMezzanine;
    }
    
    public boolean hasAvailableSeats(String seatType, int count) {
        switch (seatType.toLowerCase()) {
            case "parterre":
                return freeParterre >= count;
            case "balcony":
                return freeBalcony >= count;
            case "mezzanine":
                return freeMezzanine >= count;
            default:
                return false;
        }
    }
    
    public boolean buyTickets(String seatType, int count) {
        if (!hasAvailableSeats(seatType, count)) {
            return false;
        }
        switch (seatType.toLowerCase()) {
            case "parterre":
                freeParterre -= count;
                return true;
            case "balcony":
                freeBalcony -= count;
                return true;
            case "mezzanine":
                freeMezzanine -= count;
                return true;
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", sessionDate=" + sessionDate +
                ", sessionTime=" + sessionTime +
                ", freeParterre=" + freeParterre +
                ", freeBalcony=" + freeBalcony +
                ", freeMezzanine=" + freeMezzanine +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return id != null && id.equals(session.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
