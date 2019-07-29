package com.apploidxxx.entity.queue;


import com.apploidxxx.entity.Chat;
import com.apploidxxx.entity.User;
import com.apploidxxx.entity.dao.queue.QueueService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Arthur Kupriyanov
 */
@Entity
@Table(name = "queue")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Queue {

    public Queue(String name, String fullname){
        this.name = name;
        this.creationDate = new Date();
        this.queueSequence = new LinkedHashSet<>();
        this.chat = new Chat();
        this.fullname = fullname;
    }
    public Queue(String name){
        this(name, name);
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date creationDate;

    @Column
    private GenerationType generationType;

    @Column
    private String password;

    @Id
    @Column(name = "queue_name", unique = true)
    private String name;

    @Column(name = "fullname")
    private String fullname;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="QUEUE_MEMBERS",
            joinColumns = {@JoinColumn(name="queue_name")},
            inverseJoinColumns={@JoinColumn(name="users_id")})
    private Set<User> members;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="QUEUE_SUPER_USERS",
            joinColumns = {@JoinColumn(name="queue_name")},
            inverseJoinColumns={@JoinColumn(name="super_users")})
    private Set<User> superUsers;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private Chat chat;

    @Column(nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> queueSequence;

    @Column
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addSuperUser(User u){
        if (superUsers==null) superUsers= new HashSet<>();
        superUsers.add(u);
    }

    public void deleteSuperUser(User u){
        if (members==null) return;
        superUsers.remove(u);
    }

    public void addUser(User u){
        if (members==null) members= new HashSet<>();
        members.add(u);
        queueSequence.add(u.getId());
    }
    public void deleteUser(User u){
        if (members==null) return;
        members.remove(u);
        queueSequence.remove(u.getId());
    }

    public String getFormattedDate(){
        if (creationDate!=null)
            return new SimpleDateFormat("EEE, d MMM HH:mm").format(creationDate);
        else
            return "Not Stated";
    }

    public String getFormattedDescription(int maxLen){
        String accessType = password==null?
                "<span style='color:green'>[public]</span> ":
                "<span style='color:orange'>[private]</span> ";
        if (description==null) return accessType + "Нету описания";
        else{

            if (description.length()> maxLen){
                return description = description.substring(0, maxLen - 3) + "...";
            }
            return accessType + description;
        }
    }

    public void shuffle(){
        System.err.println("Shuffle queue! " + queueSequence + " " + new Date());
        Collections.shuffle((List<?>) queueSequence);
        System.err.println("after: " + queueSequence);
        new QueueService().updateQueue(this);
    }


    public List<User> getMembersList() {
        Map<Long, User> mapping = new HashMap<>();
        for (User u : members){
            mapping.put(u.getId(), u);
        }
        List<User> users = new LinkedList<>();
        for (Long id : queueSequence){
            users.add(mapping.get(id));
        }
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Queue queue = (Queue) o;
        return Objects.equals(name, queue.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void setGenerationType(String type){
        this.generationType = GenerationType.getType(type);
    }

}
