package ru.theater.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theater")
public class Theater {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Column(name = "address", nullable = false, length = 300)
    private String address;
    
    @Column(name = "seats_parterre", nullable = false)
    private Integer seatsParterre;
    
    @Column(name = "seats_balcony", nullable = false)
    private Integer seatsBalcony;
    
    @Column(name = "seats_mezzanine", nullable = false)
    private Integer seatsMezzanine;
    
    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Play> plays = new ArrayList<>();
    
    public Theater() {
    }
    
    public Theater(String name, String address, Integer seatsParterre, 
                   Integer seatsBalcony, Integer seatsMezzanine) {
        this.name = name;
        this.address = address;
        this.seatsParterre = seatsParterre;
        this.seatsBalcony = seatsBalcony;
        this.seatsMezzanine = seatsMezzanine;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Integer getSeatsParterre() {
        return seatsParterre;
    }
    
    public void setSeatsParterre(Integer seatsParterre) {
        this.seatsParterre = seatsParterre;
    }
    
    public Integer getSeatsBalcony() {
        return seatsBalcony;
    }
    
    public void setSeatsBalcony(Integer seatsBalcony) {
        this.seatsBalcony = seatsBalcony;
    }
    
    public Integer getSeatsMezzanine() {
        return seatsMezzanine;
    }
    
    public void setSeatsMezzanine(Integer seatsMezzanine) {
        this.seatsMezzanine = seatsMezzanine;
    }
    
    public List<Play> getPlays() {
        return plays;
    }
    
    public void setPlays(List<Play> plays) {
        this.plays = plays;
    }
    
    public void addPlay(Play play) {
        plays.add(play);
        play.setTheater(this);
    }
    
    public void removePlay(Play play) {
        plays.remove(play);
        play.setTheater(null);
    }
    
    public Integer getTotalSeats() {
        return seatsParterre + seatsBalcony + seatsMezzanine;
    }
    
    @Override
    public String toString() {
        return "Theater{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", seatsParterre=" + seatsParterre +
                ", seatsBalcony=" + seatsBalcony +
                ", seatsMezzanine=" + seatsMezzanine +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Theater theater = (Theater) o;
        return id != null && id.equals(theater.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
