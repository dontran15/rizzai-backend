package com.nighthawk.spring_portfolio.database.dating.ProfileTypes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.nighthawk.spring_portfolio.database.dating.DatingProfile;
import com.nighthawk.spring_portfolio.database.person.Person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MiscType extends ProfileType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public final Type type = Type.MISC;

    public String jsonString() {
        return "";
    }
}
