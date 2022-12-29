package com.cschurch.server.cs_server;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Bulletin {
    @Id
    private String date;
    private String bulletin1;
    private String bulletin2;
    private String bulletin3;
    private String bulletin4;
    private String bulletin5;
    private String bulletin6;
    private String bulletin7;
    private String bulletin8;

    @Builder
    public Bulletin(String date, String bulletin1, String bulletin2, String bulletin3, String bulletin4,
                    String bulletin5, String bulletin6, String bulletin7, String bulletin8) {
        this.date = date;
        this.bulletin1 = bulletin1;
        this.bulletin2 = bulletin2;
        this.bulletin3 = bulletin3;
        this.bulletin4 = bulletin4;
        this.bulletin5 = bulletin5;
        this.bulletin6 = bulletin6;
        this.bulletin7 = bulletin7;
        this.bulletin8 = bulletin8;
    }
}
