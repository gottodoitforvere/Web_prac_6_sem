package ru.theater.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "person")
public class Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private PersonRole role;
    
    @OneToMany(mappedBy = "director")
    private Set<Play> directedPlays = new HashSet<>();
    
    @ManyToMany(mappedBy = "actors")
    private Set<Play> actedPlays = new HashSet<>();
    
    public Person() {
    }
    
    public Person(String name, PersonRole role) {
        this.name = name;
        this.role = role;
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
    
    public PersonRole getRole() {
        return role;
    }
    
    public void setRole(PersonRole role) {
        this.role = role;
    }
    
    public Set<Play> getDirectedPlays() {
        return directedPlays;
    }
    
    public void setDirectedPlays(Set<Play> directedPlays) {
        this.directedPlays = directedPlays;
    }
    
    public Set<Play> getActedPlays() {
        return actedPlays;
    }
    
    public void setActedPlays(Set<Play> actedPlays) {
        this.actedPlays = actedPlays;
    }
    
    public boolean canBeDirector() {
        return role == PersonRole.DIRECTOR || role == PersonRole.BOTH;
    }
    
    public boolean canBeActor() {
        return role == PersonRole.ACTOR || role == PersonRole.BOTH;
    }
    
    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id != null && id.equals(person.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
